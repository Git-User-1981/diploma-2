package ru.book_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.book_shop.entities.other.Faq;

import java.util.List;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Integer> {
    List<Faq> findAllByOrderBySortIndexAsc();
}
