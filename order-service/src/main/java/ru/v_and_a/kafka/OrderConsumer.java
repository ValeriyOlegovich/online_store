package ru.v_and_a.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.v_and_a.core.dto.enums.OrderStatus;
import ru.v_and_a.core.dto.events.UpdateOrderStatusEvent;

@Component
@Slf4j
public class OrderConsumer {

    @KafkaListener(
            topics = "${kafka.topic.order-creation-status}",
            groupId = "order-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeUpdateOrderStatus(UpdateOrderStatusEvent event, @Nullable Acknowledgment acknowledgment) {
        log.info("Получено событие изменения статуса заказа: " + event);
        if (event.status().equals(OrderStatus.DELIVERED)) {
            log.info("Заказ orderUuid: {} завершен! ", event.orderUuid());
        } else if (event.status().equals(OrderStatus.REJECTED)) {
            log.info("Заказ orderUuid: {} отменен!", event.orderUuid());
        }
        acknowledgment.acknowledge();
    }
}
