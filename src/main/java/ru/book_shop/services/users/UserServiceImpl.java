package ru.book_shop.services.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.book_shop.dto.*;
import ru.book_shop.entities.book.Book;
import ru.book_shop.entities.book.links.Book2User;
import ru.book_shop.entities.enums.Book2UserType;
import ru.book_shop.entities.enums.ContactType;
import ru.book_shop.entities.enums.UserType;
import ru.book_shop.entities.payments.BalanceTransaction;
import ru.book_shop.entities.user.User;
import ru.book_shop.entities.user.UserConfirmation;
import ru.book_shop.entities.user.UserContact;
import ru.book_shop.entities.user.UserSession;
import ru.book_shop.exceptions.ApiCallException;
import ru.book_shop.exceptions.PageCallException;
import ru.book_shop.mappers.BalanceTransactionsMapper;
import ru.book_shop.mappers.UsersMapper;
import ru.book_shop.repositories.*;
import ru.book_shop.security.jwt.JwtService;
import ru.book_shop.security.oauth2.OAuth2UserImpl;
import ru.book_shop.services.sms.SmsService;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Value("${app.auth.jwt.refresh-token.name}")
    String refreshTokenName;

    @Value("${app.cookie.reserved-books}")
    private String reservedBooksCookieName;

    @Value("${app.coolie.max-age}")
    private Integer cookieMaxAge;

    @Value("${app.link.main}")
    private String cookiePath;

    @Value("${spring.mail.username}")
    private String mailSender;

    @Value("${app.code.attempt.count}")
    private int codeAttemptCount;

    @Value("${app.code.attempt.ttl}")
    private int codeAttemptTTL;

    @Value("${app.code.attempt.timeout}")
    private int codeAttemptTimeout;

    @Value("${app.path.books}")
    private String booksPath;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final HttpServletRequest httpRequest;
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final Book2UserRepository book2UserRepository;
    private final BookRepository bookRepository;
    private final UserConfirmationRepository userConfirmationRepository;
    private final UserContactRepository userContactRepository;
    private final BalanceTransactionsRepository balanceTransactionsRepository;
    private final MessageSource messageSource;
    private final JavaMailSender emailSender;
    private final SmsService smsService;

    @Override
    public Integer registerNewUser(RegistrationUserRequestDTO form) {
        if (userRepository.existsAllByContactsContactOrContactsContact(form.getPhone(), form.getMail())) {
            throw new PageCallException("page.error.registration.user-exists");
        }

        User user = new User();
        user.setName(form.getName());
        user.setRole(UserType.USER);
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRegTime(LocalDateTime.now());
        user.setHash(String.valueOf(UUID.randomUUID()));
        user.setContacts(List.of(
                createUserContact(form.getPhone(), ContactType.PHONE, reCheckContact(form.getPhone(), form.getPhoneCode()), user),
                createUserContact(form.getMail(), ContactType.EMAIL, reCheckContact(form.getMail(), form.getMailCode()), user)
        ));

        userRepository.save(user);

        return user.getId();
    }

    @Override
    public void loginOAuthUser(OAuth2UserImpl oAuth2User, HttpServletResponse response) {
        User user = userRepository.findByContactsContactAndContactsType(oAuth2User.getEmail(), ContactType.EMAIL).orElse(null);

        if (user == null) {
            user = new User();
            user.setName(oAuth2User.getName());
            user.setRole(UserType.USER);
            user.setPassword(passwordEncoder.encode(String.valueOf(UUID.randomUUID())));
            user.setRegTime(LocalDateTime.now());
            user.setHash(String.valueOf(UUID.randomUUID()));
            user.setContacts(List.of(
                    createUserContact(oAuth2User.getEmail(), ContactType.EMAIL, 1, user)
            ));

            userRepository.save(user);
        }

        createSession(user, response);
        deleteCookieByName("JSESSIONID", response);
        synchronizeReservedBooksToDB(user.getId());
    }

    private UserContact createUserContact(String userContact, ContactType type, int approved, User user) {
        return UserContact
                .builder()
                .contact(userContact)
                .type(type)
                .approved(approved)
                .user(user)
                .build();
    }

    @Override
    public void login(LoginRequestDTO loginForm, HttpServletResponse response) {
        final User user = userRepository
                .findByContactsContactAndContactsType(
                        loginForm.getContact(),
                        loginForm.getType().equals("phone") ? ContactType.PHONE : ContactType.EMAIL
                )
                .orElseThrow(() -> new ApiCallException("api.error.login.user-not-found"));

        if (loginForm.getType().equals("pass")) {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getId(), loginForm.getCode()));
        }
        else {
            approveContact(loginForm.getContact(), loginForm.getCode());
        }

        createSession(user, response);
        synchronizeReservedBooksToDB(user.getId());
    }

    private void createSession(User user, HttpServletResponse response) {
        final Cookie refreshCookie = jwtService.generateRefreshTokenCookie(user);
        LocalDateTime refreshExpiration = LocalDateTime.now();
        response.addCookie(jwtService.generateTokenCookie(user));
        response.addCookie(refreshCookie);

        userSessionRepository.deleteByExpiredBefore(refreshExpiration);

        final UserSession userSession = UserSession.builder()
                .user(user)
                .refreshToken(refreshCookie.getValue())
                .expired(refreshExpiration.plusSeconds(refreshCookie.getMaxAge()))
                .build();

        userSessionRepository.save(userSession);
    }

    private void synchronizeReservedBooksToDB(Integer userId) {
        book2UserRepository.deleteOldReservedBooks(cookieMaxAge);
        final List<Book2User> userBooks = new ArrayList<>();
        String cookiesBooks = getCookieValueByName(reservedBooksCookieName);

        if (cookiesBooks != null) {
            try {
                cookiesBooks = URLDecoder.decode(cookiesBooks, StandardCharsets.UTF_8);
                final Map<Integer, String> cookies = new ObjectMapper().readValue(cookiesBooks, new TypeReference<>(){});
                cookies.forEach((Integer bookId, String reserveType) -> {
                    final Book2User userBook = book2UserRepository.findByUserIdAndBookId(userId, bookId).orElse(new Book2User());
                    final int typeId = Book2UserType.getIdTypeByCode(reserveType);
                    if (typeId > 0 && userBook.getTypeId() != Book2UserType.PAID.id && userBook.getTypeId() != Book2UserType.ARCHIVED.id) {
                        setBookToUser(userBook, userId, bookId, typeId);
                        userBooks.add(userBook);
                    }
                });
                book2UserRepository.saveAll(userBooks);
            }
            catch (JsonProcessingException e) {
                log.warn("Can't synchronize ReservedBooks cookie: " + cookiesBooks, e);
            }
        }
    }

    @Override
    public User getCurrentUser(Principal principal) {
        return userRepository.findById(Integer.valueOf(principal.getName())).orElse(null);
    }

    @Override
    public String getCookieValueByName(String cookieName) {
        return Optional.ofNullable(httpRequest.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies).filter(c -> c.getName().equals(cookieName)).findFirst())
                .map(Cookie::getValue)
                .orElse(null);
    }

    @Override
    public Cookie getNewAccessTokenCookieByRefreshToken() {
        final String refreshToken = getCookieValueByName(refreshTokenName);
        if (refreshToken != null && jwtService.isTokenNotExpired(refreshToken)) {
            final UserSession userSession = userSessionRepository.findByRefreshToken(refreshToken).orElse(null);
            if (userSession != null) {
                return jwtService.generateTokenCookie(userSession.getUser());
            }
        }
        return null;
    }

    @Override
    public int[] getUserBooksCount() {
        final int [] counter = new int[3];
        final Principal user = httpRequest.getUserPrincipal();
        if (user != null) {
            book2UserRepository.getUserBooksCount(Integer.valueOf(user.getName())).forEach(booksCount -> {
                if (booksCount.getType().equals(Book2UserType.KEPT.name())) {
                    counter[0] = booksCount.getNum();
                }
                else if (booksCount.getType().equals(Book2UserType.CART.name())) {
                    counter[1] = booksCount.getNum();
                }
                else if (booksCount.getType().equals(Book2UserType.PAID.name()) || booksCount.getType().equals(Book2UserType.ARCHIVED.name())) {
                    counter[2] += booksCount.getNum();
                }
                else {
                    log.warn("Unknown reserve book type id: " + booksCount.getType());
                }
            });
        }
        return counter;
    }

    @Override
    public Map<Integer, String> getUserBooks() {
        final Principal user = httpRequest.getUserPrincipal();
        if (user == null) {
            return new HashMap<>();
        }
        return book2UserRepository
                .getUserBooks(Integer.valueOf(user.getName()))
                .stream()
                .collect(Collectors.toMap(IUserBooksCommonDTO::getNum, IUserBooksCommonDTO::getType, (k, v) -> v, LinkedHashMap::new));
    }

    @Override
    public void deleteCookieByName(String name, HttpServletResponse response) {
        final Cookie cookie = new Cookie(name, null);
        cookie.setPath(cookiePath);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    @Override
    public ChangeBookStatusDTO changeBookStatus(List<Integer> booksIds, String status) {
        final int userId = Integer.parseInt(httpRequest.getUserPrincipal().getName());
        final int typeId = Book2UserType.getIdTypeByCode(status);

        for (Integer bookId : booksIds) {
            final Book2User userBook = book2UserRepository.findByUserIdAndBookId(userId, bookId).orElse(new Book2User());

            if (status.equals("UNLINK")) {
                if (userBook.getId() > 0 && userBook.getTypeId() != Book2UserType.PAID.id && userBook.getTypeId() != Book2UserType.ARCHIVED.id) {
                    book2UserRepository.delete(userBook);
                }
            }
            else if (typeId > 0) {
                setBookToUser(userBook, userId, bookId, typeId);
                book2UserRepository.save(userBook);
            }
        }

        int[] booksCount = getUserBooksCount();
        final ChangeBookStatusDTO result = new ChangeBookStatusDTO();
        result.setKeptCounter(booksCount[0]);
        result.setCartCounter(booksCount[1]);

        return result;
    }

    @Override
    public String profileSave(Integer userId, ProfileSaveRequestDTO form) {
        User user = userRepository.findById(userId).orElseThrow(() -> new PageCallException("api.error.login.user-not-found"));

        boolean changed = false;

        if (!form.getPassword().isEmpty()) {
            if (!form.getPassword().equals(form.getPasswordReply())) {
                return messageSource.getMessage("page.error.profile.passwords-not-match", null, LocaleContextHolder.getLocale());
            }

            String newPassword = passwordEncoder.encode(form.getPassword());

            if (!user.getPassword().equals(newPassword)) {
                user.setPassword(newPassword);
                changed = true;
            }
        }

        if (!user.getName().equals(form.getName())) {
            user.setName(form.getName());
            changed = true;
        }

        if (updateUserContact(user, form.getMail(), ContactType.EMAIL)) {
            changed = true;
        }

        if (updateUserContact(user, form.getPhone(), ContactType.PHONE)) {
            changed = true;
        }

        if (changed) {
            userRepository.save(user);
            return messageSource.getMessage("page.profile.main.form.save-message", null, LocaleContextHolder.getLocale());
        }

        return null;
    }

    @Override
    public boolean buyBooks(Integer userId) {
        final User user = userRepository.findById(userId).orElseThrow(() -> new PageCallException("api.error.login.user-not-found"));
        final int totalAmount = book2UserRepository.getCartTotalAmount(userId);

        if (totalAmount > user.getBalance()) {
            return false;
        }

        final List<Book2User> booksInCart = book2UserRepository.findAllByUserIdAndTypeId(userId, Book2UserType.CART.id);

        for (Book2User b2u : booksInCart) {
            final Book book = b2u.getBook();
            final int discount = book.getDiscount();
            int price = book.getPrice();
            price = discount > 0 ? price - (price * discount / 100) : price;

            b2u.setTypeId(Book2UserType.PAID.id);
            b2u.setTime(LocalDateTime.now());

            BalanceTransaction transaction = BalanceTransaction
                    .builder()
                    .userId(userId)
                    .bookId(book.getId())
                    .time(LocalDateTime.now())
                    .value(-price)
                    .description(messageSource.getMessage(
                            "page.profile.transactions.buy",
                            new Object[]{booksPath + book.getSlug(), book.getTitle()},
                            LocaleContextHolder.getLocale()
                    ))
                    .build();
            balanceTransactionsRepository.save(transaction);
        }

        book2UserRepository.saveAll(booksInCart);

        user.setBalance(user.getBalance() - totalAmount);
        userRepository.save(user);

        return true;
    }

    @Override
    public BalanceTransactionPageDTO getUserTransactions(Integer userId, int offset, Integer limit, String sort) {
        return BalanceTransactionsMapper.toDTO(
                balanceTransactionsRepository.getBalanceTransactions(
                        userId,
                        PageRequest.of(
                                offset,
                                limit,
                                sort.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                                "time"
                        )
                )
        );
    }

    @Override
    public String getUserHash(Integer userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new PageCallException("api.error.login.user-not-found"))
                .getHash();
    }

    @Override
    public UsersPageDTO getUsersSearchResult(String query, Integer offset, Integer limit) {
        return UsersMapper.toUsersPageDTO(userRepository.getUsersList(query, PageRequest.of(offset, limit)));
    }

    @Override
    public void setRoleAdm(Integer userId) {
        final User user = userRepository.findById(userId).orElseThrow(() -> new PageCallException("api.error.login.user-not-found"));
        user.setRole(UserType.ADMIN);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Integer userId) {
        final User user = userRepository.findById(userId).orElseThrow(() -> new PageCallException("api.error.login.user-not-found"));
        userRepository.delete(user);
    }

    @Transactional
    @Override
    public void giveBookToUser(Integer userId, String bookSlug) {
        final User user = userRepository.findById(userId).orElseThrow(() -> new PageCallException("api.error.login.user-not-found"));
        final Book book = bookRepository.findBySlug(bookSlug).orElseThrow(() -> new PageCallException("page.error.book.not-found"));
        Book2User book2User = book2UserRepository.findByUserIdAndBookId(userId, book.getId()).orElse(null);

        if (book2User == null) {
            book2User = Book2User
                    .builder()
                    .userId(user.getId())
                    .book(book)
                    .time(LocalDateTime.now())
                    .build();
        }
        else if (book2User.getTypeId() == Book2UserType.PAID.id) {
            throw new PageCallException("page.error.users.have-book");
        }

        book2User.setTypeId(Book2UserType.PAID.id);
        book2UserRepository.save(book2User);

        balanceTransactionsRepository.save(
            BalanceTransaction
                .builder()
                .userId(user.getId())
                .bookId(book.getId())
                .value(0)
                .description(messageSource.getMessage(
                    "page.profile.transactions.gift",
                    new Object[]{booksPath + book.getSlug(), book.getTitle()},
                    LocaleContextHolder.getLocale()
                ))
                .time(LocalDateTime.now())
                .build()
        );
    }

    @Override
    public ConfirmationResponseDTO contactConfirmation(String contact, String type) {
        try {
            if (type.startsWith("mail")) {
                sendConfirmationEmail(contact, getConfirmationCode(contact, type.endsWith("-link")));
            }
            else {
                sendConfirmationSms(contact, getConfirmationCode(contact, type.endsWith("-link")));
            }
        }
        catch (ApiCallException e) {
            if (e.getMessage().equals("api.error.confirmation.code.attempt-count")) {
                throw e;
            }
        }

        String responseKey = type.startsWith("mail") ? "api.confirmation.send.mail" : "api.confirmation.send.phone";
        return ConfirmationResponseDTO
                .builder()
                .result(true)
                .message(messageSource.getMessage(responseKey, null, LocaleContextHolder.getLocale()))
                .build();
    }

    @Override
    public String approveProfileContact(String hash, Integer userId) {
        final UserConfirmation confirmation = userConfirmationRepository
                .findByHashAndCodeTrailsLessThan(hash, codeAttemptCount)
                .orElseThrow(() -> new PageCallException("api.error.confirmation.code.not-valid"));

        final UserContact userContact = userContactRepository
                .findByContactAndUserId(confirmation.getContact(), userId)
                .orElseThrow(() -> new PageCallException("api.error.confirmation.code.someone-else"));

        userContact.setApproved(1);

        userConfirmationRepository.delete(confirmation);

        return messageSource.getMessage("api.confirmation.success", null, LocaleContextHolder.getLocale());
    }

    @Override
    @Transactional(noRollbackFor = ApiCallException.class)
    public ConfirmationResponseDTO approveContact(String contact, String code) {
        final UserConfirmation confirmation = userConfirmationRepository
                .findByContactAndCodeTimeAfter(contact, LocalDateTime.now().minusMinutes(codeAttemptTTL))
                .orElseThrow(() -> new ApiCallException("api.error.confirmation.code.not-valid"));

        final int codeTrails = confirmation.getCodeTrails();
        confirmation.setCodeTrails(codeTrails + 1);
        userConfirmationRepository.saveAndFlush(confirmation);

        if (confirmation.getCodeTrails() > codeAttemptCount) {
            throw new ApiCallException("api.error.confirmation.code.attempt-count");
        }

        if (!confirmation.getCode().equals(code)) {
            throw new ApiCallException("api.error.confirmation.code.wrong");
        }

        return ConfirmationResponseDTO
                .builder()
                .result(true)
                .message(messageSource.getMessage("api.confirmation.success", null, LocaleContextHolder.getLocale()))
                .build();
    }

    @Override
    @Transactional(noRollbackFor = ApiCallException.class)
    public String getConfirmationCode(String contact, boolean isLink) {
        userConfirmationRepository.deleteAllByCodeTimeBefore(LocalDateTime.now().minusMinutes(codeAttemptTTL));

        UserConfirmation confirmation = userConfirmationRepository.findByContact(contact).orElse(null);

        if (confirmation == null) {
            confirmation = UserConfirmation
                    .builder()
                    .contact(contact)
                    .hash(String.valueOf(UUID.randomUUID()))
                    .build();
        }
        else if (confirmation.getCodeTime().plusMinutes(codeAttemptTimeout).isAfter(LocalDateTime.now()) &&
                 confirmation.getCodeTrails() >= codeAttemptCount) {
            throw new ApiCallException("api.error.confirmation.code.attempt-count");
        }
        else if (confirmation.getCodeTime().plusMinutes(codeAttemptTimeout).isAfter(LocalDateTime.now())) {
            throw new ApiCallException("api.error.confirmation.code.attempt");
        }

        confirmation.setCode(generateConfirmationCode());
        confirmation.setCodeTrails(0);
        confirmation.setCodeTime(LocalDateTime.now());
        userConfirmationRepository.saveAndFlush(confirmation);

        return isLink ? "http://localhost:8085/approveProfileContact/" + confirmation.getHash() : confirmation.getCode();
    }

    private int reCheckContact(String contact, String code) {
        final UserConfirmation confirmation = userConfirmationRepository
                .findByContact(contact)
                .orElseThrow(() -> new PageCallException("api.error.confirmation.code.not-valid"));

        if (!confirmation.getCode().equals(code)) {
            throw new PageCallException("api.error.confirmation.code.wrong");
        }

        userConfirmationRepository.delete(confirmation);

        return 1;
    }

    private void setBookToUser(Book2User userBook, Integer userId, Integer bookId, int typeId) {
        Book book = userBook.getBook();
        if (book == null) {
            book = new Book();
            book.setId(bookId);
        }
        userBook.setUserId(userId);
        userBook.setBook(book);
        userBook.setTypeId(typeId);
        userBook.setTime(LocalDateTime.now());
    }

    private String generateConfirmationCode() {
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder sb = new StringBuilder();
            while (sb.length() < 6) {
                sb.append(random.nextInt(9));
            }
            sb.insert(3, " ");
            return sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            throw new ApiCallException("api.error.confirmation.code.generation", e);
        }
    }

    private void sendConfirmationEmail(String contact, String confirmation) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailSender);
        message.setTo(contact);
        message.setSubject(messageSource.getMessage("api.confirmation.mail.title", null, LocaleContextHolder.getLocale()));
        message.setText(messageSource.getMessage("api.confirmation.mail.text", new Object[]{confirmation}, LocaleContextHolder.getLocale()));

        try {
            emailSender.send(message);
        }
        catch (MailException e) {
            throw new ApiCallException("api.error.confirmation.mail.send", e);
        }
    }

    private void sendConfirmationSms(String contact, String confirmation) {
        smsService.sendSms(
            contact,
            messageSource.getMessage("api.confirmation.phone.text", new Object[]{confirmation}, LocaleContextHolder.getLocale())
        );
    }

    private boolean updateUserContact(User user, String contact, ContactType contactType) {
        final UserContact userContact = user
                .getContacts()
                .stream()
                .filter(uc -> uc.getType().equals(contactType))
                .findFirst()
                .orElse(null);

        if (userContact == null) {
            user.getContacts().add(
                    UserContact
                            .builder()
                            .contact(contact)
                            .type(contactType)
                            .approved(0)
                            .user(user)
                            .build()
            );
            return true;
        }
        else if (!userContact.getContact().equals(contact)) {
            userContact.setContact(contact);
            userContact.setApproved(0);
            return true;
        }
        return false;
    }
}
