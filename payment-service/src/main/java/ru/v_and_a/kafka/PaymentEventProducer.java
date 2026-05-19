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
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.order-creation-status}")
    private String orderPaidTopic;

    public void sendUpdateOrderStatusEvent(String orderUuid, OrderStatus status, String message) {
        log.info("Отправка события оплаты заказа: orderUuid={}, status={}, message={}", orderUuid, status, message);
        kafkaTemplate.send(orderPaidTopic, new UpdateOrderStatusEvent(orderUuid, status, message))
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Событие успешно отправлено в топик {}: {}", orderPaidTopic, orderUuid);
                    } else {
                        log.error("Ошибка при отправке события в топик {}: {}", orderPaidTopic, orderUuid, ex);
                    }
                });
    }
}
