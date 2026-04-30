package ru.v_and_a.web.dto;

import lombok.Builder;
import lombok.Data;
import ru.v_and_a.domain.model.OrderStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderResponse {
    private String uuid;
    private OrderStatus status;
    private String paymentStatus;
    private String deliveryStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String message;
}
