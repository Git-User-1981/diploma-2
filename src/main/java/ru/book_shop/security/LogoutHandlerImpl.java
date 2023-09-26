package ru.book_shop.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.book_shop.dto.IUserBooksCommonDTO;
import ru.book_shop.exceptions.ApiCallException;
import ru.book_shop.repositories.Book2UserRepository;
import ru.book_shop.repositories.UserSessionRepository;
import ru.book_shop.security.jwt.JwtService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class LogoutHandlerImpl implements LogoutHandler {
    @Value("${app.auth.jwt.token.name}")
    String cookieTokenName;

    @Value("${app.auth.jwt.refresh-token.name}")
    String cookieRefreshTokenName;

    @Value("${app.cookie.reserved-books}")
    private String reservedBooksCookieName;

    @Value("${app.coolie.max-age}")
    private Integer cookieMaxAge;

    @Value("${app.link.main}")
    private String cookiePath;

    private final UserSessionRepository userSessionRepository;
    private final Book2UserRepository book2UserRepository;
    private final JwtService jwtService;

    public LogoutHandlerImpl(
            UserSessionRepository userSessionRepository,
            Book2UserRepository book2UserRepository,
            JwtService jwtService
    ) {
        this.userSessionRepository = userSessionRepository;
        this.book2UserRepository = book2UserRepository;
        this.jwtService = jwtService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(cookieRefreshTokenName))
                .findFirst()
                .map(Cookie::getValue)
                .ifPresent(refreshToken -> {
                    userSessionRepository.deleteByRefreshToken(refreshToken);

                    book2UserRepository.deleteOldReservedBooks(cookieMaxAge);
                    final Integer userId = Integer.valueOf(jwtService.extractUsername(refreshToken));
                    final Map<Integer, String> userReservedBooks = book2UserRepository.getUserReservedBooks(userId)
                            .stream()
                            .collect(Collectors.toMap(IUserBooksCommonDTO::getNum, IUserBooksCommonDTO::getType, (k, v) -> v, LinkedHashMap::new));

                    String cookieBooks = null;

                    if (!userReservedBooks.isEmpty()) {
                        try {
                            cookieBooks = new ObjectMapper().writeValueAsString(userReservedBooks);
                            cookieBooks = URLEncoder.encode(cookieBooks, StandardCharsets.UTF_8);
                        }
                        catch (JsonProcessingException e) {
                            throw new ApiCallException("api.error.status.change-status");
                        }
                    }

                    final Cookie cookie = new Cookie(reservedBooksCookieName, cookieBooks);
                    cookie.setPath(cookiePath);
                    cookie.setMaxAge(cookieBooks == null ? 0 : cookieMaxAge);
                    response.addCookie(cookie);
                });

        response.addCookie(jwtService.createCookie(cookieTokenName, null, 0));
        response.addCookie(jwtService.createCookie(cookieRefreshTokenName, null, 0));
    }
}
