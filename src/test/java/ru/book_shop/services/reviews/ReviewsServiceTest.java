package ru.book_shop.services.reviews;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReviewsServiceTest {

    @Autowired
    private ReviewsService reviewsService;

    @Test
    void getReviewsByBookId() {
        final int rating = 1;
        final int currentRating = reviewsService
                .getReviewsByBookId(4849, 0)
                .stream()
                .filter(r -> r.getId() == 176)
                .findFirst()
                .map(r -> r.getLikesCount() - r.getDislikesCount())
                .orElse(0);

        assertEquals(currentRating, rating);
    }
}
