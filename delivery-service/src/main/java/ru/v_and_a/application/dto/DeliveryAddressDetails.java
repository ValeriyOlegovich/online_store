package ru.v_and_a.application.dto;

public record DeliveryAddressDetails(
        String street,
        String city,
        String postalCode,
        String country
) {
}
