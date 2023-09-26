package ru.book_shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "Результат регистрации платежа")
@Setter
@Getter
@Builder
public class RegisterPaymentResponseDTO {
    @Schema(description = "Результат регистрации", example = "true")
    private boolean result;

    @Schema(description = "Ссылка для перехода в платежную систему", example = "https://yoomoney.ru/payments/external/confirmation?orderId=22c5d173")
    private String paymentUrl;
}
