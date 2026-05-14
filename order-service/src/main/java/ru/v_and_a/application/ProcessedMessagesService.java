package ru.v_and_a.application;

import org.springframework.stereotype.Service;

@Service
public interface ProcessedMessagesService<T> {

    void saveMessage (String idempotencyKey, T message);
    boolean isAlreadyProcessed(String idempotentKey);
}
