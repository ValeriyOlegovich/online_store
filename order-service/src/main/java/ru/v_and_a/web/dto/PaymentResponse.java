package ru.v_and_a.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PaymentResponse {

    private Long id;
    private String orderId;
    private String status;
    private String method;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime createdAt;
}
