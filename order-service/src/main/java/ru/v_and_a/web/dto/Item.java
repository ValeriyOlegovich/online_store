package ru.v_and_a.web.dto;

import lombok.Data;

@Data
public class Item {
    private String productId;
    private String name;
    private int quantity;
    private double price;
}
