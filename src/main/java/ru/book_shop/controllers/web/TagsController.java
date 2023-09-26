package ru.book_shop.controllers.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.book_shop.entities.tags.Tag;
import ru.book_shop.services.books.BooksService;
import ru.book_shop.services.tags.TagsService;

@Controller
public class TagsController {
    @Value("${app.books-per-page}")
    private Integer booksLimit;
    private final TagsService tagsService;
    private final BooksService booksService;

    @Autowired
    public TagsController(BooksService booksService, TagsService tagsService) {
        this.tagsService = tagsService;
        this.booksService = booksService;
    }

    @GetMapping("/tags/{slug}")
    public String tagsPage(@PathVariable("slug") String slug, Model model) {
        Tag tag = tagsService.getTag(slug);
        model.addAttribute("fragment", "tags/index");
        model.addAttribute("tag", tag);
        model.addAttribute("tagList", tagsService.getTagCloud());
        model.addAttribute("bookList", booksService.getBooksByTag(tag.getId(), 0, booksLimit));

        return "index";
    }
}
