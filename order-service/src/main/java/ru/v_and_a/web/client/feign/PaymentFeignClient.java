package ru.v_and_a.web.client.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.v_and_a.web.dto.PaymentRequest;
import ru.v_and_a.web.dto.PaymentResponse;

@FeignClient(
        name = "paymentClient",
        url = "${payment.service.url}"
)
public interface PaymentFeignClient {

    /**
     * Создаёт новый платёж.
     */
    @PostMapping("/api/v1/payments")
    PaymentResponse createPayment(
            @RequestHeader("X-Idempotency-Key") String idempotencyKeyHeader,
            @RequestBody PaymentRequest request
    );

    /**
     * Получает платёж по ID.
     */
    @GetMapping("/api/v1/payments/{paymentId}")
    PaymentResponse getPaymentById(@PathVariable("paymentId") Long paymentId);

    /**
     * Полное обновление платежа.
     */
    @PutMapping("/api/v1/payments/{paymentId}")
    PaymentResponse updatePayment(
            @PathVariable("paymentId") Long paymentId,
            @RequestBody PaymentRequest request
    );

    /**
     * Частичное обновление платежа.
     */
    @PatchMapping("/api/v1/payments/{paymentId}")
    PaymentResponse partialUpdatePayment(
            @PathVariable("paymentId") Long paymentId,
            @RequestBody PaymentRequest request
    );

    /**
     * Удаляет платёж.
     */
    @DeleteMapping("/api/v1/payments/{paymentId}")
    void deletePayment(@PathVariable("paymentId") Long paymentId);
}

