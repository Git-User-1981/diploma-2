package ru.book_shop.mappers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.book_shop.dto.*;
import ru.book_shop.entities.book.Book;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BooksMapper {
    @Value("${app.path.book.cover}")
    private String coversURI;

    @Value("${app.path.upload.covers}")
    private String coversLocationPath;

    @Value("${app.book.cover.default}")
    private String coverDefault;

    @Value("${app.path.books}")
    private String booksURI;

    Map<Integer, String> reservedBooks = new HashMap<>();

    private final MessageSource messageSource;

    public BooksMapper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public void setReservedBooks(Map<Integer, String> reservedBooks) {
        this.reservedBooks = reservedBooks;
    }

    public BookPageDTO toPageDTO(IBookPageDTO book) {
        final BookPageDTO dto = new BookPageDTO();
        dto.setId(book.getId());
        dto.setSlug(booksURI + book.getSlug());
        dto.setTitle(book.getTitle());
        dto.setImage(getImagePath(book.getImage()));
        dto.setIsBestseller(book.getIsBestseller() != 0);
        dto.setDescription(book.getDescription());
        dto.setRating(book.getRating());
        dto.setStatus(getBookStatus(dto.getId()));
        dto.setDiscount(book.getDiscount());
        dto.setPrice(book.getPrice());
        dto.setDiscountPrice(book.getDiscountPrice());

        return dto;
    }

    public BookCardsPageDTO toBookCardsDTO(Page<IBookCardDTO> books) {
        final BookCardsPageDTO dto = new BookCardsPageDTO();
        dto.setCount(books.getTotalElements());
        dto.setBooks(books.getContent().stream().map(this::toBookCardDTO).toList());

        return dto;
    }

    public BookCardDTO toBookCardDTO(IBookCardDTO book) {
        final BookCardDTO dto = new BookCardDTO();
        dto.setAuthors(replaceMoreMark(book.getAuthors()));
        dto.setDiscount(book.getDiscount());
        dto.setTitle(book.getTitle());
        dto.setImage(getImagePath(book.getImage()));
        dto.setSlug(booksURI + book.getSlug());
        dto.setIsBestseller(book.getIsBestseller() != 0);
        dto.setId(book.getId());
        dto.setDiscountPrice(book.getDiscountPrice());
        dto.setPrice(book.getPrice());
        dto.setRating(book.getRating());
        dto.setStatus(getBookStatus(dto.getId()));

        return dto;
    }

    public BookRatingStatDTO toBookRatingStatDTO(List<Integer> listRatingStat) {
        final BookRatingStatDTO dto = new BookRatingStatDTO();
        dto.setStar1(listRatingStat.get(0));
        dto.setStar2(listRatingStat.get(1));
        dto.setStar3(listRatingStat.get(2));
        dto.setStar4(listRatingStat.get(3));
        dto.setStar5(listRatingStat.get(4));
        dto.setTotal(listRatingStat.stream().mapToInt(Integer::intValue).sum());

        return dto;
    }

    private String replaceMoreMark(String authors) {
        return authors.replace(
            "#moreTxt#",
            messageSource.getMessage("book.author.more", null, LocaleContextHolder.getLocale())
        );
    }

    private String getImagePath(String imageName) {
        return imageName != null && Files.exists(Path.of(coversLocationPath + imageName)) ? coversURI + imageName : coverDefault;
    }

    private String getBookStatus(Integer bookId) {
        final String status = reservedBooks.get(bookId);
        return status == null ? "false" : status;
    }

    public void toBookEntity(Book book, BookEditRequestDTO form) {
        book.setTitle(form.getTitle());
        book.setDescription(form.getDescription());
        book.setIsBestseller(form.getBestseller());
        book.setDiscount(form.getDiscount());
        book.setPrice(form.getPrice());
        book.setPubDate(LocalDate.now());
    }
}
