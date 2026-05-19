package ru.v_and_a.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.v_and_a.application.DeliveryService;
import ru.v_and_a.core.dto.enums.OrderStatus;
import ru.v_and_a.core.dto.events.UpdateOrderStatusEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryEventConsumer {

    private final DeliveryService deliveryService;
    private final DeliveryEventProducer deliveryEventProducer;

    /**
     * Слушает события об оплате заказа
     */
    @KafkaListener(
            topics = "${kafka.topic.order-creation-status}",
            groupId = "delivery-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenOrderPaidEvent(UpdateOrderStatusEvent event, @Nullable Acknowledgment ack) {
        log.info("Получено событие об оплате заказа: event={}", event);
        if (event.status().equals(OrderStatus.PAID)) {
            if (event.orderUuid().equals("delivery-error")) {
                deliveryEventProducer.sendDeliveryCreatedEvent(new UpdateOrderStatusEvent(event.orderUuid(), OrderStatus.REJECTED, "Ошибка доставки"));
            } else {
                deliveryEventProducer.sendDeliveryCreatedEvent(new UpdateOrderStatusEvent(event.orderUuid(), OrderStatus.DELIVERED, null));
            }
        }
        ack.acknowledge();

    }
}
