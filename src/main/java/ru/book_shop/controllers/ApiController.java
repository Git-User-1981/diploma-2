package ru.book_shop.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.book_shop.dto.*;
import ru.book_shop.services.books.BooksService;
import ru.book_shop.services.payments.Payments;
import ru.book_shop.services.reviews.ReviewsService;
import ru.book_shop.services.users.UserService;

import java.security.Principal;

@Validated
@Tag(name = "Методы API")
@ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ApiErrorResponseDTO.class), mediaType = "application/json"))
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ApiController {
    private final BooksService booksService;
    private final ReviewsService reviewsService;
    private final UserService userService;
    private final Payments paymentService;

    @Operation(summary = "Список рекомендованных книг")
    @ApiResponse(
        responseCode = "200",
        content = @Content(schema = @Schema(implementation = BookCardsPageDTO.class), mediaType = "application/json")
    )
    @GetMapping("/books/recommended")
    public ResponseEntity<BookCardsPageDTO> recommendedBooks(@ParameterObject @Valid BookRequestDTO req) {
        return ResponseEntity.ok(booksService.getRecommendedBooks(req.getOffset(), req.getLimit()));
    }

    @Operation(summary = "Список новых книг")
    @ApiResponse(
        responseCode = "200",
        content = @Content(schema = @Schema(implementation = BookCardsPageDTO.class), mediaType = "application/json")
    )
    @GetMapping("/books/recent")
    public ResponseEntity<BookCardsPageDTO> recentBooks(@ParameterObject @Valid BookRecentRequestDTO req) {
        return ResponseEntity.ok(booksService.getRecentBooks(req.getFrom(), req.getTo(), req.getOffset(), req.getLimit()));
    }

    @Operation(summary = "Список популярных книг")
    @ApiResponse(
        responseCode = "200",
        content = @Content(schema = @Schema(implementation = BookCardsPageDTO.class), mediaType = "application/json")
    )
    @GetMapping("/books/popular")
    public ResponseEntity<BookCardsPageDTO> popularBooks(@ParameterObject @Valid BookRequestDTO req) {
        return ResponseEntity.ok(booksService.getPopularBooks(req.getOffset(), req.getLimit()));
    }

    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Список недавно просмотренных книг")
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = BookCardsPageDTO.class), mediaType = "application/json")
    )
    @GetMapping("/books/views")
    public ResponseEntity<BookCardsPageDTO> viewsBooks(@ParameterObject @Valid BookRequestDTO req) {
        return ResponseEntity.ok(booksService.getViewsBooks(req.getOffset(), req.getLimit()));
    }

    @Operation(summary = "Список книг тегу")
    @ApiResponse(
        responseCode = "200",
        content = @Content(schema = @Schema(implementation = BookCardsPageDTO.class), mediaType = "application/json")
    )
    @GetMapping("/books/tag/{tagId}")
    public ResponseEntity<BookCardsPageDTO> booksByTag(
            @Parameter(description = "Идентификатор тега")
            @PathVariable("tagId")
            @Min(value = 1, message = "api.error.book.tag-id")
            Integer tagId,
            @ParameterObject
            @Valid
            BookRequestDTO req
    ) {
        return ResponseEntity.ok(booksService.getBooksByTag(tagId, req.getOffset(), req.getLimit()));
    }

    @Operation(summary = "Список книг по жанру")
    @ApiResponse(
        responseCode = "200",
        content = @Content(schema = @Schema(implementation = BookCardsPageDTO.class), mediaType = "application/json")
    )
    @GetMapping("/books/genre/{genreId}")
    public ResponseEntity<BookCardsPageDTO> booksByGenre(
            @Parameter(description = "Идентификатор жанра")
            @PathVariable("genreId")
            @Min(value = 1, message = "api.error.book.genre-id")
            Integer genreId,
            @ParameterObject
            @Valid
            BookRequestDTO req
    ) {
        return ResponseEntity.ok(booksService.getBooksByGenre(genreId, req.getOffset(), req.getLimit()));
    }

    @Operation(summary = "Список книг по автору")
    @ApiResponse(
        responseCode = "200",
        content = @Content(schema = @Schema(implementation = BookCardsPageDTO.class), mediaType = "application/json")
    )
    @GetMapping("/books/author/{authorId}")
    public ResponseEntity<BookCardsPageDTO> booksByAuthor(
            @Parameter(description = "Идентификатор автора")
            @PathVariable("authorId")
            @Min(value = 1, message = "api.error.book.author-id")
            Integer authorId,
            @ParameterObject
            @Valid
            BookRequestDTO req
    ) {
        return ResponseEntity.ok(booksService.getBooksByAuthor(authorId, req.getOffset(), req.getLimit()));
    }

    @Operation(summary = "Результат поиска книг")
    @ApiResponse(
        responseCode = "200",
        content = @Content(schema = @Schema(implementation = BookCardsPageDTO.class), mediaType = "application/json")
    )
    @GetMapping("/search/{query}")
    public ResponseEntity<BookCardsPageDTO> booksSearch(
            @Parameter(description = "Поисковый запрос")
            @PathVariable("query")
            String query,
            @ParameterObject
            @Valid
            BookRequestDTO req
    ) {
        if (query == null) {
            return ResponseEntity.ok(new BookCardsPageDTO());
        }
        return ResponseEntity.ok(booksService.getBooksSearchResult(query, req.getOffset(), req.getLimit()));
    }

    @Hidden
    @GetMapping({"/search", "/search/"})
    public ResponseEntity<BookCardsPageDTO> booksSearch() {
        return ResponseEntity.ok(new BookCardsPageDTO());
    }

    @Operation(summary = "Изменение статуса книги")
    @ApiResponse(
        responseCode = "200",
        content = @Content(schema = @Schema(implementation = ChangeBookStatusDTO.class), mediaType = "application/json")
    )
    @PostMapping("/changeBookStatus")
    public ResponseEntity<ChangeBookStatusDTO> changeBookStatus(
            @RequestBody
            @Valid
            ChangeStatusRequestDTO request,
            HttpServletResponse response,
            Principal principal
    ) {
        if (principal == null) {
            return ResponseEntity.ok(booksService.changeBookStatus(request.getBooksIds(), request.getStatus(), response));
        }
        else {
            return ResponseEntity.ok(userService.changeBookStatus(request.getBooksIds(), request.getStatus()));
        }
    }

    @Operation(summary = "Отправка оценки пользователя на книгу")
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = BookUserRateResponseDTO.class), mediaType = "application/json")
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/rateBook")
    public ResponseEntity<BookUserRateResponseDTO> rateBook(@RequestBody @Valid BookUserRateRequestDTO request) {
        return ResponseEntity.ok(booksService.setBookRating(request.getBookId(), request.getValue()));
    }

    @Operation(summary = "Отправка отзыва на книгу")
    @ApiResponse(
        responseCode = "200",
        content = @Content(schema = @Schema(implementation = ApiBasicResponseDTO.class), mediaType = "application/json")
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/bookReview")
    public ResponseEntity<ApiBasicResponseDTO> bookReview(@RequestBody @Valid BookReviewRequestDTO request, Principal principal) {
        reviewsService.saveReview(request.getBookId(), request.getText(), Integer.valueOf(principal.getName()));
        return ResponseEntity.ok(new ApiBasicResponseDTO());
    }

    @Operation(summary = "Лайк или дизлайк отзыва на книгу")
    @ApiResponse(
        responseCode = "200",
        content = @Content(schema = @Schema(implementation = ApiBasicResponseDTO.class), mediaType = "application/json")
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/rateBookReview")
    public ResponseEntity<ApiBasicResponseDTO> rateBookReview(@RequestBody @Valid BookReviewRateRequestDTO request, Principal principal) {
        reviewsService.saveRateReview(request.getReviewId(), request.getValue(), Integer.valueOf(principal.getName()));
        return ResponseEntity.ok(new ApiBasicResponseDTO());
    }

    @Operation(summary = "Список транзакций")
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = BalanceTransactionPageDTO.class), mediaType = "application/json")
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/transactions")
    public ResponseEntity<BalanceTransactionPageDTO> getTransactions(
            @ParameterObject
            @Valid
            BalanceTransactionsRequestDTO request,
            Principal principal
    ) {
        return ResponseEntity.ok(userService.getUserTransactions(
                Integer.valueOf(principal.getName()),
                request.getOffset(),
                request.getLimit(),
                request.getSort())
        );
    }

    @Operation(summary = "Пополнение счета")
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = RegisterPaymentResponseDTO.class), mediaType = "application/json")
    )
    @PostMapping("/payment")
    public ResponseEntity<RegisterPaymentResponseDTO> registerPayment(@RequestBody @Valid RegisterPaymentRequestDTO request) {
        return ResponseEntity.ok(paymentService.registerPayment(request));
    }

    @Operation(summary = "Запрос на подтверждение контакта пользователя")
    @ApiResponse(
        responseCode = "200",
        content = @Content(schema = @Schema(implementation = ConfirmationResponseDTO.class), mediaType = "application/json")
    )
    @PostMapping("/requestContactConfirmation")
    public ResponseEntity<ConfirmationResponseDTO> contactConfirmation(@RequestBody @Valid ConfirmationRequestDTO request) {
        return ResponseEntity.ok(userService.contactConfirmation(request.getContact(), request.getType()));
    }

    @Operation(summary = "Подтверждение контакта пользователя")
    @ApiResponse(
        responseCode = "200",
        content = @Content(schema = @Schema(implementation = ConfirmationResponseDTO.class), mediaType = "application/json")
    )
    @PostMapping("/approveContact")
    public ResponseEntity<ConfirmationResponseDTO> approveContact(@RequestBody @Valid ApproveRequestDTO request) {
        return ResponseEntity.ok(userService.approveContact(request.getContact(), request.getCode()));
    }

    @Operation(summary = "Авторизация пользователя")
    @ApiResponse(
        responseCode = "200",
        content = @Content(schema = @Schema(implementation = ApiBasicResponseDTO.class), mediaType = "application/json")
    )
    @PreAuthorize("isAnonymous()")
    @PostMapping("/login")
    public ResponseEntity<ApiBasicResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginForm, HttpServletResponse response) {
        userService.login(loginForm, response);
        return ResponseEntity.ok(new ApiBasicResponseDTO());
    }

    @Operation(summary = "Результат поиска пользователей")
    @ApiResponse(
            responseCode = "200",
            content = @Content(schema = @Schema(implementation = UsersPageDTO.class), mediaType = "application/json")
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users/{query}")
    public ResponseEntity<UsersPageDTO> usersSearch(
            @Parameter(description = "Поисковый запрос")
            @PathVariable("query")
            String query,
            @ParameterObject
            @Valid
            UsersRequestDTO req
    ) {
        if (query == null) {
            return ResponseEntity.ok(UsersPageDTO.builder().build());
        }
        return ResponseEntity.ok(userService.getUsersSearchResult(query, req.getOffset(), req.getLimit()));
    }
}
