package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(name = "Транзакция")
@Builder
@Data
public class BalanceTransactionDTO {
    @Schema(description = "Дата и время транзакции", example = "29 августа 2023 18:28")
    private String time;

    @Schema(description = "Сумма транзакции", example = "-130₽")
    private String value;

    @Schema(description = "Описание транзакции", example = "Покупка книги <a href=\"/books/book-67\">Angels and Visitations</a>")
    private String description;
}
