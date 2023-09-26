package ru.book_shop.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.book_shop.entities.book.links.Book2UserRecentViews;

@Repository
public interface Book2UserRecentViewsRepository extends JpaRepository<Book2UserRecentViews, Integer> {
}
