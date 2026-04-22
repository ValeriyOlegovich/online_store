package ru.v_and_a.web;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.v_and_a.application.PaymentService;
import ru.v_and_a.web.api.PaymentApi;
import ru.v_and_a.web.dto.PaymentRequest;
import ru.v_and_a.web.dto.PaymentResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;

    /**
     * Создаёт новый платёж
     */
    @PostMapping
    public PaymentResponse create(@RequestBody PaymentRequest paymentRequest) {
        return paymentService.createPayment(paymentRequest);
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
