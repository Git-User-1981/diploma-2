package ru.book_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.book_shop.entities.user.UserConfirmation;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserConfirmationRepository extends JpaRepository<UserConfirmation, Integer> {
    void deleteAllByCodeTimeBefore(LocalDateTime codeTime);
    Optional<UserConfirmation> findByContact(String contact);
    Optional<UserConfirmation> findByHashAndCodeTrailsLessThan(String hash, Integer codeTrails);
    Optional<UserConfirmation> findByContactAndCodeTimeAfter(String contact, LocalDateTime codeTime);
}
