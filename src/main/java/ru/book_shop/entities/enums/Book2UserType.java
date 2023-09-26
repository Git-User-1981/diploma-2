package ru.book_shop.entities.enums;

import java.util.Arrays;

public enum Book2UserType {
    KEPT(1),
    CART(2),
    PAID(3),
    ARCHIVED(4);

    public final int id;

    Book2UserType(int id) {
        this.id = id;
    }

    public static int getIdTypeByCode(String code) {
        return Arrays.stream(Book2UserType.values())
                .filter(t -> t.toString().equals(code))
                .findFirst().map(t -> t.id).orElse(0);
    }
}
