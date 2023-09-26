package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(name = "Запрос списка транзакций")
@Data
public class BalanceTransactionsRequestDTO {
    @Schema(description = "Номер страницы", type = "integer", example = "0")
    @Min(value = 0, message = "api.error.transaction.offset")
    private Integer offset = 0;

    @Schema(description = "Количество транзакций на странице", type = "integer", example = "6")
    @Min(value = 1, message = "api.error.transaction.limit")
    private Integer limit = 6;

    @Schema(description = "Направление сортировки, asc или desc", example = "desc")
    @NotEmpty(message = "api.error.transaction.sort")
    private String sort = "desc";
}
