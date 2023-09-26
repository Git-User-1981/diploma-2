package ru.book_shop.entities.book.links;

import jakarta.persistence.Entity;
import lombok.*;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "book2user_recent_views")
public class Book2UserRecentViews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "book_id")
    private Integer bookId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "time")
    private LocalDateTime time;
}
