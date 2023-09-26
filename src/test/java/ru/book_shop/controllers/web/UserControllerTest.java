package ru.book_shop.controllers.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.book_shop.entities.user.UserConfirmation;
import ru.book_shop.repositories.UserConfirmationRepository;
import ru.book_shop.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UserRepository userRepository;

    UserConfirmationRepository userConfirmationRepository;

    @Autowired
    UserControllerTest(UserConfirmationRepository userConfirmationRepository) {
        this.userConfirmationRepository = userConfirmationRepository;
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

    @Test
    void registration() throws Exception {
        generateConfirmationCode("+7 (123) 456-78-90");
        generateConfirmationCode("test@test.com");

        mockMvc.perform(
                    post("/registration")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "TestUser")
                        .param("mail", "test@test.com")
                        .param("mailCode", "111 111")
                        .param("phone", "+7 (123) 456-78-90")
                        .param("phoneCode", "111 111")
                        .param("password", "12345678")
                )
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signin"));
    }
}
