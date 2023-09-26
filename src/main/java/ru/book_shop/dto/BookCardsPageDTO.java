package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Schema(name = "Список книг")
@Data
public class BookCardsPageDTO {
    @Schema(description = "Всего книг")
    private Long count = -1L;
    private List<BookCardDTO> books = new ArrayList<>();
}
