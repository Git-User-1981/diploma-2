package ru.book_shop.dao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MenuItem {
    private String name;
    private String path;
    private String title;
    private String forRoles;
}
