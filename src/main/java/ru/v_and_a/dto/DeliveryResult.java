package ru.v_and_a.dto;

public record DeliveryResult(boolean success, String trackingNumber, String message) {
}