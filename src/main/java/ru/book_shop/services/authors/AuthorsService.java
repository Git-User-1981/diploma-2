package ru.book_shop.services.authors;

import org.springframework.web.multipart.MultipartFile;
import ru.book_shop.dto.AuthorDTO;
import ru.book_shop.entities.author.Author;

import java.util.List;
import java.util.Map;

public interface AuthorsService {
    Map<Character, List<Author>> getAuthorsData();
    AuthorDTO getAuthor(String slug);
    List<AuthorDTO> getAuthorsByBookId(Integer bookId);
    void savePhoto(String slug, MultipartFile photoFile);
    List<AuthorDTO> getAuthorsList();
}
