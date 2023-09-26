package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Schema(name = "Запрос локальной авторизации пользователя")
@Builder
@Data
public class LoginRequestDTO {
    @Schema(description = "Контакт пользователя", example = "mail@example.com")
    @NotNull(message = "api.error.login.contact")
    private String contact;

    @Schema(description = "Код подтверждения или пароль", example = "12345")
    @NotNull(message = "api.error.login.code")
    private String code;

    @Schema(description = "Тип авторизации", example = "mailtype")
    @NotNull(message = "api.error.login.type")
    private String type;
}
