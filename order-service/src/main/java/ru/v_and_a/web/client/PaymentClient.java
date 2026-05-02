package ru.v_and_a.web.client;

import feign.RetryableException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.v_and_a.web.client.feign.PaymentFeignClient;
import ru.v_and_a.web.dto.PaymentRequest;
import ru.v_and_a.web.dto.PaymentResponse;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentClient {

    private final PaymentFeignClient paymentFeignClient;

    @Retry(name = "paymentClient", fallbackMethod = "createPaymentFallback")
    @RateLimiter(name = "paymentClient")
    @Bulkhead(name = "paymentClient")
    @CircuitBreaker(name = "paymentClient")
    public PaymentResponse createPayment(String idempotencyKey, PaymentRequest request) {
        log.info("Вызов PaymentClient.createPayment: {}", idempotencyKey);
        return paymentFeignClient.createPayment(idempotencyKey, request);
    }

    public PaymentResponse createPaymentFallback(String idempotencyKey, PaymentRequest request, Throwable throwable) {
        String status;
        String message;

        if (throwable instanceof RequestNotPermitted) {
            String msg = throwable.getMessage();
            if (msg.contains("Bulkhead")) {
                status = "BULKHEAD_REJECTED";
                message = "Слишком много параллельных операций. Сервис временно недоступен из-за ограничения нагрузки.";
            } else {
                status = "REJECTED";
                message = "Запрос отклонён из-за ограничения ресурсов.";
            }
            log.warn("Request rejected by RateLimiter or Bulkhead: {}", message);

        } else if (throwable instanceof CallNotPermittedException) {
            status = "CIRCUIT_OPEN";
            message = "Платёжный сервис временно недоступен. Цепь разомкнута после множества сбоев.";
            log.warn("CircuitBreaker не разрешил вызов: {}", message);

        } else if (throwable instanceof RetryableException) {
            status = "PAYMENT_UNAVAILABLE";
            message = "Не удалось выполнить платёж: " + throwable.getClass().getSimpleName();
            log.warn("Платежный сервис не доступен: {} {}", throwable.getMessage(), throwable.getClass().getSimpleName());
        }
        else {
            status = "PAYMENT_FAILED";
            message = "Не удалось выполнить платёж: " + throwable.getClass().getSimpleName();
            log.warn("Неизвестная ошибка при создании платежа: {} {}", throwable.getMessage(), throwable.getClass().getSimpleName());
        }

        return PaymentResponse.builder()
                .status(status)
                .message(message)
                .build();
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
