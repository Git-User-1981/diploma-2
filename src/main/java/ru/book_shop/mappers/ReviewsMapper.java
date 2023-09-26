package ru.book_shop.mappers;

import org.springframework.stereotype.Component;
import ru.book_shop.dto.BookReviewDTO;
import ru.book_shop.dto.IBookReviewDTO;

@Component
public class ReviewsMapper {
    private ReviewsMapper(){}

    public static BookReviewDTO toDTO(IBookReviewDTO review) {
        BookReviewDTO dto = new BookReviewDTO();
        dto.setId(review.getId());
        dto.setUserId(review.getUserId());
        dto.setUserName(review.getUserName());
        dto.setUserRating(review.getUserRating());
        dto.setTime(review.getReviewTime());
        dto.setLikesCount(review.getLikesCount());
        dto.setDislikesCount(review.getDislikesCount());
        dto.setUserLike(review.getUserLike());

        String reviewText = review.getText();
        String[] reviewWords = reviewText.replaceAll("[\\p{Punct}«»—:]+", "").trim().split("\\s+");

        if (reviewWords.length > 30) {
            dto.setReviewShort(reviewText.substring(0, reviewText.indexOf(reviewWords[30])));
            dto.setReviewFull(reviewText.substring(reviewText.indexOf(reviewWords[30])));
        }
        else {
            dto.setReviewShort(reviewText);
        }

        return dto;
    }
}
