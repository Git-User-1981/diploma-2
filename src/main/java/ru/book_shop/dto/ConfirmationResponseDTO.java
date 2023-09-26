package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "Ответ на подтверждение контакта пользователя")
@Setter
@Getter
@Builder
public class ConfirmationResponseDTO {
    @Schema(description = "Результат выполнения", example = "true")
    private boolean result;

    @Schema(description = "Текст сообщения об успешном подтверждении контакта пользователя", example = "Контакт подтвержден")
    private String message;
}
