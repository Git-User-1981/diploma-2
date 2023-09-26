package ru.book_shop.controllers.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.book_shop.dto.AuthorDTO;
import ru.book_shop.services.authors.AuthorsService;
import ru.book_shop.services.books.BooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class AuthorsController {
    @Value("${app.link.authors}")
    private String authorsPath;

    private final AuthorsService authorsService;
    private final BooksService booksService;

    @Autowired
    public AuthorsController(AuthorsService authorsService, BooksService booksService) {
        this.authorsService = authorsService;
        this.booksService = booksService;
    }

    @GetMapping("/authors")
    public String authorsPage(Model model) {
        model.addAttribute("fragment", "authors/index");
        model.addAttribute("authorsData", authorsService.getAuthorsData());

        return "index";
    }

    @GetMapping("/authors/{slug}")
    public String genrePage(@PathVariable("slug") String slug, Model model) {
        AuthorDTO author = authorsService.getAuthor(slug);
        model.addAttribute("fragment", "authors/slug");
        model.addAttribute("author", author);
        model.addAttribute("bookList", booksService.getBooksByAuthor(author.getId(), 0, 5));

        return "index";
    }

    @PostMapping("/authors/{slug}/photo/save")
    public String authorPhotoSave(@PathVariable("slug") String slug, @RequestParam("file") MultipartFile file) {
        authorsService.savePhoto(slug, file);
        return "redirect:" + authorsPath + '/' + slug;
    }
}
