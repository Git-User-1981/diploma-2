package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(name = "Запрос на сохранение оценки отзыва на книгу")
@Data
public class BookReviewRateRequestDTO {
    @Schema(description = "Идентификатор отзыва на книгу", example = "1111")
    @Min(value = 1, message = "api.error.review.review-id")
    @NotNull(message = "api.error.review.review-id")
    private Integer reviewId;

    @Schema(description = "оценка: лайк (1), дизлайк (-1) или удалить (0)", example = "1")
    @Min(value = -1, message = "api.error.review.like-dislike")
    @Max(value = 1, message = "api.error.review.like-dislike")
    @NotNull(message = "api.error.review.like-dislike")
    private Integer value;
}
