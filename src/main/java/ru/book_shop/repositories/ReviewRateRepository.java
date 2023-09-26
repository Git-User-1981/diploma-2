package ru.book_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.book_shop.entities.book.review.BookReviewLike;

import java.util.Optional;

@Repository
public interface ReviewRateRepository extends JpaRepository<BookReviewLike, Integer> {
    Optional<BookReviewLike> getByReviewIdAndUserId(Integer reviewId, Integer userId);
}
