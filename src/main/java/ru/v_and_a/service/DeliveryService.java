package ru.v_and_a.service;

import org.springframework.stereotype.Service;
import ru.v_and_a.dto.DeliveryResult;
import ru.v_and_a.dto.Order;

@Service
public interface DeliveryService {

    DeliveryResult deliveryOrder(Order order);
}
