package ru.v_and_a.core.dto.enums;

public enum OrderStatus {
    CREATED("Создан"),
    PAID("Оплачен"),
    DELIVERED("Доставлен"),
    REJECTED("Отменён");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }
}
