package ru.book_shop.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserProfileDTO {
    private int id;
    private String name;
    private String phone;
    private int phoneApproved;
    private String email;
    private int emailApproved;
}
