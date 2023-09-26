package ru.book_shop.services.users;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import ru.book_shop.dto.LoginRequestDTO;
import ru.book_shop.dto.RegistrationUserRequestDTO;
import ru.book_shop.entities.user.UserConfirmation;
import ru.book_shop.exceptions.PageCallException;
import ru.book_shop.repositories.UserConfirmationRepository;
import ru.book_shop.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {
    private final UserRepository userRepository;
    private final UserConfirmationRepository userConfirmationRepository;
    private final UserService userService;
    private static RegistrationUserRequestDTO registerDto;
    private static Integer newUserId;

    @Autowired
    UserServiceTest(UserRepository userRepository, UserConfirmationRepository userConfirmationRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userConfirmationRepository = userConfirmationRepository;
        this.userService = userService;
    }

    void generateConfirmationCode(String contact) {
        UserConfirmation confirmation = userConfirmationRepository.findByContact(contact).orElse(null);

        if (confirmation == null) {
            confirmation = UserConfirmation
                    .builder()
                    .contact(contact)
                    .hash(String.valueOf(UUID.randomUUID()))
                    .build();
        }

        confirmation.setCode("111 111");
        confirmation.setCodeTrails(0);
        confirmation.setCodeTime(LocalDateTime.now());
        userConfirmationRepository.saveAndFlush(confirmation);
    }


    @BeforeAll
    void setUp() {
        generateConfirmationCode("+7 (123) 456-78-90");
        generateConfirmationCode("test@test.com");

        registerDto = RegistrationUserRequestDTO.builder()
                .name("User")
                .mail("test@test.com")
                .mailCode("111 111")
                .phone("+7 (123) 456-78-90")
                .phoneCode("111 111")
                .password("12345678")
                .build();
    }

    @AfterAll
    void tearDown() {
        if (newUserId != null) {
            userRepository.deleteById(newUserId);
        }
    }

    @Test
    @Order(1)
    void registerNewUser() {
        newUserId = userService.registerNewUser(registerDto);
        assertTrue(newUserId > 0);
    }

    @Test
    @Order(2)
    void registerNewUserFail() {
        final Exception exception = assertThrows(PageCallException.class, () -> userService.registerNewUser(registerDto));
        assertTrue(exception.getMessage().contains("registration.user-exists"));
    }

    @Test
    @Order(3)
    void login() {
        final LoginRequestDTO loginForm = LoginRequestDTO.builder()
                .contact("test@test.com")
                .code("12345678")
                .type("pass")
                .build();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        userService.login(loginForm, response);

        assertNotNull(response.getCookie("token"));
        assertNotNull(response.getCookie("refresh-token"));
    }

    @Test
    @Order(4)
    void loginFail() {
        final LoginRequestDTO loginForm = LoginRequestDTO.builder()
                .contact("test@test.com")
                .code("wrong_password")
                .type("pass")
                .build();
        final MockHttpServletResponse response = new MockHttpServletResponse();

        Exception exception = assertThrows(BadCredentialsException.class, () -> userService.login(loginForm, response));

        assertEquals("BadCredentialsException", exception.getClass().getSimpleName());
    }
}
