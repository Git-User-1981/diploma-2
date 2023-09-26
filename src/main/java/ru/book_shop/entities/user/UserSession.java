package ru.book_shop.entities.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@RequiredArgsConstructor
@ToString
@Entity
@Builder
@Table(name = "users_sessions")
@AllArgsConstructor
public class UserSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "expired")
    private LocalDateTime expired;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;
}
