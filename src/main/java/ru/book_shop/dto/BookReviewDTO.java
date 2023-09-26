package ru.book_shop.dto;

import lombok.Data;

@Data
public class BookReviewDTO {
    private Integer id;
    private Integer userId;
    private String userName;
    private String time;
    private String reviewShort;
    private String reviewFull;
    private Integer likesCount;
    private Integer dislikesCount;
    private Integer userRating;
    private Integer userLike;
}
