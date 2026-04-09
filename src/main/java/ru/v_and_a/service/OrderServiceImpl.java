package ru.v_and_a.service;

import org.springframework.stereotype.Service;
import ru.v_and_a.dto.Order;
import ru.v_and_a.enums.OrderStatus;

@Service
public class OrderServiceImpl implements OrderService {
    private final PaymentService paymentService;
    private final DeliveryService delivery;

    public OrderServiceImpl(PaymentService paymentService, DeliveryService delivery) {
        this.paymentService = paymentService;
        this.delivery = delivery;
    }


    @Override
    public String createOrder() {
        Order order = new Order();
        paymentService.payOrder(order);
        delivery.deliveryOrder(order);
        return order.getUuid();
    }

    @Override
    public String getStatusByUuid() {
        return OrderStatus.CREATED.getValue();
    }
}
