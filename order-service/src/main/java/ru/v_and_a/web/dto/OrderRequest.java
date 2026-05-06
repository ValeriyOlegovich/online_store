package ru.v_and_a.web.dto;

import lombok.Data;
import ru.v_and_a.domain.model.Item;
import ru.v_and_a.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {
    private String orderUuid;
    private List<Item> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
}
