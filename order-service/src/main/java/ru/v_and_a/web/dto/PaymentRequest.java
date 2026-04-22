package ru.v_and_a.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PaymentRequest {

    private String orderId;
    private String status;
    private String method;
    private BigDecimal amount;
    private String currency;
}
