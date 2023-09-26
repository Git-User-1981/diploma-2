package ru.book_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.book_shop.dto.IBookReviewDTO;
import ru.book_shop.entities.book.review.BookReview;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<BookReview, Integer> {
    @Query(value = "select br.id as id, " +
                          "br.user_id as userId, " +
                          "br.user_name as userName, " +
                          "case when (u_like + u_dislike) > 0 then round((u_like / cast(u_like + u_dislike as numeric)) * 5) else 0 end as userRating, " +
                          "to_char(br.time, 'DD.MM.YYYY HH24:MI') as reviewTime, " +
                          "br.text as text, " +
                          "br.likes_count as likesCount, " +
                          "br.dislikes_count as dislikesCount, " +
                          "coalesce(br.u_rate, 0) as userLike " +
                     "from (select r.*, " +
                                  "u.name user_name, " +
                                  "(select count(1) from books_review_like l where l.review_id = r.id and l.value = 1) likes_count, " +
                                  "(select count(1) from books_review_like l where l.review_id = r.id and l.value = -1) dislikes_count, " +
                                  "(select count(brl.value) " +
                                     "from books_review ur " +
                                     "left join books_review_like brl on ur.id = brl.review_id and brl.value = 1 " +
                                    "where ur.user_id = u.id) as u_like, " +
                                  "(select count(brdl.value) " +
                                     "from books_review ur2 " +
                                     "left join books_review_like brdl on ur2.id = brdl.review_id and brdl.value = -1 " +
                                    "where ur2.user_id = u.id) as u_dislike, " +
                                  "(select brul.value from books_review_like brul where brul.review_id = r.id and brul.user_id = :userId) u_rate " +
                             "from books_review r " +
                             "join users u on r.user_id = u.id " +
                            "where r.book_id = :bookId) br " +
                    "order by (br.likes_count - br.dislikes_count) desc, br.time desc",
           nativeQuery = true
    )
    List<IBookReviewDTO> getReviewsByBookId(Integer bookId, Integer userId);
}
