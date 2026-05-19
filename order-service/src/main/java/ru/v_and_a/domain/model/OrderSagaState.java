package ru.v_and_a.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.v_and_a.core.dto.enums.OrderStatus;

@Entity
@Table(name = "order_saga_states")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSagaState {

    @Id
    private String orderUuid;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String message;
}
