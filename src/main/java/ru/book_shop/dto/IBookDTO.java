package ru.book_shop.dto;

public interface IBookDTO {
    Integer getId();
    String getTitle();
    String getImage();
    String getSlug();
    Integer getIsBestseller();
    Integer getPrice();
    Integer getDiscountPrice();
    Short getDiscount();
    Integer getRating();
}
