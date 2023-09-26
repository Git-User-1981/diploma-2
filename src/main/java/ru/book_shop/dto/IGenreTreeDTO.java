package ru.book_shop.dto;

public interface IGenreTreeDTO {
    Integer getId();
    String getName();
    String getSlug();
    Integer getCnt();
    Integer getParent();
}
