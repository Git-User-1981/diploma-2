package ru.book_shop.services.reviews;

import ru.book_shop.dto.BookReviewDTO;

import java.util.List;

public interface ReviewsService {
    List<BookReviewDTO> getReviewsByBookId(Integer bookId, Integer userId);
    void saveReview(Integer bookId, String text, Integer userId);
    void saveRateReview(Integer reviewId, Integer value, Integer userId);
    void deleteReview(Integer reviewId);
}
