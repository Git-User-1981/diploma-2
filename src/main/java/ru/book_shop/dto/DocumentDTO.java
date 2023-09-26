package ru.book_shop.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentDTO {
    private String slug;
    private String title;
    private String shortText;
    private String text;
    private String img;
}
