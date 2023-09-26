package ru.book_shop.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BookEditRequestDTO {
    @NotNull(message = "api.error.book.id")
    private Integer book;

    @NotBlank(message = "api.error.book.title")
    private String title;

    @NotBlank(message = "api.error.book.description")
    private String description;

    private short bestseller = 0;

    @Min(value = 0, message = "api.error.book.discount")
    private short discount;

    @Min(value = 0, message = "api.error.book.price")
    private Integer price;

    @NotEmpty(message = "api.error.book.authors")
    private List<Integer> authors;

    @NotEmpty(message = "api.error.book.tags")
    private List<Integer> tags;
}
