package ru.book_shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import ru.book_shop.repositories.UserSessionRepository;
import ru.book_shop.security.UserDetailsServiceImpl;
import ru.book_shop.security.jwt.JwtAuthenticationFilter;
import ru.book_shop.security.jwt.JwtService;
import ru.book_shop.security.oauth2.OAuth2UserImpl;
import ru.book_shop.services.users.UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            UserDetailsServiceImpl userDetailsService
    ) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(getPasswordEncoder())
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtService jwtService,
            UserSessionRepository userSessionRepository,
            LogoutHandler logoutHandler,
            UserService userService,
            @Value("${app.link.main}") String mainUrl,
            @Value("${app.auth.jwt.token.name}") String cookieTokenName,
            @Value("${app.auth.jwt.refresh-token.name}") String cookieRefreshTokenName,
            @Value("${app.link.my}") String myURI
    ) throws Exception {
        final JwtAuthenticationFilter jwtAuthFilter = new JwtAuthenticationFilter(
                cookieTokenName,
                cookieRefreshTokenName,
                jwtService,
                userSessionRepository
        );

        return http.csrf().disable()
                .securityMatcher(
                    new AndRequestMatcher(
                        new NegatedRequestMatcher(new AntPathRequestMatcher("/assets/**")),
                        new NegatedRequestMatcher(new AntPathRequestMatcher("/favicon.ico"))
                    )
                )
                .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/**").permitAll()
                    .anyRequest().authenticated()
                )
                .formLogin()
                .loginPage("/signin")
                .failureUrl("/signin")
                .and()
                .logout()
                .logoutUrl("/logout")
                .addLogoutHandler(logoutHandler)
                .logoutSuccessUrl(mainUrl)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login()
                .successHandler((request, response, authentication) -> {
                    userService.loginOAuthUser((OAuth2UserImpl) authentication.getPrincipal(), response);
                    response.sendRedirect(myURI);
                })
                .and()
                .build();
    }
}
