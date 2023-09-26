package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "Ответ на изменение оценки книги пользователем")
@Setter
@Getter
@Builder
public class BookUserRateResponseDTO {
    @Schema(description = "Результат выполнения", example = "true")
    private boolean result;

    @Schema(description = "Текст сообщения об успешном изменении оценки книги пользователем", example = "Рейтинг успешно изменен")
    private String message;
}
