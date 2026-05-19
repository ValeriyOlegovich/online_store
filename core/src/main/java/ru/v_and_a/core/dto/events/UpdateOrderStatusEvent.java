package ru.v_and_a.core.dto.events;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.v_and_a.core.dto.enums.OrderStatus;

public record UpdateOrderStatusEvent(@NotNull String orderUuid, @NotNull OrderStatus status, @Nullable String message) {}
