package ru.v_and_a.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "outbox")
@Data
@NoArgsConstructor
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "event_type", nullable = false)
    private String eventType;
    @Column(name = "payload", nullable = false)
    private String payload;
    @Column(name = "created_at", nullable=false)
    private OffsetDateTime createdAt = OffsetDateTime.now();
    @Column(name="published", nullable=false)
    private Boolean published = false;

    public OutboxEvent(String eventType, String payload) {
        this.eventType = eventType;
        this.payload = payload;
    }
}