package ru.book_shop.entities.payments;

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
@Table(name = "balance_transactions")
public class BalanceTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "time")
    private LocalDateTime time;

    @Column(name = "value")
    private int value;

    @Column(name = "book_id")
    private Integer bookId;

    @Column(name = "description")
    private String description;
}
