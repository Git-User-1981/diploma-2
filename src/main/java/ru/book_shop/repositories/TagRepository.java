package ru.book_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.book_shop.dto.ITagCloudDTO;
import ru.book_shop.entities.tags.Tag;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {
    @Query(value = "with grps as (select min(c.tg_cnt) min_cnt, " +
                                        "(max(c.tg_cnt) - min(c.tg_cnt)) / 5 step " +
                                   "from (select count(bt.tag_id) tg_cnt " +
                                           "from book2tag bt " +
                                          "group by bt.tag_id) c" +
                                "), " +
                                "tags_cnt as (select bt2.tag_id, " +
                                                    "count(bt2.tag_id) cnt " +
                                               "from book2tag bt2 " +
                                              "group by bt2.tag_id" +
                                ") " +
                   "select t.title, " +
                          "t.slug, " +
                          "case " +
                              "when tc.cnt >= (g.min_cnt + g.step * 4) then 'Tag_lg' " +
                              "when tc.cnt >= (g.min_cnt + g.step * 3) then 'Tag_md' " +
                              "when tc.cnt >= (g.min_cnt + g.step * 2) then '' " +
                              "when tc.cnt >= (g.min_cnt + g.step * 1) then 'Tag_sm' " +
                              "else 'Tag_xs' " +
                          "end clss " +
                     "from grps g, tags_cnt tc " +
                    "right join tags t on t.id = tc.tag_id " +
                    "order by t.id",
           nativeQuery = true
    )
    List<ITagCloudDTO> getTagsCloud();

    Optional<Tag> findBySlug(String slug);

    @Query(value = "select t.* " +
                     "from tags t " +
                     "join book2tag b2t on b2t.tag_id = t.id " +
                    "where b2t.book_id = :bookId " +
                    "order by t.title",
           nativeQuery = true
    )
    List<Tag> getTagsByBookId(Integer bookId);
}
