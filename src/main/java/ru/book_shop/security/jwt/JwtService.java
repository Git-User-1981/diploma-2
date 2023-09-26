package ru.book_shop.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.book_shop.entities.user.User;
import ru.book_shop.security.UserDetailsImpl;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {
    @Value("${app.link.main}")
    private String cookieTokenPath;

    @Value("${app.auth.jwt.key}")
    private String secretKey;

    @Value("${app.auth.jwt.token.name}")
    private String cookieTokenName;

    @Value("${app.auth.jwt.refresh-token.name}")
    private String cookieRefreshTokenName;

    @Value("${app.auth.jwt.expiration}")
    private long jwtExpiration;

    @Value("${app.auth.jwt.refresh.expiration}")
    private long refreshExpiration;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Cookie generateTokenCookie(User user) {
        final String token = generateToken(Map.of("roles", user.getRole()), String.valueOf(user.getId()));
        return createCookie(cookieTokenName, token, (int) jwtExpiration);
    }

    public String generateToken(Map<String, Object> extraClaims, String subject) {
        return buildToken(extraClaims, subject, jwtExpiration);
    }

    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), String.valueOf(user.getId()), refreshExpiration);
    }

    public Cookie generateRefreshTokenCookie(User user) {
        final String token = generateRefreshToken(user);
        return createCookie(cookieRefreshTokenName, token, (int) refreshExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, String subject, long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Cookie createCookie(String cookieName, String cookieValue, Integer cookieExpiration) {
        final Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setPath(cookieTokenPath);
        cookie.setMaxAge(cookieExpiration);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);

        return cookie;
    }

    public boolean isTokenNotExpired(String token) {
        try {
            return !extractExpiration(token).before(new Date());
        }
        catch (ExpiredJwtException e) {
            log.info("Token expired: {}", token);
            return false;
        }
    }

    public UserDetails getPrincipalFromToken(String token) {
        return UserDetailsImpl.builder()
                .userName(String.valueOf(extractUsername(token)))
                .role(extractAllClaims(token).get("roles").toString())
                .build();
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        final byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
