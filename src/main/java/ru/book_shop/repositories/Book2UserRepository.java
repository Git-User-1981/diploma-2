package ru.book_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.book_shop.dto.IUserBooksCommonDTO;
import ru.book_shop.entities.book.links.Book2User;

import java.util.List;
import java.util.Optional;

@Repository
public interface Book2UserRepository extends JpaRepository<Book2User, Integer> {
    Optional<Book2User> findByUserIdAndBookId(Integer userId, Integer bookId);
    List<Book2User> findAllByUserIdAndTypeId(Integer userId, Integer typeId);

    @Modifying
    @Query(
            value = "delete from book2user bu " +
                     "where bu.time < now() - make_interval(secs => :maxAge) " +
                       "and bu.type_id in (1, 2)",
            nativeQuery = true
    )
    void deleteOldReservedBooks(Integer maxAge);

    @Query(
            value = "select but.code type, " +
                           "count(bu.type_id) num " +
                      "from book2user bu " +
                      "join book2user_type but on but.id = bu.type_id " +
                     "where bu.user_id = :userId " +
                     "group by but.code",
            nativeQuery = true
    )
    List<IUserBooksCommonDTO> getUserBooksCount(Integer userId);

    @Query(
            value = "select but.code type, " +
                           "bu.book_id num " +
                      "from book2user bu " +
                      "join book2user_type but on but.id = bu.type_id " +
                     "where bu.user_id = :userId " +
                     "order by bu.time desc",
            nativeQuery = true
    )
    List<IUserBooksCommonDTO> getUserBooks(Integer userId);

    @Query(
            value = "select but.code type, " +
                           "bu.book_id num " +
                      "from book2user bu " +
                      "join book2user_type but on but.id = bu.type_id " +
                     "where bu.type_id in (1, 2) " +
                       "and bu.user_id = :userId " +
                     "order by bu.time desc",
            nativeQuery = true
    )
    List<IUserBooksCommonDTO> getUserReservedBooks(Integer userId);

    @Query(
            value = "select sum(case when b.discount > 0 then b.price - (b.price * b.discount / 100) else b.price end) as total " +
                      "from book2user b2u " +
                      "join books b on b2u.book_id = b.id " +
                     "where b2u.type_id = 2 " +
                       "and b2u.user_id = :userId",
            nativeQuery = true
    )
    int getCartTotalAmount(Integer userId);
}
