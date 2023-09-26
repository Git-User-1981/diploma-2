package ru.book_shop.controllers.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import ru.book_shop.services.books.BooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SearchController {
    @Value("${app.books-per-page}")
    private Integer booksLimit;
    private final BooksService booksService;

    @Autowired
    public SearchController(BooksService booksService) {
        this.booksService = booksService;
    }

    @GetMapping({"/search", "/search/", "/search/{query}"})
    public String searchPage(@PathVariable(value = "query", required = false) String query, Model model) {
        model.addAttribute("fragment", "search/index");
        model.addAttribute("query", query);
        if (query != null) {
            model.addAttribute("bookList", booksService.getBooksSearchResult(query, 0, booksLimit));
        }

        return "index";
    }
}
