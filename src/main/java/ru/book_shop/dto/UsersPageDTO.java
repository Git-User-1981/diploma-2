package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Schema(name = "Список пользователей")
@Builder
@Data
public class UsersPageDTO {
    @Schema(description = "Всего пользователей")
    private Long count;
    private List<UsersDTO> users;
}
