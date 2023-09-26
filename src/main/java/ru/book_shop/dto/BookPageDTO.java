package ru.book_shop.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookPageDTO extends BookDTO {
    private List<AuthorDTO> authors;
}
