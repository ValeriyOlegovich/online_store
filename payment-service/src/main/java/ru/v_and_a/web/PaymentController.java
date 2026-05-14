package ru.v_and_a.web;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.v_and_a.application.PaymentService;
import ru.v_and_a.domain.model.PaymentStatus;
import ru.v_and_a.web.api.PaymentApi;
import ru.v_and_a.web.dto.PaymentRequest;
import ru.v_and_a.web.dto.PaymentResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;

    /**
     * Создаёт новый платёж
     */
    @PostMapping
    @CircuitBreaker(name = "payment", fallbackMethod = "createFallback")
    @RateLimiter(name = "createLimiter")
    public PaymentResponse create(
            @RequestHeader("X-Idempotency-Key") String idempotencyKeyHeader,
            @RequestBody PaymentRequest paymentRequest) {
        return paymentService.createPayment(paymentRequest);
    }

    private String createFallback(String idempotencyKeyHeader, PaymentRequest paymentRequest, Throwable throwable) {
        return "fallback-order-id-" + paymentRequest.getOrderId();
    }

    /**
     * Инициирует оплату заказа
     * @param orderUuid UUID заказа
     * @param idempotencyKeyHeader уникальный ключ идемпотентности
     * @return PaymentResponse
     */
    @PostMapping("/{orderUuid}")
    @CircuitBreaker(name = "payment", fallbackMethod = "payOrderFallback")
    @RateLimiter(name = "createLimiter")
    public ResponseEntity<PaymentResponse> pay(
            @PathVariable String orderUuid,
            @RequestHeader("X-Idempotency-Key") String idempotencyKeyHeader
    ) {
        PaymentResponse response = paymentService.orderPayment(orderUuid);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<PaymentResponse> payOrderFallback(
            String orderId,
            String idempotencyKeyHeader,
            Throwable throwable
    ) {
        log.warn("Fallback triggered for payOrder. Order: {}, Error: {}", orderId, throwable.getMessage());

        PaymentResponse fallbackResponse = PaymentResponse.builder()
                .status(PaymentStatus.FAILED)
                .message("Оплата временно недоступна. Повторите позже.")
                .orderId(orderId)
                .build();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(fallbackResponse);
    }
    /**
     * Возвращает список всех платежей
     */
    @GetMapping
    public List<PaymentResponse> getAll(Pageable pageable) {
        return paymentService.getAll(pageable);
    }

    /**
     * Возвращает платёж по ID
     */
    @GetMapping("/{paymentId}")
    public PaymentResponse getById(@PathVariable Long paymentId) {
        return paymentService.getPaymentById(paymentId);
    }

    /**
     * Полное обновление платежа
     */
    @PutMapping("/{paymentId}")
    public PaymentResponse update(@PathVariable Long paymentId,
                                  @RequestBody PaymentRequest paymentRequest) {
        return paymentService.updatePayment(paymentId, paymentRequest);
    }

    /**
     * Частичное обновление (например, изменение статуса)
     */
    @PatchMapping("/{paymentId}")
    public PaymentResponse partialUpdate(@PathVariable Long paymentId,
                                         @RequestBody PaymentRequest paymentRequest) {
        return paymentService.partialUpdatePayment(paymentId, paymentRequest);
    }

    /**
     * Удаляет платёж
     */
    @DeleteMapping("/{paymentId}")
    public void delete(@PathVariable Long paymentId) {
        paymentService.deletePayment(paymentId);
    }
}
