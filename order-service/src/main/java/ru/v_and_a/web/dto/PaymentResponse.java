package ru.v_and_a.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private String orderId;
    private String status;
    private String method;
    private BigDecimal amount;
    private String currency;
    private OffsetDateTime createdAt;
    private String message;
}
