package ru.book_shop.services.authors;

import org.apache.commons.collections4.map.LinkedMap;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;
import ru.book_shop.dto.AuthorDTO;
import ru.book_shop.exceptions.PageCallException;
import ru.book_shop.mappers.AuthorMapper;
import ru.book_shop.entities.author.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.book_shop.repositories.AuthorRepository;
import ru.book_shop.services.storage.FileStorageService;
import ru.book_shop.services.storage.StorageType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthorsServiceImpl implements AuthorsService {
    private final AuthorRepository authorRepository;
    private final FileStorageService storageService;

    @Autowired
    public AuthorsServiceImpl(AuthorRepository authorRepository, FileStorageService storageService) {
        this.authorRepository = authorRepository;
        this.storageService = storageService;
    }

    @Override
    public Map<Character, List<Author>> getAuthorsData() {
        return authorRepository
            .findAll(Sort.by(Sort.Direction.ASC, "name"))
            .stream()
            .collect(Collectors.groupingBy(a -> Character.toLowerCase(a.getName().charAt(0)), LinkedMap::new, Collectors.toList()));
    }

    @Override
    public AuthorDTO getAuthor(String slug) {
        return AuthorMapper.toDTO(authorRepository.findBySlug(slug).orElseThrow(() -> new PageCallException("page.error.author.not-found")));
    }

    @Override
    public List<AuthorDTO> getAuthorsByBookId(Integer bookId) {
        return authorRepository.getAuthorsByBookId(bookId).stream().map(AuthorMapper::toDTO).toList();
    }

    @Override
    public void savePhoto(String slug, MultipartFile photoFile) {
        Author author = authorRepository.findBySlug(slug).orElseThrow(() -> new PageCallException("page.error.author.not-found"));
        String coverName = storageService.saveFile(photoFile, StorageType.PHOTO);
        author.setPhoto(coverName);
        authorRepository.save(author);
    }

    @Override
    public List<AuthorDTO> getAuthorsList() {
        return authorRepository.findAll(Sort.by(Sort.Direction.ASC, "name")).stream().map(AuthorMapper::toDTO).toList();
    }
}
