package ru.book_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.book_shop.entities.user.UserContact;

import java.util.Optional;

@Repository
public interface UserContactRepository extends JpaRepository<UserContact, Integer> {
    Optional<UserContact> findByContactAndUserId(String contact, Integer id);
}
