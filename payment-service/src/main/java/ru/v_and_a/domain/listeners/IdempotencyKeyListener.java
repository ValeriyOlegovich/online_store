package ru.v_and_a.domain.listeners;

import jakarta.persistence.PreRemove;

public class IdempotencyKeyListener {

    @PreRemove
    public void preRemove() {
        throw new IllegalStateException("Удаление записей из IdempotencyKey запрещено!");
    }
}
