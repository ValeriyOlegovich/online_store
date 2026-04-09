package ru.v_and_a.service;

import org.springframework.stereotype.Service;
import ru.v_and_a.dto.Order;
import ru.v_and_a.dto.PaymentResult;

@Service
public interface PaymentService {

    PaymentResult payOrder(Order order);
}
