package ru.book_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.book_shop.entities.other.Message;

@Repository
public interface MessagesRepository extends JpaRepository<Message, Integer> {
}
