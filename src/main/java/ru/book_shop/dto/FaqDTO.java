package ru.book_shop.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FaqDTO {
    private String question;
    private String answer;
}
