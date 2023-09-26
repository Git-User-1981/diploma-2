package ru.book_shop.services.payments.yoo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YooPaymentDto {
    private String id;
    private String status;
    private YooAmountDto amount;
    @JsonProperty("payment_method_data")
    private YooPaymentMethodDataDto paymentMethodData;
    private YooConfirmationDto confirmation;
    private Boolean capture;
    private Boolean paid;
    private String description;
}
