package ru.book_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.book_shop.entities.book.file.FileDownload;

import java.util.Optional;

@Repository
public interface FileDownloadRepository extends JpaRepository<FileDownload, Integer> {
    Optional<FileDownload> findByBookIdAndUserId(Integer bookId, Integer userId);
}
