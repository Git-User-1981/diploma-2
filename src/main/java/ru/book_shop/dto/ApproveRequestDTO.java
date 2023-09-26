package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(name = "Подтверждение контакта пользователя")
@Data
public class ApproveRequestDTO {
    @Schema(description = "Телефон или e-mail, для подтверждения", example = "test@test.ru")
    @NotBlank(message = "api.error.confirmation.contact.empty")
    String contact;

    @Schema(description = "Код подтверждения", example = "111 111")
    @NotBlank(message = "api.error.confirmation.code.empty")
    String code;
}
