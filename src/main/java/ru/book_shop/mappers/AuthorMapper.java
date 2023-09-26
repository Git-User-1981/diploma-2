package ru.book_shop.mappers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.book_shop.dto.AuthorDTO;
import ru.book_shop.entities.author.Author;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class AuthorMapper {
    private static String photosPath;
    private static String photosLocationPath;
    private static String photoDefault;
    private static Integer descriptionLength;

    @Value("${app.path.authors.photo}")
    public void setPhotoPath(String path){
        AuthorMapper.photosPath = path;
    }

    @Value("${app.path.upload.authors}")
    public void setPhotoLocationPath(String path){
        AuthorMapper.photosLocationPath = path;
    }

    @Value("${app.author.biography.short}")
    public void setPhotoPath(Integer length){
        AuthorMapper.descriptionLength = length;
    }

    @Value("${app.author.photo.default}")
    public void setPhotoDefault(String photo){
        AuthorMapper.photoDefault = photo;
    }

    private AuthorMapper(){}

    public static AuthorDTO toDTO(Author author) {
        AuthorDTO dto = new AuthorDTO();
        dto.setId(author.getId());
        dto.setName(author.getName());
        dto.setSlug(author.getSlug());
        dto.setPhoto(getImagePath(author.getPhoto()));

        String description = author.getDescription();
        if (description.length() > descriptionLength) {
            dto.setDescriptionShort(description.substring(0, descriptionLength));
            dto.setDescriptionFull(description.substring(descriptionLength));
        }
        else {
            dto.setDescriptionShort(description);
        }

        return dto;
    }

    private static String getImagePath(String imageName) {
        return imageName != null && Files.exists(Path.of(photosLocationPath + imageName)) ? photosPath + imageName : photoDefault;
    }
}
