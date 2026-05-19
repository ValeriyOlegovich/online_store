package ru.v_and_a.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.v_and_a.core.dto.events.UpdateOrderStatusEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeliveryEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.order-creation-status}")
    private String deliveryCreatedTopic;

    public void sendDeliveryCreatedEvent(UpdateOrderStatusEvent event) {
        log.info("Отправка события о создании доставки: {}", event);
        kafkaTemplate.send(deliveryCreatedTopic, event.orderUuid(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Событие отправлено в топик '{}': orderUuid={}", deliveryCreatedTopic, event.orderUuid());
                    } else {
                        log.error("Ошибка при отправке события в топик '{}': orderUuid={}", deliveryCreatedTopic, event.orderUuid(), ex);
                    }
                });
    }
}
