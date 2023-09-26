package ru.book_shop.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.book_shop.dto.ChangeStatusRequestDTO;
import ru.book_shop.dto.LoginRequestDTO;

import java.util.List;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ApiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private static Cookie[] cookies;

    @Test
    @Order(1)
    void login() throws Exception {
        final LoginRequestDTO loginDto = LoginRequestDTO.builder()
                .contact("test-user@mail.me")
                .code("123456")
                .type("pass")
                .build();

        MvcResult result = mockMvc.perform(
                        post("/api/login")
                                .content(new ObjectMapper().writeValueAsString(loginDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andReturn();

        cookies = result.getResponse().getCookies();
    }

    @Test
    @Order(2)
    void logout() throws Exception {
        mockMvc.perform(get("/logout").cookie(cookies))
                .andDo(print())
                .andExpect(unauthenticated());
    }

    @Test
    @Order(3)
    void addBookToCard() throws Exception {
        final ChangeStatusRequestDTO request = ChangeStatusRequestDTO.builder()
                .status("CART")
                .booksIds(List.of(49, 107))
                .build();

        MvcResult result = mockMvc.perform(
                        post("/api/changeBookStatus")
                                .content(new ObjectMapper().writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.cartCounter").value(2))
                .andReturn();

        cookies = result.getResponse().getCookies();
    }

    @Test
    @Order(4)
    void removeBookFromCard() throws Exception {
        final ChangeStatusRequestDTO request = ChangeStatusRequestDTO.builder()
                .status("UNLINK")
                .booksIds(List.of(107))
                .build();

        mockMvc.perform(
                        post("/api/changeBookStatus")
                                .cookie(cookies)
                                .content(new ObjectMapper().writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.cartCounter").value(1));
    }
}
