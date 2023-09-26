package ru.book_shop.services.books;

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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.book_shop.dto.*;
import org.springframework.stereotype.Service;
import ru.book_shop.entities.author.Author;
import ru.book_shop.entities.book.file.BookFile;
import ru.book_shop.entities.book.file.BookFileType;
import ru.book_shop.entities.book.links.Book2Author;
import ru.book_shop.entities.tags.Tag;
import ru.book_shop.exceptions.ApiCallException;
import ru.book_shop.exceptions.PageCallException;
import ru.book_shop.mappers.BooksMapper;
import ru.book_shop.entities.book.Book;
import ru.book_shop.repositories.AuthorRepository;
import ru.book_shop.repositories.BookRepository;
import ru.book_shop.repositories.BooksFilesRepository;
import ru.book_shop.repositories.TagRepository;
import ru.book_shop.services.authors.AuthorsService;
import ru.book_shop.services.storage.FileStorageService;
import ru.book_shop.services.storage.StorageType;
import ru.book_shop.services.users.UserService;

import org.hibernate.exception.ConstraintViolationException;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BooksServiceImpl implements BooksService {
    @Value("${app.coolie.max-age}")
    private Integer cookieMaxAge;

    @Value("${app.link.main}")
    private String cookiePath;

    @Value("${app.cookie.reserved-books}")
    private String reservedBooksCookieName;

    private final MessageSource messageSource;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final TagRepository tagRepository;
    private final BooksFilesRepository booksFilesRepository;
    private final UserService userService;
    private final BooksMapper mapper;
    private final AuthorsService authorsService;
    private final FileStorageService storageService;
    private final HttpServletRequest httpRequest;

    @Override
    public BookCardsPageDTO getRecommendedBooks(Integer offset, Integer limit) {
        final Map<Integer, String> userBooks = getReservedBooks();
        mapper.setReservedBooks(userBooks);

        if (userBooks.isEmpty()) {
            return mapper.toBookCardsDTO(bookRepository.getRecommendedBooks(PageRequest.of(offset, limit)));
        }

        final Set<Integer> userBookIds = userBooks.keySet();
        return mapper.toBookCardsDTO(bookRepository.getRecommendedBooksUser(userBookIds, PageRequest.of(offset, limit)));
    }

    @Override
    public BookCardsPageDTO getRecentBooks(LocalDate from, LocalDate to, Integer offset, Integer limit) {
        if (from == null) {
            from = LocalDate.parse("0000-01-01");
        }
        if (to == null) {
            to = LocalDate.now();
        }
        mapper.setReservedBooks(getReservedBooks());
        return mapper.toBookCardsDTO(bookRepository.getRecentBooks(from, to, PageRequest.of(offset, limit)));
    }

    @Override
    public BookCardsPageDTO getPopularBooks(Integer offset, Integer limit) {
        mapper.setReservedBooks(getReservedBooks());
        return mapper.toBookCardsDTO(bookRepository.getPopularBooks(PageRequest.of(offset, limit)));
    }

    @Override
    public BookCardsPageDTO getViewsBooks(Integer offset, Integer limit) {
        mapper.setReservedBooks(getReservedBooks());
        return mapper.toBookCardsDTO(bookRepository.getViewsBooks(Integer.valueOf(httpRequest.getUserPrincipal().getName()), PageRequest.of(offset, limit)));
    }

    @Override
    public BookCardsPageDTO getBooksByTag(Integer tagId, Integer offset, Integer limit) {
        mapper.setReservedBooks(getReservedBooks());
        return mapper.toBookCardsDTO(bookRepository.getBooksByTagId(tagId, PageRequest.of(offset, limit)));
    }

    @Override
    public BookCardsPageDTO getBooksByGenre(Integer genreId, Integer offset, Integer limit) {
        mapper.setReservedBooks(getReservedBooks());
        return mapper.toBookCardsDTO(bookRepository.getBooksByGenreId(genreId, PageRequest.of(offset, limit)));
    }

    @Override
    public BookCardsPageDTO getBooksByAuthor(Integer authorId, Integer offset, Integer limit) {
        mapper.setReservedBooks(getReservedBooks());
        return mapper.toBookCardsDTO(bookRepository.getBooksByAuthorId(authorId, PageRequest.of(offset, limit)));
    }

    @Override
    public BookCardsPageDTO getBooksSearchResult(String query, Integer offset, Integer limit) {
        mapper.setReservedBooks(getReservedBooks());
        return mapper.toBookCardsDTO(bookRepository.getBooksSearchResult(query, PageRequest.of(offset, limit)));
    }

    @Override
    public BookPageDTO getBook(String slug) {
        mapper.setReservedBooks(getReservedBooks());
        final BookPageDTO book = mapper.toPageDTO(bookRepository.getBookBySlug(slug).orElseThrow(() -> new PageCallException("page.error.book.not-found")));
        book.setAuthors(authorsService.getAuthorsByBookId(book.getId()));

        final Principal user = httpRequest.getUserPrincipal();
        if (user != null) {
            book.setUserRating(bookRepository.getBookUserRating(Integer.valueOf(user.getName()), book.getId()).orElse(0));
        }

        return book;
    }

    @Override
    public void saveCover(String slug, MultipartFile coverFile) {
        final Book book = bookRepository.findBySlug(slug).orElseThrow(() -> new PageCallException("page.error.book.not-found"));
        book.setImage(storageService.saveFile(coverFile, StorageType.COVER));
        bookRepository.save(book);
    }

    @Override
    public ChangeBookStatusDTO changeBookStatus(List<Integer> booksIds, String status, HttpServletResponse response) {
        final Map<Integer, String> reservedBooks = getReservedBooks();

        for (Integer bookId : booksIds) {
            if (status.equals("UNLINK")) {
                reservedBooks.remove(bookId);
            }
            else {
                reservedBooks.put(bookId, status);
            }
        }

        String booksStatuses = null;
        int keptCounter = 0;
        int cartCounter = 0;

        if (!reservedBooks.isEmpty()) {
            try {
                booksStatuses = new ObjectMapper().writeValueAsString(reservedBooks);
                booksStatuses = URLEncoder.encode(booksStatuses, StandardCharsets.UTF_8);

                for (String reserveType : reservedBooks.values()) {
                    switch (reserveType) {
                        case "KEPT" -> keptCounter++;
                        case "CART" -> cartCounter++;
                        default -> log.warn("Unknown reserve book type: " + reserveType);
                    }
                }
            }
            catch (JsonProcessingException e) {
                throw new ApiCallException("api.error.status.change-status");
            }
        }

        final Cookie cookie = new Cookie(reservedBooksCookieName, booksStatuses);
        cookie.setPath(cookiePath);
        cookie.setMaxAge(booksStatuses == null ? 0 : cookieMaxAge);
        response.addCookie(cookie);

        final ChangeBookStatusDTO result = new ChangeBookStatusDTO();
        result.setKeptCounter(keptCounter);
        result.setCartCounter(cartCounter);

        return result;
    }

    @Override
    public List<BookPageDTO> getBooksByStatus(String status, List<String> totals) {
        final List<BookPageDTO> dtoList = new ArrayList<>();
        int totalPrice = 0;
        int totalOldPrice = 0;
        final List<Integer> booksIdsList = new ArrayList<>();
        final Map<Integer, String> reservedBooks = getReservedBooks();
        final List<Integer> booksIds = reservedBooks.entrySet()
                .stream()
                .filter(map -> map.getValue().equals(status))
                .map(Map.Entry::getKey).toList();

        if (!booksIds.isEmpty()) {
            mapper.setReservedBooks(reservedBooks);
            final List<IBookPageDTO> listIBooks = bookRepository.getBooksByIds(booksIds);
            for (IBookPageDTO iBook : listIBooks) {
                BookPageDTO book = mapper.toPageDTO(iBook);
                book.setAuthors(authorsService.getAuthorsByBookId(book.getId()));

                totalPrice += book.getPrice();
                totalOldPrice += book.getDiscountPrice();
                booksIdsList.add(book.getId());

                dtoList.add(book);
            }
        }

        totals.add(Integer.toString(totalPrice));
        totals.add(Integer.toString(totalOldPrice));

        try {
            totals.add(new ObjectMapper().writeValueAsString(booksIdsList));
        }
        catch (JsonProcessingException e) {
            totals.add("[]");
            log.warn("Cant parse booksIdsList: " + booksIdsList, e);
        }

        return dtoList;
    }

    @Override
    public List<BookCardDTO> getUserBooksByStatus(String status) {
        final Principal user = httpRequest.getUserPrincipal();
        if (user == null) {
            return new ArrayList<>();
        }

        mapper.setReservedBooks(getReservedBooks());
        return bookRepository.getUserBooksByStatus(Integer.valueOf(user.getName()), status)
                .stream()
                .map(mapper::toBookCardDTO)
                .toList();
    }

    @Override
    public BookRatingStatDTO getRatingStat(Integer bookId) {
        return mapper.toBookRatingStatDTO(bookRepository.getBookRatingStat(bookId));
    }

    @Override
    public Map<Integer, String> getReservedBooks() {
        if (httpRequest.getUserPrincipal() != null) {
            return userService.getUserBooks();
        }

        String cookiesBooks = Optional.ofNullable(httpRequest.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies).filter(c -> c.getName().equals(reservedBooksCookieName)).findFirst())
                .map(Cookie::getValue)
                .orElse(null);

        if (cookiesBooks != null) {
            try {
                cookiesBooks = URLDecoder.decode(cookiesBooks, StandardCharsets.UTF_8);
                return new ObjectMapper().readValue(cookiesBooks, new TypeReference<>(){});
            }
            catch (JsonProcessingException e) {
                log.warn("Can't parse ReservedBooks cookie: " + cookiesBooks, e);
            }
        }

        return new HashMap<>();
    }

    @Transactional
    @Override
    public BookUserRateResponseDTO setBookRating(Integer bookId, Integer rating) {
        try {
            bookRepository.setBookRating(bookId, Integer.valueOf(httpRequest.getUserPrincipal().getName()), rating);
        }
        catch (DataIntegrityViolationException e) {
            String constraint = ((ConstraintViolationException) e.getCause()).getConstraintName();
            throw new ApiCallException(constraint.equals("books_ratings_uk") ? "api.error.book.rating.already" : "page.error.book.not-found", e);
        }
        return BookUserRateResponseDTO
                .builder()
                .result(true)
                .message(messageSource.getMessage("api.book.rating.changed-successfully", null, LocaleContextHolder.getLocale()))
                .build();
    }

    @Override
    public String addBook(String title) {
        final Book book = Book
                .builder()
                .title(title)
                .slug(UUID.randomUUID().toString())
                .price(0)
                .isBestseller((short) 0)
                .pubDate(LocalDate.now())
                .build();

        bookRepository.save(book);
        book.setSlug("book-" + book.getId());
        bookRepository.save(book);

        return book.getSlug();
    }

    @Override
    public void deleteBook(String slug) {
        final Book book = bookRepository.findBySlug(slug).orElseThrow(() -> new PageCallException("page.error.book.not-found"));
        bookRepository.delete(book);
    }

    @Override
    public String saveBook(BookEditRequestDTO form) {
        final Book book = bookRepository.findById(form.getBook()).orElseThrow(() -> new PageCallException("page.error.book.not-found"));
        mapper.toBookEntity(book, form);

        book.getBook2Authors().removeAll(book.getBook2Authors());
        book.getBook2Tags().removeAll(book.getBook2Tags());
        bookRepository.save(book);

        int index = 0;
        for (Integer authorId: form.getAuthors()) {
            final Author author = authorRepository.findById(authorId).orElseThrow(() -> new PageCallException("page.error.author.not-found"));
            book.getBook2Authors().add(
                Book2Author
                    .builder()
                    .author(author)
                    .book(book)
                    .sortIndex(index++)
                    .build()
            );
        }
        for (Integer tagId: form.getTags()) {
            final Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new PageCallException("page.error.tag.not-found"));
            book.getBook2Tags().add(tag);
        }
        bookRepository.save(book);

        return book.getSlug();
    }

    @Override
    public void saveBookFile(String slug, MultipartFile file, String bookFileTypeName) {
        final Book book = bookRepository.findBySlug(slug).orElseThrow(() -> new PageCallException("page.error.book.not-found"));
        final Integer bookFileTypeId = booksFilesRepository
                .getFileTypeIdByName(bookFileTypeName)
                .orElseThrow(() -> new PageCallException("page.error.book.file-type-not-found"));
        final BookFile bookFile = booksFilesRepository
                .getBookFileByBookAndFileTypeId(book, bookFileTypeId)
                .orElse(BookFile.builder().book(book).fileType(BookFileType.builder().id(bookFileTypeId).build()).build());

        bookFile.setPath(storageService.saveFile(file, StorageType.FILE));
        bookFile.setHash(UUID.randomUUID().toString());
        booksFilesRepository.save(bookFile);
    }
}
