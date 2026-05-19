package ru.v_and_a.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.v_and_a.core.dto.enums.OrderStatus;
import ru.v_and_a.core.dto.events.UpdateOrderStatusEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentConsumer {

    private final PaymentEventProducer paymentProducer;

    @KafkaListener(
            topics = "${kafka.topic.order-creation-status}",
            groupId="payment-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeUpdateOrderStatus(UpdateOrderStatusEvent event, Acknowledgment acknowledgment) {
        log.info("Получено событие изменения статуса заказа: " + event);
        if (event.status().equals(OrderStatus.CREATED)) {
            if (event.orderUuid().equals("payment-error")) {
                paymentProducer.sendUpdateOrderStatusEvent(event.orderUuid(), OrderStatus.REJECTED, "Ошибка оплаты");
            } else {
                paymentProducer.sendUpdateOrderStatusEvent(event.orderUuid(), OrderStatus.PAID, null);
            }
        } else if (event.status().equals(OrderStatus.REJECTED)) {
            log.info("Оплата заказа отклонена: " + event);
        }

        acknowledgment.acknowledge();
    }
}
