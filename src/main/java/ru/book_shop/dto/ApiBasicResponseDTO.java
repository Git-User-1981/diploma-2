package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "Базовый ответ")
@Setter
@Getter
public class ApiBasicResponseDTO {
    @Schema(description = "Результат выполнения", example = "true")
    private boolean result = true;
}
