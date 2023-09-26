package ru.book_shop.services.users;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import ru.book_shop.dto.*;
import ru.book_shop.entities.user.User;
import ru.book_shop.security.oauth2.OAuth2UserImpl;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface UserService {
    Integer registerNewUser(RegistrationUserRequestDTO form);
    void loginOAuthUser(OAuth2UserImpl oAuth2User, HttpServletResponse response);
    void login(LoginRequestDTO loginForm, HttpServletResponse response);
    User getCurrentUser(Principal principal);
    String getCookieValueByName(String cookieName);
    Cookie getNewAccessTokenCookieByRefreshToken();
    int[] getUserBooksCount();
    Map<Integer, String> getUserBooks();
    void deleteCookieByName(String name, HttpServletResponse response);
    ChangeBookStatusDTO changeBookStatus(List<Integer> booksIds, String status);
    String profileSave(Integer userId, ProfileSaveRequestDTO form);
    ConfirmationResponseDTO contactConfirmation(String contact, String type);
    String approveProfileContact(String hash, Integer userId);
    ConfirmationResponseDTO approveContact(String contact, String code);
    String getConfirmationCode(String contact, boolean isLink);
    boolean buyBooks(Integer userId);
    BalanceTransactionPageDTO getUserTransactions(Integer userId, int offset, Integer limit, String sort);
    String getUserHash(Integer userId);
    UsersPageDTO getUsersSearchResult(String query, Integer offset, Integer limit);
    void setRoleAdm(Integer userId);
    void deleteUser(Integer userId);
    void giveBookToUser(Integer userId, String bookSlug);
}
