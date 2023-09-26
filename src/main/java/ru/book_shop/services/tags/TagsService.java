package ru.book_shop.services.tags;

import ru.book_shop.dto.TagCloudDTO;
import ru.book_shop.entities.tags.Tag;

import java.util.List;

public interface TagsService {
    List<TagCloudDTO> getTagCloud();
    Tag getTag(String slug);
    List<Tag> getTagsByBookId(Integer bookId);
    List<Tag> getTagsList();
}
