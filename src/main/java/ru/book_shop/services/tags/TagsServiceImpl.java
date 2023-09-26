package ru.book_shop.services.tags;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.book_shop.dto.TagCloudDTO;
import ru.book_shop.exceptions.PageCallException;
import ru.book_shop.mappers.TagsMapper;
import ru.book_shop.entities.tags.Tag;
import ru.book_shop.repositories.TagRepository;

import java.util.List;

@Service
public class TagsServiceImpl implements TagsService {

    private final TagRepository tagRepository;

    public TagsServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public List<TagCloudDTO> getTagCloud() {
        return tagRepository.getTagsCloud().stream().map(TagsMapper::toDTO).toList();
    }

    @Override
    public Tag getTag(String slug) {
        return tagRepository.findBySlug(slug).orElseThrow(() -> new PageCallException("page.error.tag.not-found"));
    }

    @Override
    public List<Tag> getTagsByBookId(Integer bookId) {
        return tagRepository.getTagsByBookId(bookId);
    }

    @Override
    public List<Tag> getTagsList() {
        return tagRepository.findAll(Sort.by(Sort.Direction.ASC, "title"));
    }
}
