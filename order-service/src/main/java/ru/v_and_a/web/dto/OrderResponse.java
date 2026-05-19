package ru.v_and_a.web.dto;

import lombok.Builder;
import lombok.Data;
import ru.v_and_a.core.dto.enums.OrderStatus;

@Data
@Builder
public class OrderResponse {
    private String uuid;
    private OrderStatus status;
    private String message;
}
