package ru.v_and_a.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@MappedSuperclass
@NoArgsConstructor
public abstract class ProcessedMessage {
    @Id
    @Column(name = "idempotent_key", length = 255, nullable = false)
    private String idempotentKey;

    @Column(nullable = false)
    private String event;

    @Column(name = "processed_at", nullable = false)
    private OffsetDateTime processedAt = OffsetDateTime.now();

    public ProcessedMessage(String idempotentKey, String event){
        this.idempotentKey = idempotentKey;
        this.event = event;
    }
}