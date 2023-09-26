package ru.book_shop.dto;

import java.time.LocalDateTime;

public interface IBalanceTransactionDTO {
    String getSlug();
    String getTitle();
    LocalDateTime getTime();
    Integer getValue();
}
