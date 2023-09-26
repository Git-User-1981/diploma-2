package ru.book_shop.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class BookDTO {
    @Schema(description = "Идентификатор книги")
    private Integer id;

    @Schema(description = "Мнемонический идентификатор книги")
    private String slug;

    @Schema(description = "Ссылка на обложку книги")
    private String image;

    @Schema(description = "Название книги")
    private String title;

    @Schema(description = "Размер скидки")
    private Short discount;

    @Schema(description = "Признак бестселлера")
    private Boolean isBestseller;

    @Schema(description = "Рейтинг книги")
    private Integer rating;

    @Schema(description = "Статус книги по отношению к текущему пользователю")
    private String status;

    @Schema(description = "Стоимость книги")
    private Integer price;

    @Schema(description = "Стоимость книги с учетом скидки")
    private Integer discountPrice;

    @JsonIgnore
    private String description;

    @JsonIgnore
    private Integer userRating = 0;
}
