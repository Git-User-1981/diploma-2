package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(name = "Запрос на подтверждение контакта пользователя")
@Data
public class ConfirmationRequestDTO {
    @Schema(description = "Телефон или e-mail, на который нужно отправить код подтверждения", example = "test@test.ru")
    @NotBlank(message = "api.error.confirmation.contact.empty")
    String contact;

    @Schema(description = "Тип контакта, phone или mail", example = "mail")
    @NotBlank(message = "api.error.confirmation.type.empty")
    String type;
}
