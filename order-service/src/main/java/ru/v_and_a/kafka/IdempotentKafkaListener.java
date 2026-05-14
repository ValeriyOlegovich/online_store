package ru.v_and_a.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.support.Acknowledgment;
import ru.v_and_a.application.ProcessedMessagesService;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Slf4j
public abstract class IdempotentKafkaListener<T> {
    
    private final ObjectMapper objectMapper;
    private final ProcessedMessagesService<T> processedMessages;

    /**
     * Обработка полученного Kafka сообщения с проверкой идемпотентности.
     *
     * @param consumerRecord исходный Kafka рекорд
     * @param message расшифрованное сообщение
     * @param acknowledgment подтверждение обработки
     * @throws JsonProcessingException при ошибке сериализации сообщения
     */
    public void consumeMessage(ConsumerRecord<String, T> consumerRecord, T
            message, Acknowledgment acknowledgment) throws JsonProcessingException {
        Header idempotentKeyHeader =
                consumerRecord.headers().lastHeader("X-Idempotency-Key");
        if (idempotentKeyHeader == null) {
            log.error("Отсутствует ключ идемпотентности у сообщения: {}", message);
                    acknowledgment.acknowledge();
            return;
        }
        // Преобразование заголовка в строку
        String idempotentKey = new String(idempotentKeyHeader.value(),
                StandardCharsets.UTF_8);
        boolean isDuplicate = processedMessages.isAlreadyProcessed(idempotentKey);
        if (!isDuplicate) {
            processedMessages.saveMessage(idempotentKey, message);
        } else {
            log.warn("Сообщение {} c идентификатором {} уже обработано", message, idempotentKey);
        }

        processConsumedMessage(message);
    }

    /**
     * Метод для обработки конкретных событий, реализуемый в
     подклассах.
     *
     * @param event полученное событие
     */
    public abstract void processConsumedMessage(T event);
}