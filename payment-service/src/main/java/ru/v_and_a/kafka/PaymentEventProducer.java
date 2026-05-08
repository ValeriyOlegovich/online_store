package ru.v_and_a.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.v_and_a.kafka.events.PaymentEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.order-paid}")
    private String orderPaidTopic;

    public void sendOrderPaidEvent(PaymentEvent event) {
        log.info("Отправка события оплаты заказа: PaymentEvent(orderUuid={})", event.orderUuid());
        kafkaTemplate.send(orderPaidTopic, event.orderUuid(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Событие успешно отправлено в топик {}: {}", orderPaidTopic, event.orderUuid());
                    } else {
                        log.error("Ошибка при отправке события в топик {}: {}", orderPaidTopic, event.orderUuid(), ex);
                    }
                });
    }
}
