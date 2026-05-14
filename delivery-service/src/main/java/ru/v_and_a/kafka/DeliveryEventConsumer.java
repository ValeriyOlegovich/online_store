package ru.v_and_a.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.v_and_a.application.DeliveryService;
import ru.v_and_a.kafka.events.PaymentEvent;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryEventConsumer {

    private final DeliveryService deliveryService;

    /**
     * Слушает события об оплате заказа
     */
    @KafkaListener(
            topics = "${kafka.topic.order-paid}",
            groupId = "delivery-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listenOrderPaidEvent(PaymentEvent event, @Nullable Acknowledgment ack) {
        log.info("Получено событие об оплате заказа: orderUuid={}", event.orderUuid());

        try {
            startDelivery(event.orderUuid());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Ошибка при создании доставки для заказа: {}", event.orderUuid(), e);
            ack.nack(Duration.ofSeconds(1));
            throw e;
        }
    }

    /**
     * Слушает события об создании заказа
     */
    @KafkaListener(
            topics = "${kafka.topic.order-create}",
            groupId = "delivery-group",
            containerFactory = "stringKafkaListenerContainerFactory"
    )
    public void listenOrderCreateEvent(String event) {
        log.info("Получено событие о создании заказа: orderUuid={}", event);
    }

    private void startDelivery(String orderUuid) {
        deliveryService.createDeliveryByOrderUuid(orderUuid);
        log.info("Доставка создана для заказа: {}", orderUuid);
    }
}
