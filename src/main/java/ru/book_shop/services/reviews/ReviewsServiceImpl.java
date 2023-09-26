package ru.book_shop.services.reviews;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.book_shop.dto.BookReviewDTO;
import ru.book_shop.entities.book.review.BookReview;
import ru.book_shop.entities.book.review.BookReviewLike;
import ru.book_shop.exceptions.ApiCallException;
import ru.book_shop.mappers.ReviewsMapper;
import ru.book_shop.repositories.ReviewRateRepository;
import ru.book_shop.repositories.ReviewRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class ReviewsServiceImpl implements ReviewsService {
    private final ReviewRepository reviewRepository;
    private final ReviewRateRepository reviewRateRepository;

    public ReviewsServiceImpl(ReviewRepository reviewRepository, ReviewRateRepository reviewRateRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewRateRepository = reviewRateRepository;
    }

    @Override
    public List<BookReviewDTO> getReviewsByBookId(Integer bookId, Integer userId) {
        return reviewRepository.getReviewsByBookId(bookId, userId).stream().map(ReviewsMapper::toDTO).toList();
    }

    @Override
    public void saveReview(Integer bookId, String text, Integer userId) {
        BookReview bookReview = new BookReview();
        bookReview.setBookId(bookId);
        bookReview.setText(text);
        bookReview.setTime(LocalDateTime.now());
        bookReview.setUserId(userId);

        try {
            reviewRepository.save(bookReview);
        }
        catch (DataIntegrityViolationException e) {
            throw new ApiCallException("api.error.review.save", e, 200);
        }
    }

    @Override
    public void saveRateReview(Integer reviewId, Integer value, Integer userId) {
        BookReviewLike bookReviewLike = reviewRateRepository
                .getByReviewIdAndUserId(reviewId, userId)
                .orElse(new BookReviewLike());

        bookReviewLike.setReviewId(reviewId);
        bookReviewLike.setUserId(userId);
        bookReviewLike.setValue(value.shortValue());
        bookReviewLike.setTime(LocalDateTime.now());

        try {
            if (value == 0) {
                reviewRateRepository.delete(bookReviewLike);
            }
            else {
                reviewRateRepository.save(bookReviewLike);
            }
        }
        catch (DataIntegrityViolationException e) {
            throw new ApiCallException("api.error.review.like-dislike.save", e);
        }
    }

    @Override
    public void deleteReview(Integer reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
