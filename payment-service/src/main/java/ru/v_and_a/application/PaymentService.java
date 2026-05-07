package ru.v_and_a.application;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.v_and_a.domain.model.Payment;
import ru.v_and_a.domain.model.PaymentStatus;
import ru.v_and_a.web.dto.PaymentRequest;
import ru.v_and_a.web.dto.PaymentResponse;

import java.util.List;

@Service
public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request);
    List<PaymentResponse> getAll(Pageable pageable);
    PaymentResponse getPaymentById(Long id);
    PaymentResponse updatePayment(Long id, PaymentRequest request);
    PaymentResponse partialUpdatePayment(Long id, PaymentRequest request);
    void deletePayment(Long id);
    Payment getPaymentByOrderUuid(String orderUuid);
    int cancel(Long id);
}
