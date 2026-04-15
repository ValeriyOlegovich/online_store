package ru.v_and_a.application.dto;

import ru.v_and_a.domain.model.DeliveryStatus;

import java.time.LocalDate;

public record DeliveryDetails(
        Long id,
        Long orderId,
        DeliveryStatus status,
        DeliveryAddressDetails deliveryAddress,
        LocalDate deliveryDate,
        TimeWindowDetails timeWindow,
        String trackingNumber
) {
}
