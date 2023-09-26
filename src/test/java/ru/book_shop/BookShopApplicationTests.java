package ru.book_shop;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BookShopApplicationTests {
    private final BookShopApplication application;

    @Autowired
    BookShopApplicationTests(BookShopApplication application) {
        this.application = application;
    }

    @Test
    void contextLoads() {
        assertNotNull(application);
    }

}
