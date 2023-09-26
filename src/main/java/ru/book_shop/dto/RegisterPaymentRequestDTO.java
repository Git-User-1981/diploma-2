package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(name = "Запрос на пополнение счета пользователя")
@Data
public class RegisterPaymentRequestDTO {
    @Schema(description = "Хеш пользователя", example = "1111-1we3-fd-e43")
    @NotEmpty(message = "api.error.payment.hash")
    private String hash;

    @Schema(description = "Оценка книги", example = "100")
    @Min(value = 100, message = "api.error.payment.sum")
    @NotNull(message = "api.error.payment.sum")
    private Integer sum;
}
