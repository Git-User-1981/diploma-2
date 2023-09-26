package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(name = "Список транзакций")
@Data
public class BalanceTransactionPageDTO {
    @Schema(description = "Всего транзакций")
    private Long count = -1L;
    private List<BalanceTransactionDTO> transactions = new ArrayList<>();
}
