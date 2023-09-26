package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Schema(name = "Запрос на изменение статуса книги")
@Builder
@Data
public class ChangeStatusRequestDTO {
    @Schema(description = "Новый статус книги", example = "KEPT")
    @NotEmpty(message = "api.error.status.status")
    private String status;

    @Schema(description = "Массив идентификаторов книг", example = "[1, 2, 3]")
    @NotEmpty(message = "api.error.status.book-list")
    private List<@Min(value = 1, message = "api.error.book.id") Integer> booksIds;
}
