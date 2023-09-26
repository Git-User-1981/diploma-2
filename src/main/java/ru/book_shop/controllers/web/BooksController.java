package ru.book_shop.controllers.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.book_shop.dto.*;
import ru.book_shop.services.authors.AuthorsService;
import ru.book_shop.services.books.BooksService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import ru.book_shop.services.reviews.ReviewsService;
import ru.book_shop.services.storage.FileStorageService;
import ru.book_shop.services.tags.TagsService;

import java.security.Principal;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/books")
public class BooksController {
    public static final String INDEX = "index";

    @Value("${app.books-per-page}")
    private Integer booksLimit;

    @Value("${app.path.books}")
    private String booksURI;

    private final BooksService booksService;
    private final AuthorsService authorsService;
    private final TagsService tagsService;
    private final FileStorageService storageService;
    private final ReviewsService reviewsService;

    @GetMapping("/recent")
    public String recentPage(Model model) {
        model.addAttribute("fragment", "books/recent");
        model.addAttribute("recentBooks", booksService.getRecentBooks(
                LocalDate.now().minusYears(1),
                LocalDate.now(),
                0,
                booksLimit
        ));
        return INDEX;
    }

    @GetMapping("/popular")
    public String popularPage(Model model) {
        model.addAttribute("fragment", "books/popular");
        model.addAttribute("popularBooks", booksService.getPopularBooks(0, booksLimit));
        return INDEX;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/views")
    public String viewsPage(Model model) {
        model.addAttribute("fragment", "books/views");
        model.addAttribute("viewsBooks", booksService.getViewsBooks(0, booksLimit));
        return INDEX;
    }

    @GetMapping("/author/{slug}")
    public String booksAuthorPage(@PathVariable("slug") String slug, Model model) {
        AuthorDTO author = authorsService.getAuthor(slug);
        model.addAttribute("fragment", "books/author");
        model.addAttribute("author", author);
        model.addAttribute("authorBooks", booksService.getBooksByAuthor(author.getId(), 0, booksLimit));
        return INDEX;
    }

    @GetMapping("/{slug}")
    public String bookPage(@PathVariable("slug") String slug, Model model, Principal principal) {
        final BookDTO book = booksService.getBook(slug);
        final Integer userId = principal == null ? 0 : Integer.parseInt(principal.getName());
        model.addAttribute("fragment", "books/slug");
        model.addAttribute("book", book);
        model.addAttribute("tags", tagsService.getTagsByBookId(book.getId()));
        model.addAttribute("files", storageService.getBookFiles(book.getId()));
        model.addAttribute("ratingStat", booksService.getRatingStat(book.getId()));
        model.addAttribute("reviews", reviewsService.getReviewsByBookId(book.getId(), userId));
        return INDEX;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{slug}/cover/save")
    public String bookCoverSave(@PathVariable("slug") String slug, @RequestParam("file") MultipartFile file) {
        booksService.saveCover(slug, file);
        return "redirect:" + booksURI + slug;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/download/{hash}")
    public ResponseEntity<InputStreamResource> bookFile(@PathVariable("hash") String hash, Principal principal) {
        final BookFileDTO file = storageService.getBookFileByHash(hash, Integer.valueOf(principal.getName()));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getFileName())
                .contentType(file.getMimeType())
                .contentLength(file.getLength())
                .body(file.getData());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/new")
    public String bookNew(@RequestParam(value = "title") String title) {
        return String.format("redirect:%s%s/edit", booksURI, booksService.addBook(title));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{slug}/edit")
    public String bookEdit(@PathVariable(value = "slug") String slug, Model model) {
        final BookDTO book = booksService.getBook(slug);
        model.addAttribute("fragment", "books/edit");
        model.addAttribute("book", book);
        model.addAttribute("authorsList", authorsService.getAuthorsList());
        model.addAttribute("tags", tagsService.getTagsByBookId(book.getId()));
        model.addAttribute("tagsList", tagsService.getTagsList());
        model.addAttribute("files", storageService.getBookFiles(book.getId()));
        return INDEX;
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/save")
    public String saveBook(@Valid BookEditRequestDTO form) {
        return "redirect:" + booksURI + booksService.saveBook(form);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{slug}/file/save")
    public String saveBookFile(
            @PathVariable(value = "slug") String slug,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type
    ) {
        booksService.saveBookFile(slug, file, type);
        return String.format("redirect:%s%s/edit", booksURI, slug);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{slug}/delete")
    public String bookDelete(@PathVariable(value = "slug") String slug) {
        booksService.deleteBook(slug);
        return "redirect:/";
    }
}
