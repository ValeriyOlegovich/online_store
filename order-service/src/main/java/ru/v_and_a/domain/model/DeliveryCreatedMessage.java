package ru.v_and_a.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(
        name = "delivery_created_message",
        indexes = @Index(name = "idx_idempotent_key", columnList = "idempotent_key")
)
@Data
@NoArgsConstructor
public class DeliveryCreatedMessage extends ProcessedMessage {

    public DeliveryCreatedMessage(String idempotentKey, String event) {
        super(idempotentKey, event);
    }
}