package ru.book_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import ru.book_shop.entities.user.UserSession;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Integer> {
    @Modifying
    void deleteByExpiredBefore(LocalDateTime dateTime);

    @Modifying
    void deleteByRefreshToken(String refreshToken);

    Optional<UserSession> findByRefreshToken(String refreshToken);
}
