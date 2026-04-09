package ru.v_and_a.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Инициализация аннотаций Mockito
    }

    @Test
    void createOrder() {
        String response = orderController.createOrder();
        assertNotNull(response);
    }

    @Test
    void getStatusByUuid() {
        String response = orderController.getStatusByUuid();
        assertNotNull(response);
    }
}