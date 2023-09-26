package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "Ошибки")
@Setter
@Getter
public class ApiErrorResponseDTO {
    @Schema(description = "Результат выполнения", example = "false")
    private boolean result = false;

    @Schema(description = "Текст ошибки", example = "Сообщение об ошибке")
    private String error;

    public ApiErrorResponseDTO(String error) {
        this.error = error;
    }
}
