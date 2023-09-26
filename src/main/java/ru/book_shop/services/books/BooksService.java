package ru.book_shop.services.books;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import ru.book_shop.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface BooksService {
    BookCardsPageDTO getRecommendedBooks(Integer offset, Integer limit);
    BookCardsPageDTO getRecentBooks(LocalDate from, LocalDate to, Integer offset, Integer limit);
    BookCardsPageDTO getPopularBooks(Integer offset, Integer limit);
    BookCardsPageDTO getBooksByTag(Integer tagId, Integer offset, Integer limit);
    BookCardsPageDTO getBooksByGenre(Integer genreId, Integer offset, Integer limit);
    BookCardsPageDTO getBooksByAuthor(Integer authorId, Integer offset, Integer limit);
    BookCardsPageDTO getViewsBooks(Integer offset, Integer limit);
    BookCardsPageDTO getBooksSearchResult(String query, Integer offset, Integer limit);
    List<BookCardDTO> getUserBooksByStatus(String status);
    BookPageDTO getBook(String slug);
    void saveCover(String slug, MultipartFile coverFile);
    ChangeBookStatusDTO changeBookStatus(List<Integer> booksIds, String status, HttpServletResponse response);
    List<BookPageDTO> getBooksByStatus(String status, List<String> totals);
    BookRatingStatDTO getRatingStat(Integer bookId);
    Map<Integer, String> getReservedBooks();
    BookUserRateResponseDTO setBookRating(Integer bookId, Integer value);
    String addBook(String title);
    void deleteBook(String slug);
    String saveBook(BookEditRequestDTO form);
    void saveBookFile(String slug, MultipartFile file, String bookFileType);
}
