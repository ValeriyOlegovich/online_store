package ru.v_and_a.service;

import org.springframework.stereotype.Service;
import ru.v_and_a.dto.Order;
import ru.v_and_a.dto.PaymentResult;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Override
    public PaymentResult payOrder(Order order) {
        return new PaymentResult(true, "Заказ оплачен и передан в доставку", "transactionUuid");
    }
}
