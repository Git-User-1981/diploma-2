package ru.book_shop.dto;

public interface IBookReviewDTO {
    Integer getId();
    Integer getUserId();
    String getUserName();
    Integer getUserRating();
    String getReviewTime();
    String getText();
    Integer getLikesCount();
    Integer getDislikesCount();
    Integer getUserLike();
}
