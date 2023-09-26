package ru.book_shop.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.book_shop.dto.IBookCardDTO;
import ru.book_shop.dto.IBookPageDTO;
import ru.book_shop.entities.book.Book;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    String BOOK_FIELDS = "b.id, " +
                         "b.title, " +
                         "b.image, " +
                         "b.slug, " +
                         "b.is_bestseller as isBestseller, " +
                         "case when b.discount > 0 then b.price - (b.price * b.discount / 100) else b.price end as price, " +
                         "case when b.discount > 0 then b.price else 0 end as discountPrice, " +
                         "b.discount, " +
                         "coalesce((select round(avg(br.value)) from books_ratings br where br.book_id = b.id group by br.book_id), 0) as rating, ";
    String BOOK_CARD_FIELDS = BOOK_FIELDS +
            "coalesce((select case when a2.several then a2.name || '#moreTxt#' else a2.name end " +
               "from (select a.name, " +
                            "row_number() over (partition by b2a.book_id order by b2a.sort_index, a.name) rn, " +
                            "(select count(author_id) > 1 from book2author where book_id = b.id) several " +
                               "from authors a " +
                               "join book2author b2a on b2a.author_id = a.id " +
                              "where b2a.book_id = b.id) a2 " +
              "where a2.rn = 1), '') as authors ";

    @Query(value = "select " + BOOK_FIELDS +
                          "b.description " +
                     "from books b " +
                    "where b.slug = :slug",
           nativeQuery = true
    )
    Optional<IBookPageDTO> getBookBySlug(String slug);

    @Query(value = "select " + BOOK_CARD_FIELDS +
                     "from books b " +
                     "left join public.book2author b2a2 on b.id = b2a2.book_id " +
                     "left join authors a3 on a3.id = b2a2.author_id " +
                    "where lower(b.title || coalesce(a3.name, '')) like '%' || lower(:query) || '%' " +
                    "group by b.id, b.pub_date, b.title " +
                    "order by b.pub_date desc, b.title",
           countQuery = "select count(1) " +
                          "from books b " +
                          "left join public.book2author b2a2 on b.id = b2a2.book_id " +
                          "left join authors a3 on a3.id = b2a2.author_id " +
                         "where lower(b.title || coalesce(a3.name, '')) like '%' || lower(:query) || '%' " +
                         "group by b.id, b.pub_date, b.title",
           nativeQuery = true
    )
    Page<IBookCardDTO> getBooksSearchResult(String query, Pageable nextPage);

    @Query(value = "select " + BOOK_CARD_FIELDS +
                     "from books b " +
                    "order by rating desc, b.pub_date desc",
          countQuery = "select count(1) from books",
          nativeQuery = true
    )
    Page<IBookCardDTO> getRecommendedBooks(Pageable nextPage);

    @Query(value = "with " +
                       "client_books as ( " +
                           "select b.id, " +
                                  "b.title " +
                             "from books b " +
                            "where b.id in :userBookIds" +
                       "), " +
                       "pref_author as ( " +
                           "select distinct ba.author_id " +
                             "from book2author ba " +
                            "where ba.book_id in (select id from client_books)" +
                       "), " +
                       "pref_tag as ( " +
                           "select distinct bt.tag_id " +
                             "from book2tag bt " +
                            "where bt.book_id in (select id from client_books)" +
                       ")," +
                       "pref_genre as ( " +
                           "select distinct bg.genre_id " +
                             "from book2genre bg " +
                            "where bg.book_id in (select id from client_books)" +
                       ") " +
                   "select " + BOOK_CARD_FIELDS +
                     "from books b " +
                    "where b.id not in (select id from client_books) " +
                      "and ( " +
                           "b.id in (select distinct b2a.book_id " +
                                      "from pref_author pa " +
                                      "join book2author b2a on b2a.author_id = pa.author_id) " +
                           "or " +
                           "b.id in (select distinct b2t.book_id " +
                                      "from pref_tag pt " +
                                      "join book2tag b2t on b2t.tag_id = pt.tag_id) " +
                           "or " +
                           "b.id in (select distinct b2g.book_id " +
                                      "from pref_genre pg " +
                                      "join book2genre b2g on b2g.genre_id = pg.genre_id) " +
                      ") " +
                    "order by b.pub_date desc, rating desc",
            countQuery = "with " +
                             "client_books as ( " +
                                 "select b.id, " +
                                        "b.title " +
                                   "from books b " +
                                  "where b.id in :userBookIds" +
                             "), " +
                             "pref_author as ( " +
                                 "select distinct ba.author_id " +
                                   "from book2author ba " +
                                  "where ba.book_id in (select id from client_books)" +
                             "), " +
                             "pref_tag as ( " +
                                 "select distinct bt.tag_id " +
                                   "from book2tag bt " +
                                  "where bt.book_id in (select id from client_books)" +
                             ")," +
                             "pref_genre as ( " +
                                 "select distinct bg.genre_id " +
                                   "from book2genre bg " +
                                  "where bg.book_id in (select id from client_books)" +
                             ") " +
                             "select count(1) " +
                               "from books b " +
                              "where b.id not in (select id from client_books) " +
                                "and ( " +
                                     "b.id in (select distinct b2a.book_id " +
                                                "from pref_author pa " +
                                                "join book2author b2a on b2a.author_id = pa.author_id) " +
                                     "or " +
                                     "b.id in (select distinct b2t.book_id " +
                                                "from pref_tag pt " +
                                                "join book2tag b2t on b2t.tag_id = pt.tag_id) " +
                                     "or " +
                                     "b.id in (select distinct b2g.book_id " +
                                                "from pref_genre pg " +
                                                "join book2genre b2g on b2g.genre_id = pg.genre_id) " +
                                     ")",
            nativeQuery = true
    )
    Page<IBookCardDTO> getRecommendedBooksUser(Set<Integer> userBookIds, Pageable nextPage);

    @Query(value = "select " + BOOK_CARD_FIELDS +
                     "from books b " +
                    "where b.pub_date between :from and :to " +
                    "order by b.pub_date desc, b.title",
           countQuery = "select count(1) " +
                          "from books b " +
                         "where b.pub_date between :from and :to",
           nativeQuery = true
    )
    Page<IBookCardDTO> getRecentBooks(LocalDate from, LocalDate to, Pageable nextPage);

    @Query(value = "select " + BOOK_CARD_FIELDS +
                     "from books b " +
                     "left join book2user ub on ub.book_id = b.id and ub.type_id = 3 " +
                     "left join book2user uc on uc.book_id = b.id and uc.type_id = 2 " +
                     "left join book2user uk on uk.book_id = b.id and uk.type_id = 1 " +
                     "left join book2user_recent_views brv on brv.book_id = b.id " +
                    "group by b.id, b.title " +
                    "order by count(ub.id) + count(uc.id) * 0.7 + count(uk.id) * 0.4 + count(brv.id) * 0.3 desc, b.title",
           countQuery = "select count(1) from books",
           nativeQuery = true
    )
    Page<IBookCardDTO> getPopularBooks(Pageable nextPage);

    @Query(value = "select " + BOOK_CARD_FIELDS +
                     "from books b " +
                     "join book2user_recent_views brv on brv.book_id = b.id " +
                    "where brv.user_id = :userId " +
                    "group by b.id " +
                    "order by count(brv.book_id) desc, max(brv.time) desc",
            countQuery = "select count(1) from book2user_recent_views brv where brv.user_id = :userId",
            nativeQuery = true
    )
    Page<IBookCardDTO> getViewsBooks(Integer userId, Pageable nextPage);

    @Query(value = "select " + BOOK_CARD_FIELDS +
                     "from book2tag bt " +
                     "join books b on b.id = bt.book_id " +
                    "where bt.tag_id = :tagId " +
                    "order by b.pub_date desc, b.title",
           countQuery = "select count(1) "+
                          "from book2tag bt " +
                          "join books b on b.id = bt.book_id " +
                         "where bt.tag_id = :tagId",
           nativeQuery = true
    )
    Page<IBookCardDTO> getBooksByTagId(Integer tagId, Pageable nextPage);

    @Query(value = "select " + BOOK_CARD_FIELDS +
                     "from book2genre b2g " +
                     "join books b on b.id = b2g.book_id " +
                    "where b2g.genre_id = :genreId " +
                    "order by b.pub_date desc, b.title",
           countQuery = "select count(1) " +
                          "from book2genre b2g " +
                          "join books b on b.id = b2g.book_id " +
                         "where b2g.genre_id = :genreId",
           nativeQuery = true
    )
    Page<IBookCardDTO> getBooksByGenreId(Integer genreId, Pageable nextPage);

    @Query(value = "select " + BOOK_CARD_FIELDS +
                     "from books b " +
                     "join book2author b2a on b2a.book_id = b.id " +
                    "where b2a.author_id = :authorId " +
                    "order by b.pub_date desc, b.title",
           countQuery = "select count(1) "+
                          "from books b " +
                          "join book2author b2a on b2a.book_id = b.id " +
                         "where b2a.author_id = :authorId",
           nativeQuery = true
    )
    Page<IBookCardDTO> getBooksByAuthorId(Integer authorId, Pageable nextPage);

    Optional<Book> findBySlug(String slug);


    @Query(value = "select " + BOOK_FIELDS +
                           "'' as description " +
                     "from books b " +
                    "where b.id in (:booksId) " +
                    "order by b.title",
           nativeQuery = true
    )
    List<IBookPageDTO> getBooksByIds(List<Integer> booksId);


    @Query(value = "with stars_seq as (select generate_series(1, 5) as value), " +
                        "votes as (select b.value from books_ratings b where b.book_id = :bookId) " +
                   "select coalesce(count(votes.value), 0) as votes " +
                     "from stars_seq " +
                     "left join votes on votes.value = stars_seq.value " +
                    "group by stars_seq.value " +
                    "order by stars_seq.value",
           nativeQuery = true
    )
    List<Integer> getBookRatingStat(Integer bookId);

    @Query(value = "select " + BOOK_CARD_FIELDS +
                      "from books b " +
                      "join book2user bu on bu.book_id = b.id " +
                      "join book2user_type but on bu.type_id = but.id " +
                     "where bu.user_id = :userId and but.code = :statusCode",
           nativeQuery = true
    )
    List<IBookCardDTO> getUserBooksByStatus(Integer userId, String statusCode);

    @Query(
            value = "select br.value " +
                      "from books_ratings br " +
                     "where br.user_id = :userId and br.book_id = :bookId",
            nativeQuery = true
    )
    Optional<Integer> getBookUserRating(Integer userId, Integer bookId);

    @Modifying
    @Query(
            value = "insert into books_ratings (book_id, user_id, value) values (:bookId, :userId, :rating)",
            nativeQuery = true
    )
    void setBookRating(Integer bookId, Integer userId, Integer rating);
}
