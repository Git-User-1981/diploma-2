package ru.book_shop.selenium;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MainPageTest {
    private static ChromeDriver driver;

    @BeforeAll
    static void setup() {
        System.setProperty("webdriver.chrome.driver", "E:/learning/java/java_spring_advanced/chromedriver-win64/chromedriver.exe");

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(5));
    }

    @AfterAll
    static void tearDown() {
        driver.quit();
    }

    @Test
    void mainPageAccess() throws InterruptedException {
        new MainPage(driver).callPage().pause();

        assertTrue(driver.getPageSource().contains("BOOKSHOP"));
    }

    @Test
    void sectionsTest() throws InterruptedException {
        new MainPage(driver)
                .callPage()
                .pause()
                .followLink("/genres")
                .pause()
                .followGenre()
                .pause()
                .followBook("Пугачёвщина (сборник документов)")
                .pause()
                .followLink("/books/recent")
                .pause()
                .followBook("Туркменская советская энциклопедия")
                .pause()
                .followLink("/books/popular")
                .pause()
                .followBook(2)
                .pause()
                .followLink("/authors")
                .pause()
                .followAuthor()
                .pause();

        assertTrue(driver.getPageSource().contains("Биография"));
    }
}
