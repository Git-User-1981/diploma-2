package ru.book_shop.entities.user;

import lombok.*;
import ru.book_shop.entities.enums.ContactType;

import jakarta.persistence.*;

import java.io.Serializable;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "users_contacts")
public class UserContact implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ContactType type;

    @Column(name = "approved")
    private int approved;

    @Column(name = "contact")
    private String contact;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;
}
