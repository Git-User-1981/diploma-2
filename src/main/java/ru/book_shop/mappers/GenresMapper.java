package ru.book_shop.mappers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.book_shop.dto.GenreTreeDTO;
import ru.book_shop.dto.IGenreTreeDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class GenresMapper {
    private static String genresPath;

    private GenresMapper() {}

    @Value("${app.link.genres}")
    public void setGenresPath(String path){
        GenresMapper.genresPath = path + '/';
    }

    public static List<GenreTreeDTO> toGenreTreeDTO(List<IGenreTreeDTO> iGenreList, Integer parentId) {
        List<GenreTreeDTO> list = new ArrayList<>();
        for (IGenreTreeDTO iGenre : iGenreList) {
            if (Objects.equals(iGenre.getParent(), parentId)) {
                GenreTreeDTO dto = new GenreTreeDTO();
                dto.setName(iGenre.getName());
                dto.setPath(genresPath + iGenre.getSlug());
                dto.setBookCount(iGenre.getCnt());
                dto.setChildren(toGenreTreeDTO(iGenreList, iGenre.getId()));
                list.add(dto);
            }
        }
        return list;
    }
}
