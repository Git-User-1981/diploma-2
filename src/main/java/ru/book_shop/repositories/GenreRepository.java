package ru.book_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.book_shop.dto.IGenreTreeDTO;
import ru.book_shop.entities.genre.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
    @Query(value = "with recursive r as (" +
                       "select g.id, " +
                              "g.name, " +
                              "g.slug, " +
                              "coalesce(g.parent_id, 0) as parent_id, " +
                              "0 as level " +
                         "from genres g " +
                        "where g.parent_id is null " +
                       "union all " +
                       "select g.id, " +
                              "g.name, " +
                              "g.slug, " +
                              "coalesce(g.parent_id, 0) as parent_id, " +
                              "r.level + 1 as level " +
                         "from genres g " +
                         "join r on g.parent_id = r.id" +
                   ") " +
                   "select r.id, " +
                          "r.name, " +
                          "r.slug, " +
                          "count(b2g.book_id) cnt, " +
                          "r.parent_id parent " +
                     "from r " +
                     "join book2genre b2g on b2g.genre_id = r.id " +
                    "group by r.id, r.name, r.slug, r.parent_id, r.level " +
                    "order by r.parent_id, r.level, cnt desc, r.name", nativeQuery = true)
    List<IGenreTreeDTO> getGenresTree();

    @Query(value = "with recursive r as (" +
                       "select g.* " +
                         "from genres g " +
                        "where g.id = :genreId " +
                        "union all\n" +
                       "select g.* " +
                         "from genres g " +
                         "join r on r.parent_id = g.id" +
                   ") " +
                   "select * " +
                     "from r " +
                    "where r.id != :genreId " +
                    "order by id", nativeQuery = true)
    List<Genre> getBreadCrumbs(Integer genreId);

    Optional<Genre> findBySlug(String slug);
}
