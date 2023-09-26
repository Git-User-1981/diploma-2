package ru.book_shop.mappers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.book_shop.dto.ITagCloudDTO;
import ru.book_shop.dto.TagCloudDTO;

@Component
public class TagsMapper {
    private static String tagsPath;

    @Value("${app.path.tag.page}")
    public void setTagsPath(String path){
        TagsMapper.tagsPath = path;
    }

    private TagsMapper(){}

    public static TagCloudDTO toDTO(ITagCloudDTO tag) {
        TagCloudDTO dto = new TagCloudDTO();
        dto.setTitle(tag.getTitle());
        dto.setPath(tagsPath + tag.getSlug());
        dto.setClss(tag.getClss());

        return dto;
    }
}
