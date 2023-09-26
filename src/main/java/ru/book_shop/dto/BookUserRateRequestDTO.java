package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(name = "Запрос на сохранение пользовательской оценки книги")
@Data
public class BookUserRateRequestDTO {
    @Schema(description = "Идентификатор книги, на которую отправляется оценка", example = "1111")
    @Min(value = 1, message = "api.error.book.id")
    @NotNull(message = "api.error.book.id")
    private Integer bookId;

    @Schema(description = "Оценка книги", example = "5")
    @Min(value = 1, message = "api.error.book.rating")
    @Max(value = 5, message = "api.error.book.rating")
    @NotNull(message = "api.error.book.rating")
    private Integer value;
}
