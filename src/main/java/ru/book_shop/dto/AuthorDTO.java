package ru.book_shop.dto;

import lombok.Data;

@Data
public class AuthorDTO {
    Integer id;
    String name;
    String descriptionShort;
    String descriptionFull;
    String photo;
    String slug;
}
