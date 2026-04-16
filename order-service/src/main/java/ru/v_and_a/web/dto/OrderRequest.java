package ru.v_and_a.web.dto;

import lombok.Data;
import ru.v_and_a.domain.model.OrderStatus;

import java.util.List;

@Data
public class OrderRequest {
    private List<Item> items;
    private String currency;
    private String notes;
    private OrderStatus status;
}
