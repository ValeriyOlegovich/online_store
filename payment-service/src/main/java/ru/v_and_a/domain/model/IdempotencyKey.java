package ru.v_and_a.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.v_and_a.domain.listeners.IdempotencyKeyListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "Idempotency_Key",
        indexes = @Index(name = "IDX_KEY", columnList = "idempotencyKey", unique = true)
)
@NoArgsConstructor
@AllArgsConstructor
@Data
@EntityListeners(IdempotencyKeyListener.class)
public class IdempotencyKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String idempotencyKey;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private String responsePayload; // JSON-результат
    private Integer httpStatus;
}
