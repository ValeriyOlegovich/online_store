package ru.v_and_a.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DltReader {

    @KafkaListener(
            topics = "order.creation.status-dlt",
            groupId = "dlt-reader-group",
            containerFactory = "stringContainerFactory"
    )
    public void listen(@Payload(required = false) String payload,
                       ConsumerRecord<String, String> record,
                       Acknowledgment acknowledgment) {
        log.error("=== ОШИБОЧНОЕ СООБЩЕНИЕ ИЗ DLT ===");
        if (payload != null) {
            log.error("Payload: {}", payload);
        } else {
            log.error("Payload is null");
        }
        acknowledgment.acknowledge();
    }
}