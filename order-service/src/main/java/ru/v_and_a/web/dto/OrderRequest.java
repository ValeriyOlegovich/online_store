package ru.v_and_a.web.dto;

import lombok.Data;
import ru.v_and_a.domain.model.Item;
import ru.v_and_a.domain.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {
    private List<Item> items;
    private String currency;
    private BigDecimal totalAmount;
    private String notes;
    private OrderStatus status;
}
