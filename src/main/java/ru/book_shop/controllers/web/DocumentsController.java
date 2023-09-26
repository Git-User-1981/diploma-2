package ru.book_shop.controllers.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.book_shop.services.other.OtherService;

@Controller
@RequiredArgsConstructor
public class DocumentsController {
    private final OtherService otherService;

    @GetMapping("/documents")
    public String documentsPage(Model model) {
        model.addAttribute("fragment", "documents/index");
        model.addAttribute("list", otherService.getListDocuments());
        return "index";
    }

    @GetMapping("/documents/{slug}")
    public String documentPage(@PathVariable("slug") String slug, Model model) {
        model.addAttribute("fragment", "documents/slug");
        model.addAttribute("doc", otherService.getDocument(slug));
        return "index";
    }
}
