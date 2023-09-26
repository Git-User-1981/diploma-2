package ru.book_shop.dto;

import lombok.Data;

@Data
public class BookRatingStatDTO {
    private Integer star1;
    private Integer star2;
    private Integer star3;
    private Integer star4;
    private Integer star5;
    private Integer total;
}
