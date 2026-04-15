package ru.v_and_a.service;

import org.springframework.stereotype.Service;
import ru.v_and_a.dto.DeliveryResult;
import ru.v_and_a.dto.Order;

@Service
public class DeliveryServiceImpl implements DeliveryService {
    @Override
    public DeliveryResult deliveryOrder(Order order) {
        return new DeliveryResult(true, "trackingNumber","Заказ принят в доставку");
    }
}
