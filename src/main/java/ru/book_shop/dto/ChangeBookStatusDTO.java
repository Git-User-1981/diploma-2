package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "Ответ на изменение статуса книги")
@Data
public class ChangeBookStatusDTO {
    @Schema(description = "Результат выполнения", example = "true")
    private boolean result = true;

    @Schema(description = "Количество отложенных книг", example = "2")
    int keptCounter = 0;

    @Schema(description = "Количество книг в корзине", example = "5")
    int cartCounter = 0;
}
