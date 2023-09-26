package ru.book_shop.services.genres;

import ru.book_shop.dto.GenreTreeDTO;
import ru.book_shop.entities.genre.Genre;

import java.util.List;

public interface GenresService {
    List<GenreTreeDTO> getGenreTree();
    Genre getGenre(String slug);
    List<Genre> getBreadcrumbs(Integer genreId);
}
