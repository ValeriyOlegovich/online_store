package ru.v_and_a.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Адрес доставки в запросе")
public class DeliveryAddressRequest {

    private String building;
    private String apartment;
    private String street;
    private String city;
}
