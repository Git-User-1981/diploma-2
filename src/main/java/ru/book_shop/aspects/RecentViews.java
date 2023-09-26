package ru.book_shop.aspects;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.book_shop.dto.BookPageDTO;
import ru.book_shop.entities.book.links.Book2UserRecentViews;
import ru.book_shop.repositories.Book2UserRecentViewsRepository;

import java.security.Principal;
import java.time.LocalDateTime;

@Aspect
@RequiredArgsConstructor
@Component
public class RecentViews {
    private final HttpServletRequest request;
    private final Book2UserRecentViewsRepository repository;

    @AfterReturning(
            pointcut = "execution(* ru.book_shop.services.books.BooksService.getBook(..))",
            returning = "book"
    )
    public void setUserRecentViews(BookPageDTO book) {
        final Principal principal = request.getUserPrincipal();
        if (principal != null) {
            repository.save(
                    Book2UserRecentViews
                            .builder()
                            .bookId(book.getId())
                            .userId(Integer.valueOf(principal.getName()))
                            .time(LocalDateTime.now())
                            .build()
            );
        }
    }
}
