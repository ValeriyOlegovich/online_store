package ru.v_and_a.rabbitMQ.events;

import java.time.OffsetDateTime;

public record OrderCancelledEvent(
        String orderUuid,
        OffsetDateTime cancelledAt,
        String reason
) {}