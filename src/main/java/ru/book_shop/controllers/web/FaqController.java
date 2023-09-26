package ru.book_shop.controllers.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.book_shop.services.other.OtherService;

@Controller
@RequiredArgsConstructor
public class FaqController {
    private final OtherService otherService;

    @GetMapping("/faq")
    public String faqPage(Model model) {
        model.addAttribute("fragment", "faq");
        model.addAttribute("list", otherService.geFaqList());
        return "index";
    }
}
