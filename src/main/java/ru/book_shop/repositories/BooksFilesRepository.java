package ru.book_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.book_shop.dto.IBookFilesDTO;
import ru.book_shop.entities.book.Book;
import ru.book_shop.entities.book.file.BookFile;

import java.util.List;
import java.util.Optional;

@Repository
public interface BooksFilesRepository extends JpaRepository<BookFile, Integer> {
    @Query(value = "select f.hash, " +
                          "f.path, " +
                          "ft.name as type, " +
                          "ft.description " +
                     "from books_files f " +
                     "join books_file_type ft on ft.id = f.type_id " +
                    "where f.book_id = :bookId " +
                    "order by ft.name",
           nativeQuery = true
    )
    List<IBookFilesDTO> getBookFiles(Integer bookId);

    @Query(
            value = "select bf.* " +
                      "from books_files bf " +
                      "join book2user b2u on b2u.book_id = bf.book_id " +
                     "where b2u.type_id in (3, 4) " +
                       "and bf.hash = :hash " +
                       "and b2u.user_id = :userId",
            nativeQuery = true
    )
    Optional<BookFile> getUserBookFile(String hash, Integer userId);

    @Query(
            value = "select bft.id " +
                    "from books_file_type bft " +
                   "where lower(bft.name) = lower(:name)",
            nativeQuery = true
    )
    Optional<Integer> getFileTypeIdByName(String name);

    Optional<BookFile> getBookFileByBookAndFileTypeId(Book book, Integer bookFileTypeId);
}
