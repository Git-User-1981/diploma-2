package ru.book_shop.services.genres;

import org.springframework.stereotype.Service;
import ru.book_shop.dto.GenreTreeDTO;
import ru.book_shop.exceptions.PageCallException;
import ru.book_shop.mappers.GenresMapper;
import ru.book_shop.entities.genre.Genre;
import ru.book_shop.repositories.GenreRepository;

import java.util.List;

@Service
public class GenresServiceImpl implements GenresService {
    private final GenreRepository genreRepository;

    public GenresServiceImpl(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public List<GenreTreeDTO> getGenreTree() {
        return GenresMapper.toGenreTreeDTO(genreRepository.getGenresTree(), 0);
    }

    @Override
    public Genre getGenre(String slug) {
        return genreRepository.findBySlug(slug).orElseThrow(() -> new PageCallException("page.error.genre.not-found"));
    }

    @Override
    public List<Genre> getBreadcrumbs(Integer genreId) {
        return genreRepository.getBreadCrumbs(genreId);
    }
}
