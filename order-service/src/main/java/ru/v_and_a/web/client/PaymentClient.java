package ru.v_and_a.web.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.v_and_a.domain.model.Order;
import ru.v_and_a.web.client.feign.PaymentFeignClient;
import ru.v_and_a.web.dto.OrderRequest;
import ru.v_and_a.web.dto.PaymentRequest;
import ru.v_and_a.web.dto.PaymentResponse;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentClient {

    private final PaymentFeignClient paymentFeignClient;

    @CircuitBreaker(name = "paymentClient")
    @Retry(name = "paymentClient")
    public PaymentResponse createPayment(String idempotencyKey, PaymentRequest request) {
        log.info("Вызов PaymentClient.createPayment: ", idempotencyKey);
        return paymentFeignClient.createPayment(idempotencyKey, request);
    }

    // Остальные методы можно добавить по аналогии (если нужны)
    public PaymentResponse getPaymentById(Long paymentId) {
        return paymentFeignClient.getPaymentById(paymentId);
    }

    public PaymentResponse updatePayment(Long paymentId, PaymentRequest request) {
        return paymentFeignClient.updatePayment(paymentId, request);
    }

    public PaymentResponse partialUpdatePayment(Long paymentId, PaymentRequest request) {
        return paymentFeignClient.partialUpdatePayment(paymentId, request);
    }

    public void deletePayment(Long paymentId) {
        paymentFeignClient.deletePayment(paymentId);
    }
}
