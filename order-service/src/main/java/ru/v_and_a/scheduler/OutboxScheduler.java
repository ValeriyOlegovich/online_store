package ru.v_and_a.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.v_and_a.domain.model.OutboxEvent;
import ru.v_and_a.domain.repository.OutboxRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxScheduler {
    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic.order-create}")
    private String orderEventsTopic;

    @Scheduled(fixedDelay = 5000)
    @Transactional(readOnly = true)
    public void sendPendingEvents() {
        log.info("Проверка на наличие неотправленных событий в outbox");

        List<OutboxEvent> pendingEvents = outboxRepository.find50ByPublishedFalse();

        if (pendingEvents.isEmpty()) {
            log.info("Нет неотправленных событий");
            return;
        }

        log.info("Найдено {} неотправленных событий", pendingEvents.size());

        for (OutboxEvent event : pendingEvents) {
            try {
                // Отправляем в Kafka
                kafkaTemplate.send(orderEventsTopic, event.getId().toString(), event.getPayload());

                // Помечаем как опубликованное (в рамках одной транзакции)
                markAsPublished(event.getId());

                log.info("Событие {} успешно отправлено в топик {}", event.getId(), orderEventsTopic);
            } catch (Exception e) {
                log.error("Ошибка при отправке события {} в Kafka", event.getId(), e);
                // Не помечаем как published → повторим позже
            }
        }
    }

    @Transactional
    protected void markAsPublished(Long eventId) {
        outboxRepository.markAsPublished(eventId);
    }
}
