package ru.v_and_a.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.v_and_a.application.PaymentService;
import ru.v_and_a.web.dto.PaymentRequest;
import ru.v_and_a.web.dto.PaymentResponse;

import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * Создаёт новый платёж
     */
    @PostMapping("/create")
    public PaymentResponse create(@RequestBody PaymentRequest paymentRequest) {
        return paymentService.createPayment(paymentRequest);
    }

    /**
     * Возвращает список всех платежей
     */
    @GetMapping("/getAll")
    public List<PaymentResponse> getAll() {
        return paymentService.getAllPayments();
    }

    /**
     * Возвращает платёж по ID
     */
    @GetMapping("/getById/{paymentId}")
    public PaymentResponse getById(@PathVariable Long paymentId) {
        return paymentService.getPaymentById(paymentId);
    }

    /**
     * Полное обновление платежа
     */
    @PutMapping("/updateById/{paymentId}")
    public PaymentResponse update(@PathVariable Long paymentId,
                                  @RequestBody PaymentRequest paymentRequest) {
        return paymentService.updatePayment(paymentId, paymentRequest);
    }

    /**
     * Частичное обновление (например, изменение статуса)
     */
    @PatchMapping("/partialUpdateById/{paymentId}")
    public PaymentResponse partialUpdate(@PathVariable Long paymentId,
                                         @RequestBody PaymentRequest paymentRequest) {
        return paymentService.partialUpdatePayment(paymentId, paymentRequest);
    }

    /**
     * Удаляет платёж
     */
    @DeleteMapping("/deleteById/{paymentId}")
    public void delete(@PathVariable Long paymentId) {
        paymentService.deletePayment(paymentId);
    }
}
