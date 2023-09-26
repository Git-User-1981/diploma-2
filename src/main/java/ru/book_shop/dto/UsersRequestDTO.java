package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UsersRequestDTO {
    @Schema(description = "Номер страницы", type = "integer")
    @Min(value = 0, message = "api.error.book.offset")
    private Integer offset = 0;

    @Schema(description = "Количество пользователей на странице", type = "integer")
    @Min(value = 1, message = "api.error.users.limit")
    private Integer limit = 6;
}
