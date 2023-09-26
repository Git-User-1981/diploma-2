package ru.book_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.book_shop.entities.other.Document;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentsRepository extends JpaRepository<Document, Integer> {
    List<Document> findAllByOrderBySortIndexAsc();
    Optional<Document> findBySlug(String slug);
}
