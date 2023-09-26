package ru.book_shop.services.books;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.book_shop.dto.BookCardsPageDTO;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BooksServiceTest {

    @Autowired
    private BooksService booksService;

    @Test
    void getPopularBooks() {
        final long totalBooksCount = 4909L;
        BookCardsPageDTO bookPage = booksService.getPopularBooks(0, 5);
        assertNotNull(bookPage);
        assertEquals(bookPage.getCount(), totalBooksCount);
    }

    @Test
    void getRecommendedBooks() {
        final long totalBooksCount = 4909L;
        BookCardsPageDTO bookPage = booksService.getRecommendedBooks(0, 5);
        assertNotNull(bookPage);
        assertEquals(bookPage.getCount(), totalBooksCount);
    }
}
