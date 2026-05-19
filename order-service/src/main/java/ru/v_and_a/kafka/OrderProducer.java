package ru.v_and_a.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.v_and_a.core.dto.enums.OrderStatus;
import ru.v_and_a.core.dto.events.UpdateOrderStatusEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.order-creation-status}")
    private String deliveryCreatedTopic;

    public void sendOrderCreatedEvent(String orderUuid, OrderStatus status, String message) {
        log.info("Отправка события о создании заказа uuid: {}", orderUuid);

        kafkaTemplate.send(deliveryCreatedTopic, orderUuid, new UpdateOrderStatusEvent(orderUuid, status, message))
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Событие отправлено в топик '{}': orderUuid={}", deliveryCreatedTopic, orderUuid);
                    } else {
                        log.error("Ошибка при отправке события в топик '{}': orderUuid={}", deliveryCreatedTopic, orderUuid);
                    }
                });
    }
}
