package ru.book_shop.entities.book.links;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.book_shop.entities.book.Book;
import ru.book_shop.entities.genre.Genre;

import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "book2genre")
public class Book2Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id")
    @ToString.Exclude
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "genre_id")
    @ToString.Exclude
    private Genre genre;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book2Genre that = (Book2Genre) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(genre.getName(), that.genre.getName()) &&
                Objects.equals(book.getTitle(), that.book.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, genre.getName(), book.getTitle());
    }
}
