package ru.v_and_a.application;

import org.springframework.stereotype.Service;
import ru.v_and_a.web.dto.PaymentRequest;
import ru.v_and_a.web.dto.PaymentResponse;

import java.util.List;

@Service
public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request);
    List<PaymentResponse> getAllPayments();
    PaymentResponse getPaymentById(Long id);
    PaymentResponse updatePayment(Long id, PaymentRequest request);
    PaymentResponse partialUpdatePayment(Long id, PaymentRequest request);
    void deletePayment(Long id);
}
