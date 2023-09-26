package ru.book_shop.dto;

import lombok.Data;

import java.util.List;

@Data
public class GenreTreeDTO {
    private String name;
    private String path;
    private Integer bookCount;
    private List<GenreTreeDTO> children;
}
