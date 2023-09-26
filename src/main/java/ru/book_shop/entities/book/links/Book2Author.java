package ru.book_shop.entities.book.links;

import jakarta.persistence.*;
import lombok.*;
import ru.book_shop.entities.author.Author;
import ru.book_shop.entities.book.Book;

import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "book2author")
public class Book2Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sort_index")
    private Integer sortIndex;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id")
    @ToString.Exclude
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id")
    @ToString.Exclude
    private Book book;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book2Author that = (Book2Author) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(sortIndex, that.sortIndex) &&
                Objects.equals(author.getName(), that.author.getName()) &&
                Objects.equals(book.getTitle(), that.book.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, sortIndex, author.getName(), book.getTitle());
    }
}
