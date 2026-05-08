package ru.v_and_a.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.v_and_a.application.OrderService;
import ru.v_and_a.kafka.events.DeliveryCreatedEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = "${kafka.topic.delivery-created}",
            groupId = "order-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenDeliveryCreated(DeliveryCreatedEvent event) {
        log.info("Получено событие: доставка создана для заказа {}", event.toString());
    }
}
