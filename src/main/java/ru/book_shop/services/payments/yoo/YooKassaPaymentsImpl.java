package ru.book_shop.services.payments.yoo;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.book_shop.dto.RegisterPaymentRequestDTO;
import ru.book_shop.dto.RegisterPaymentResponseDTO;
import ru.book_shop.entities.payments.BalanceTransaction;
import ru.book_shop.entities.user.User;
import ru.book_shop.exceptions.ApiCallException;
import ru.book_shop.exceptions.PageCallException;
import ru.book_shop.repositories.BalanceTransactionsRepository;
import ru.book_shop.repositories.UserRepository;
import ru.book_shop.services.payments.Payments;
import ru.book_shop.services.payments.yoo.dto.YooAmountDto;
import ru.book_shop.services.payments.yoo.dto.YooConfirmationDto;
import ru.book_shop.services.payments.yoo.dto.YooPaymentDto;
import ru.book_shop.services.payments.yoo.dto.YooPaymentMethodDataDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class YooKassaPaymentsImpl implements Payments {
    @Value("${payment.url}")
    private String paymentUrl;

    @Value("${payment.shop-id}")
    private String paymentShopId;

    @Value("${payment.key}")
    private String paymentKey;

    private final RestTemplate restTemplate;
    private final MessageSource messageSource;
    private final UserRepository userRepository;
    private final BalanceTransactionsRepository balanceTransactionsRepository;

    @Override
    public RegisterPaymentResponseDTO registerPayment(RegisterPaymentRequestDTO request) {
        final ResponseEntity<YooPaymentDto> responseFull = restTemplate.exchange(
                paymentUrl,
                HttpMethod.POST,
                createPaymentRequestBody(request),
                YooPaymentDto.class
        );

        if (responseFull.getStatusCode() == HttpStatus.OK) {
            YooPaymentDto response = responseFull.getBody();
            if (response!= null && response.getStatus().equals("pending")) {
                return RegisterPaymentResponseDTO.builder()
                        .result(true)
                        .paymentUrl(response.getConfirmation().getConfirmationUrl())
                        .build();
            }
        }

        throw new ApiCallException("api.error.payment.register.error");
    }

    @Override
    public void completePayment(String hash, Integer sum) {
        final User user = userRepository.getUserByHash(hash).orElseThrow(() -> new PageCallException("api.error.login.user-not-found"));
        user.setBalance(user.getBalance() + sum);
        userRepository.save(user);

        BalanceTransaction transaction = BalanceTransaction
                .builder()
                .userId(user.getId())
                .bookId(null)
                .value(sum)
                .description(messageSource.getMessage(
                        "api.error.payment.register.message",
                        new Object[]{sum},
                        LocaleContextHolder.getLocale()
                ))
                .time(LocalDateTime.now())
                .build();
        balanceTransactionsRepository.save(transaction);
    }

    private HttpEntity<YooPaymentDto> createPaymentRequestBody(RegisterPaymentRequestDTO request){
        final YooPaymentDto payment = YooPaymentDto.builder()
                .amount(
                        YooAmountDto.builder()
                        .currency("RUB")
                        .value(request.getSum().toString())
                        .build()
                )
                .paymentMethodData(
                        YooPaymentMethodDataDto.builder()
                        .type("bank_card")
                        .build()
                )
                .confirmation(
                        YooConfirmationDto.builder()
                        .type("redirect")
                        .returnUrl("http://localhost:8085/complete/" + request.getHash() + "/" + request.getSum())
                        .build()
                )
                .description(messageSource.getMessage(
                        "api.error.payment.register.message",
                        new Object[]{request.getSum()},
                        LocaleContextHolder.getLocale()
                ))
                .capture(true)
                .build();

        return new HttpEntity<>(payment, createRequestHttpHeaders());
    }

    private HttpHeaders createRequestHttpHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setBasicAuth(paymentShopId, paymentKey);
        headers.add("Idempotence-Key", UUID.randomUUID().toString());
        return headers;
    }
}
