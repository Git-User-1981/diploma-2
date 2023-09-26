package ru.book_shop.controllers.web;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.book_shop.dto.ProfileSaveRequestDTO;
import ru.book_shop.dto.RegistrationUserRequestDTO;
import ru.book_shop.mappers.UsersMapper;
import ru.book_shop.services.books.BooksService;
import ru.book_shop.services.payments.Payments;
import ru.book_shop.services.users.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    public static final String INDEX = "index";

    @Value("${app.link.signin}")
    String signinUrl;

    @Value("${app.link.profile}")
    String profileUrl;

    @Value("${app.link.payment}")
    String paymentPage;

    private final UserService userService;
    private final BooksService booksService;
    private final Payments paymentService;

    @GetMapping("${app.link.postponed}")
    public String postponedPage(Model model) {
        final List<String> totals = new ArrayList<>();
        model.addAttribute("fragment", "user/postponed");
        model.addAttribute("books", booksService.getBooksByStatus("KEPT", totals));
        model.addAttribute("totals", totals);

        return INDEX;
    }

    @GetMapping("${app.link.cart}")
    public String cartPage(Model model) {
        final List<String> totals = new ArrayList<>();
        model.addAttribute("fragment", "user/cart");
        model.addAttribute("books", booksService.getBooksByStatus("CART", totals));
        model.addAttribute("totals", totals);
        return INDEX;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("${app.link.order}")
    public String order(
            @Value("${app.link.my}")
            String myPage,
            RedirectAttributes model,
            Principal principal
    ) {
        if (userService.buyBooks(Integer.valueOf(principal.getName()))){
            return "redirect:" + myPage;
        }

        model.addFlashAttribute("noMoney", true);
        return "redirect:" + paymentPage;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("${app.link.my}")
    public String myBooksPage(Model model) {
        model.addAttribute("fragment", "user/my");
        model.addAttribute("isArchive", false);
        model.addAttribute("books", booksService.getUserBooksByStatus("PAID"));
        return INDEX;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("${app.link.archive}")
    public String archivePage(Model model) {
        model.addAttribute("fragment", "user/my");
        model.addAttribute("isArchive", true);
        model.addAttribute("books", booksService.getUserBooksByStatus("ARCHIVED"));
        return INDEX;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("${app.link.profile}")
    public String profileMainPage(Model model, Principal principal) {
        model.addAttribute("fragment", "user/profile");
        model.addAttribute("profileActive", "main");
        model.addAttribute("userInfo", UsersMapper.toProfileDTO(userService.getCurrentUser(principal)));
        return INDEX;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("${app.link.profile-save}")
    public String profileSave(@Valid ProfileSaveRequestDTO form, RedirectAttributes model, Principal principal) {
        model.addFlashAttribute("profileSaveResult", userService.profileSave(Integer.valueOf(principal.getName()), form));
        return "redirect:" + profileUrl;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("${app.link.transactions}")
    public String profileTransactionsPage(@Value("${app.transactions-per-page}") Integer limit, Model model, Principal principal) {
        model.addAttribute("fragment", "user/profile");
        model.addAttribute("profileActive", "transactions");
        model.addAttribute("transactions", userService.getUserTransactions(Integer.valueOf(principal.getName()), 0, limit, "desc"));
        return INDEX;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("${app.link.payment}")
    public String profilePaymentPage(Model model, Principal principal) {
        model.addAttribute("fragment", "user/profile");
        model.addAttribute("profileActive", "payment");
        model.addAttribute("userHash", userService.getUserHash(Integer.valueOf(principal.getName())));
        return INDEX;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/approveProfileContact/{hash}")
    public String approveProfileContact(@PathVariable("hash") String hash, RedirectAttributes model, Principal principal) {
        model.addFlashAttribute("profileSaveResult", userService.approveProfileContact(hash, Integer.valueOf(principal.getName())));
        return "redirect:" + profileUrl;
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("${app.link.signin}")
    public String signinPage(Model model) {
        model.addAttribute("fragment", "user/signin");
        return INDEX;
    }

    @PreAuthorize("isAnonymous()")
    @GetMapping("${app.link.signup}")
    public String signupPage(Model model) {
        model.addAttribute("fragment", "user/signup");
        return INDEX;
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("${app.link.registration}")
    public String registration(@Valid RegistrationUserRequestDTO form, RedirectAttributes model) {
        userService.registerNewUser(form);
        model.addFlashAttribute("regOk", true);
        return "redirect:" + signinUrl;
    }

    @GetMapping("${app.link.refresh-token}")
    public String refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        Cookie token = userService.getNewAccessTokenCookieByRefreshToken();

        if (token == null) {
            return "redirect:" + signinUrl;
        }

        response.addCookie(token);

        String redirectUrl = request.getAttribute("ref").toString();
        return "redirect:" + (redirectUrl == null ? signinUrl : redirectUrl);
    }

    @GetMapping("/complete/{hash}/{sum}")
    public String completePayment(@PathVariable("hash") String hash, @PathVariable("sum") Integer sum, RedirectAttributes model) {
        paymentService.completePayment(hash, sum);
        model.addFlashAttribute("paySuccess", true);
        return "redirect:" + paymentPage;
    }
}
