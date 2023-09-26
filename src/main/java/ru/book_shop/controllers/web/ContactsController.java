package ru.book_shop.controllers.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.book_shop.dto.FeedbackRequestDTO;
import ru.book_shop.services.other.OtherService;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ContactsController {
    private final OtherService otherService;

    @GetMapping("${app.link.contacts}")
    public String contactsPage(Model model, Principal principal) {
        model.addAttribute("fragment", "contacts");
        model.addAttribute("isAnonymous", principal == null);
        return "index";
    }

    @PostMapping("${app.link.contacts}")
    public String feedback(
            @Value("${app.link.contacts}") String backUrl,
            @Valid FeedbackRequestDTO form,
            Principal principal,
            RedirectAttributes model
    ) {
        otherService.feedback(principal, form);
        model.addFlashAttribute("sendSuccess", true);
        return "redirect:" + backUrl;
    }
}
