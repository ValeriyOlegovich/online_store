package ru.v_and_a.domain.model;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CREATED("Создан"),
    PAID("Оплачен");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }
}
