package ru.book_shop.controllers.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import ru.book_shop.entities.genre.Genre;
import ru.book_shop.services.books.BooksService;
import ru.book_shop.services.genres.GenresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GenresController {
    @Value("${app.books-per-page}")
    private Integer booksLimit;
    private final GenresService genresService;
    private final BooksService booksService;

    @Autowired
    public GenresController(GenresService genresService, BooksService booksService) {
        this.genresService = genresService;
        this.booksService = booksService;
    }

    @GetMapping("/genres")
    public String genresPage(Model model) {
        model.addAttribute("fragment", "genres/index");
        model.addAttribute("genreTree", genresService.getGenreTree());
        return "index";
    }

    @GetMapping("/genres/{slug}")
    public String genrePage(@PathVariable("slug") String slug, Model model) {
        Genre genre = genresService.getGenre(slug);
        model.addAttribute("fragment", "genres/slug");
        model.addAttribute("genre", genre);
        model.addAttribute("breadcrumbs", genresService.getBreadcrumbs(genre.getId()));
        model.addAttribute("bookList", booksService.getBooksByGenre(genre.getId(), 0, booksLimit));

        return "index";
    }
}
