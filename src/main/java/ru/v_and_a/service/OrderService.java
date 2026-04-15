package ru.v_and_a.service;

import org.springframework.stereotype.Service;

@Service
public interface OrderService {
    String createOrder();
    String getStatusByUuid();
}
