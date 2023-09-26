package ru.book_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.book_shop.entities.author.Author;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Integer> {
    Optional<Author> findBySlug(String slug);

    @Query(value = "select a.* " +
                     "from authors a " +
                     "join book2author b2a on b2a.author_id = a.id " +
                    "where b2a.book_id = :bookId " +
                    "order by b2a.sort_index, a.name", nativeQuery = true)
    List<Author> getAuthorsByBookId(Integer bookId);
}
