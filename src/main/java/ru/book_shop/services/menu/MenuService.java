package ru.book_shop.services.menu;

import ru.book_shop.dao.MenuItem;

import java.util.List;

public interface MenuService {
    List<MenuItem> getMenuItems();
    int[] getReservedBooksCount();
}
