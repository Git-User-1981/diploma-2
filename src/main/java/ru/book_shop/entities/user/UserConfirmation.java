package ru.book_shop.entities.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "users_confirmations")
public class UserConfirmation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hash")
    private String hash;

    @Column(name = "code")
    private String code;

    @Column(name = "code_time")
    private LocalDateTime codeTime;

    @Column(name = "code_trails")
    private int codeTrails;

    @Column(name = "contact")
    private String contact;
}
