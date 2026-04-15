package ru.v_and_a.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CREATED("Создан");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

}
