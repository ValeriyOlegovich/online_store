package ru.v_and_a.web.dto;

import lombok.Data;
import ru.v_and_a.core.dto.enums.OrderStatus;
import ru.v_and_a.domain.model.Item;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderRequest {
    private String orderUuid;
    private List<Item> items;
    private BigDecimal totalAmount;
    private OrderStatus status;
}
