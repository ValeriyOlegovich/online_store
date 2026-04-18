package ru.v_and_a.domain.model;

import lombok.Data;

@Data
public class Item {
    private String productId;
    private String name;
    private int quantity;
    private double price;
}
