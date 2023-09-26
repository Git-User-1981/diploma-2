package ru.book_shop.dto;

import lombok.Builder;
import lombok.Data;
import ru.book_shop.entities.enums.UserType;

@Builder
@Data
public class UserShortDTO {
    private int id;
    private String hash;
    private String name;
    private UserType role;
    private int balance;
}
