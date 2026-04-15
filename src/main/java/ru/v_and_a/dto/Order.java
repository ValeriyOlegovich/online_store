package ru.v_and_a.dto;

import lombok.Data;
import ru.v_and_a.enums.OrderStatus;

import java.util.UUID;

@Data
public class Order {
    private String uuid;
    private OrderStatus status;

    public Order() {
        this.uuid = UUID.randomUUID().toString();
        this.status = OrderStatus.CREATED;
    }
}
