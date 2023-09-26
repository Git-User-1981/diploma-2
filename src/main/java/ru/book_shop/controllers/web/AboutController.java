package ru.book_shop.controllers.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {
    @GetMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("fragment", "about");
        return "index";
    }
}
