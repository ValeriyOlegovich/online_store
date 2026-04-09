package ru.v_and_a.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import ru.v_and_a.dto.DeliveryResult;
import ru.v_and_a.dto.Order;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryServiceImplTest {

    @InjectMocks
    private DeliveryServiceImpl deliveryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Инициализация аннотаций Mockito
    }

    @Test
    void deliveryOrder() {
        DeliveryResult result =  deliveryService.deliveryOrder(new Order());
        assertEquals(result.message(), "Заказ принят в доставку");
        assertNotNull(result.trackingNumber());
        assertTrue(result.success());
    }
}