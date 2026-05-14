package ru.v_and_a.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.v_and_a.domain.model.DeliveryCreatedMessage;
import ru.v_and_a.domain.repository.ProcessedMessagesRepository;
import ru.v_and_a.kafka.events.DeliveryCreatedEvent;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryCreatedMessagesServiceImpl implements ProcessedMessagesService<DeliveryCreatedEvent> {

    private final ObjectMapper objectMapper;
    private final ProcessedMessagesRepository processedMessagesRepository;

    @Override
    public void saveMessage(String idempotencyKey, DeliveryCreatedEvent message) {
        try {
            processedMessagesRepository.save(new DeliveryCreatedMessage(idempotencyKey, objectMapper.writeValueAsString(message)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isAlreadyProcessed(String idempotentKey) {
        return processedMessagesRepository.isAlreadyProcessed(idempotentKey);
    }
}
