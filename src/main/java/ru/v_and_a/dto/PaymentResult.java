package ru.v_and_a.dto;

public record PaymentResult(boolean success, String message, String transactionUuid) {
}
