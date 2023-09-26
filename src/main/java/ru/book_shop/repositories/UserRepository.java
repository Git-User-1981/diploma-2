package ru.book_shop.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.book_shop.dto.IUsersDTO;
import ru.book_shop.entities.enums.ContactType;
import ru.book_shop.entities.user.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsAllByContactsContactOrContactsContact(String phone, String email);
    Optional<User> findByContactsContactAndContactsType(String contact, ContactType type);
    Optional<User> getUserByHash(String hash);

    @Query(
            value = "select u.id, " +
                           "u.name, " +
                           "to_char(u.reg_time, 'DD.MM.YYYY HH24:MI:SS') regTime, " +
                           "u.role, " +
                           "(select uc.contact from users_contacts uc where u.id = uc.user_id and uc.type = 'PHONE') phone, " +
                           "(select uc.contact from users_contacts uc where u.id = uc.user_id and uc.type = 'EMAIL') email, " +
                           "u.balance " +
                      "from users u " +
                     "where lower(u.name) like '%' || lower(:query) || '%' " +
                     "order by u.name, u.reg_time",
            countQuery = "select count(1) from users u where lower(u.name) like '%' || lower(:query) || '%'",
            nativeQuery = true
    )
    Page<IUsersDTO> getUsersList(String query, Pageable nextPage);
}
