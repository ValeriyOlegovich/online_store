package ru.v_and_a.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import ru.v_and_a.dto.Order;
import ru.v_and_a.dto.PaymentResult;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceImplTest {

    @InjectMocks
    PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Инициализация аннотаций Mockito
    }

    @Test
    void payOrder() {
        PaymentResult result= paymentService.payOrder(new Order());
        assertEquals(result.message(), "Заказ оплачен и передан в доставку");
        assertTrue(result.success());
        assertNotNull(result.transactionUuid());
    }
}