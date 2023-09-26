package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(name = "Пользователь")
@Builder
@Data
public class UsersDTO {
    @Schema(description = "Идентификатор пользователя")
    private Integer id;

    @Schema(description = "Имя пользователя")
    private String name;

    @Schema(description = "Дата регистрации")
    private String regTime;

    @Schema(description = "Роль пользователя")
    private String role;

    @Schema(description = "Номер телефона")
    private String phone;

    @Schema(description = "Email")
    private String email;

    @Schema(description = "Баланс")
    private Integer balance;
}
