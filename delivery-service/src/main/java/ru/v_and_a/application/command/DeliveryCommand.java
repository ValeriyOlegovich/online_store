package ru.v_and_a.application.command;

import ru.v_and_a.application.dto.DeliveryAddressDetails;
import ru.v_and_a.application.dto.TimeWindowDetails;
import ru.v_and_a.domain.model.DeliveryStatus;

import java.time.LocalDate;

public record DeliveryCommand(
        Long orderId,
        DeliveryStatus status,
        DeliveryAddressDetails deliveryAddress,
        LocalDate deliveryDate,
        TimeWindowDetails timeWindow,
        String trackingNumber
) {
}
