package ru.v_and_a.kafka.events;

public record DeliveryCreatedEvent(String orderUuid, String trackingNumber)
{}
