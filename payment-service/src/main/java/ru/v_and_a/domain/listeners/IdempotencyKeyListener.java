package ru.v_and_a.domain.listeners;

import jakarta.persistence.PreRemove;
import ru.v_and_a.domain.model.IdempotencyKey;

public class IdempotencyKeyListener {

    @PreRemove
    public void preRemove(IdempotencyKey key) {
        throw new IllegalStateException("Удаление записей из IdempotencyKey запрещено!");
    }
}
