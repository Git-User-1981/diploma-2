package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(name = "Запрос на сохранение отзыва о книге")
@Data
public class BookReviewRequestDTO {
    @Schema(description = "Идентификатор книги, на которую отправляется отзыв", example = "1111")
    @Min(value = 1, message = "api.error.book.id")
    @NotNull(message = "api.error.book.id")
    private Integer bookId;

    @Schema(description = "Текст отзыва", example = "Хорошая книга!")
    @Size(min = 7, message = "api.error.review.text-length")
    @NotNull(message = "api.error.review.text-length")
    private String text;
}
