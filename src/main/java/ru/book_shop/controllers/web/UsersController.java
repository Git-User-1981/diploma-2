package ru.book_shop.controllers.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.book_shop.exceptions.PageCallException;
import ru.book_shop.services.reviews.ReviewsService;
import ru.book_shop.services.users.UserService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@Controller
@RequiredArgsConstructor
public class UsersController {
    private final UserService userService;
    private final ReviewsService reviewsService;

    @GetMapping({"${app.link.users}", "${app.link.users}/", "${app.link.users}/{query}"})
    public String usersPage(
            @PathVariable(value = "query", required = false) String query,
            @Value("${app.books-per-page}") Integer usersLimit,
            Model model
    ) {
        model.addAttribute("fragment", "users/index");
        model.addAttribute("query", query);
        if (query != null) {
            model.addAttribute("usersList", userService.getUsersSearchResult(query, 0, usersLimit));
        }

        return "index";
    }

    @PostMapping("${app.link.users}/{query}")
    public String usersAction(
            @PathVariable(value = "query") String query,
            @RequestParam(value = "user") Integer userId,
            @RequestParam(value = "action") String action,
            @RequestParam(value = "slug", required = false) String bookSlug
    ) {
        switch (action) {
            case "adm" -> userService.setRoleAdm(userId);
            case "del" -> userService.deleteUser(userId);
            case "gift" -> userService.giveBookToUser(userId, bookSlug);
            default -> throw new PageCallException("page.error.users.unexpected-action");
        }
        return "redirect:/users/" + URLEncoder.encode(query, StandardCharsets.UTF_8);
    }

    @PostMapping("${app.link.users.review-delete}")
    public String deleteUserReview(@RequestParam(value = "review") Integer reviewId, HttpServletRequest request) {
        reviewsService.deleteReview(reviewId);
        return "redirect:" + request.getHeader("Referer");
    }
}
