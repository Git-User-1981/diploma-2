package ru.book_shop.controllers.web;

import ru.book_shop.services.books.BooksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.book_shop.services.tags.TagsService;

@Controller
public class MainPageController {
    private final BooksService booksService;
    private final TagsService tagsService;

    @Autowired
    public MainPageController(BooksService booksService, TagsService tagsService) {
        this.booksService = booksService;
        this.tagsService = tagsService;
    }

    @GetMapping("/")
    public String mainPage(Model model) {
        model.addAttribute("recommendedBooks", booksService.getRecommendedBooks(0, 6).getBooks());
        model.addAttribute("recentBooks", booksService.getRecentBooks(null, null, 0, 6).getBooks());
        model.addAttribute("popularBooks", booksService.getPopularBooks(0, 6).getBooks());
        model.addAttribute("tagList", tagsService.getTagCloud());
        model.addAttribute("fragment", "main/index");
        return "index";
    }
}
