package ru.book_shop.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class MainPage {
    private static final String URL_BOOKSHOP = "http://localhost:8085/";
    private final ChromeDriver driver;

    public MainPage(ChromeDriver driver) {
        this.driver = driver;
    }

    public MainPage callPage() {
        driver.get(URL_BOOKSHOP);
        return this;
    }

    public MainPage pause() throws InterruptedException {
        Thread.sleep(2000);
        return this;
    }
    public MainPage followLink(String uri) {
        driver.findElement(By.cssSelector(".menu_main a.menu-link[href=\"" + uri + "\"]")).click();
        return this;
    }

    public MainPage followGenre() {
        driver.findElement(By.cssSelector(".Tags.Tags_genres .Tag a")).click();
        return this;
    }

    public MainPage followBook(int index) {
        driver.findElements(By.cssSelector(".Cards .Card a.Card-picture")).get(index).click();
        return this;
    }

    public MainPage followAuthor() {
        driver.findElement(By.cssSelector(".Authors .Authors-item a")).click();
        return this;
    }

    public MainPage followBook(String text) {
        driver.findElements(By.linkText(text)).get(0).click();
        return this;
    }
}
