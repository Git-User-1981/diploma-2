package ru.book_shop.services.payments;

import ru.book_shop.dto.RegisterPaymentRequestDTO;
import ru.book_shop.dto.RegisterPaymentResponseDTO;

public interface Payments {
    RegisterPaymentResponseDTO registerPayment(RegisterPaymentRequestDTO request);
    void completePayment(String hash, Integer sum);
}
