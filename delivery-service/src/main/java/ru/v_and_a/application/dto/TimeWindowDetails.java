package ru.v_and_a.application.dto;

import java.time.LocalTime;

public record TimeWindowDetails(
        LocalTime startTime,
        LocalTime endTime
) {
}
