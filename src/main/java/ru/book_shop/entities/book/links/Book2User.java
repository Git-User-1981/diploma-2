package ru.book_shop.entities.book.links;

import jakarta.persistence.*;
import lombok.*;
import ru.book_shop.entities.book.Book;

import java.time.LocalDateTime;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "book2user")
public class Book2User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "time")
    private LocalDateTime time;

    @Column(name = "type_id")
    private int typeId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id")
    @ToString.Exclude
    private Book book;

    @Column(name = "user_id")
    private int userId;
}
