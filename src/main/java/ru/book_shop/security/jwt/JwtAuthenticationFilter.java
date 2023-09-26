package ru.book_shop.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.book_shop.entities.user.UserSession;
import ru.book_shop.repositories.UserSessionRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;


public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final String cookieTokenName;
    private final String cookieRefreshTokenName;
    private final JwtService jwtService;
    private final UserSessionRepository userSessionRepository;

    public JwtAuthenticationFilter(
            String cookieTokenName,
            String cookieRefreshTokenName,
            JwtService jwtService,
            UserSessionRepository userSessionRepository
    ) {
        this.cookieTokenName = cookieTokenName;
        this.cookieRefreshTokenName = cookieRefreshTokenName;
        this.jwtService = jwtService;
        this.userSessionRepository = userSessionRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String token = getTokenByName(request, cookieTokenName);

        if (token != null && jwtService.isTokenNotExpired(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
            setAuth(token, request);
        }
        else {
            final String refreshToken = getTokenByName(request, cookieRefreshTokenName);

            if (refreshToken != null && jwtService.isTokenNotExpired(refreshToken)) {
                UserSession userSession = userSessionRepository.findByRefreshToken(refreshToken).orElse(null);

                if (userSession != null) {
                    Cookie cookie = jwtService.generateTokenCookie(userSession.getUser());
                    setAuth(cookie.getValue(), request);
                    response.addCookie(cookie);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setAuth(String token, HttpServletRequest request) {
        UserDetails userDetails = jwtService.getPrincipalFromToken(token);
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private String getTokenByName(HttpServletRequest request, String cookieName){
        return Optional.ofNullable(request.getCookies())
                .flatMap(cookies -> Arrays.stream(cookies).filter(c -> c.getName().equals(cookieName)).findFirst())
                .map(Cookie::getValue)
                .orElse(null);
    }
}
