package ru.v_and_a.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import ru.v_and_a.application.ProcessedMessagesService;
import ru.v_and_a.kafka.events.DeliveryCreatedEvent;

@Component
@Slf4j
public class OrderConsumer extends IdempotentKafkaListener<DeliveryCreatedEvent> {

    public OrderConsumer(ObjectMapper objectMapper, ProcessedMessagesService<DeliveryCreatedEvent> processedMessages) {
        super(objectMapper, processedMessages);
    }

    @Override
    @KafkaListener(
            topics = "${kafka.topic.delivery-created}",
            groupId = "order-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeMessage(ConsumerRecord<String, DeliveryCreatedEvent> consumerRecord, DeliveryCreatedEvent event, Acknowledgment acknowledgment) {
        log.info("Получено событие: " + event);
        try {
            super.consumeMessage(consumerRecord, event, acknowledgment);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void processConsumedMessage(DeliveryCreatedEvent event) {
        log.info("Получено событие: доставка создана для заказа {}", event.toString());
    }
}
