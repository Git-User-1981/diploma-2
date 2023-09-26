package ru.book_shop.services.menu;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.book_shop.dao.MenuItem;
import org.springframework.stereotype.Service;
import ru.book_shop.services.books.BooksService;

import java.util.List;

@Slf4j
@Service
public class MenuServiceImpl implements MenuService {
    private final List<MenuItem> menuItems;
    private final BooksService booksService;

    public MenuServiceImpl(@Value("${app.link.main}") String main,
                           @Value("${app.link.genres}") String genres,
                           @Value("${app.link.book.recent}") String recent,
                           @Value("${app.link.book.popular}") String popular,
                           @Value("${app.link.authors}") String authors,
                           @Value("${app.link.book.views}") String views,
                           @Value("${app.link.users}") String users,
                           BooksService booksService) {
        this.booksService = booksService;
        menuItems = List.of(
            new MenuItem("main/index", main, "menu.main", "ANY"),
            new MenuItem("genres/", genres, "menu.genres", "ANY"),
            new MenuItem("books/recent", recent, "menu.recent", "ANY"),
            new MenuItem("books/popular", popular, "menu.popular", "ANY"),
            new MenuItem("authors/", authors, "menu.authors", "ANY"),
            new MenuItem("books/views", views, "menu.views", "ROLE_USER,ROLE_ADMIN"),
            new MenuItem("users/", users, "menu.users", "ROLE_ADMIN")
        );
    }

    @Override
    public List<MenuItem> getMenuItems() {
        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final String role = auth == null ? "ANY" : auth.getAuthorities().stream().findFirst().orElseThrow().getAuthority();
        return menuItems
                .stream()
                .filter(mi -> mi.getForRoles().equals("ANY") || mi.getForRoles().contains(role))
                .toList();
    }

    @Override
    public int[] getReservedBooksCount() {
        final int [] counter = new int[3];
        for (String reserveType : booksService.getReservedBooks().values()) {
            switch (reserveType) {
                case "KEPT" -> counter[0]++;
                case "CART" -> counter[1]++;
                default -> log.warn("Unknown reserve book type: " + reserveType);
            }
        }
        return counter;
    }
}
