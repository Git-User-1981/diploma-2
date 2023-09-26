package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(name = "Книга")
@Data
@EqualsAndHashCode(callSuper = true)
public class BookCardDTO extends BookDTO {
    @Schema(description = "Авторы книги")
    private String authors;
}
