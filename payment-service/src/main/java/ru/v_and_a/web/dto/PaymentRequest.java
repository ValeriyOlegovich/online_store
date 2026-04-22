package ru.v_and_a.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.v_and_a.domain.model.PaymentMethod;
import ru.v_and_a.domain.model.PaymentStatus;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Schema(description = "Запрос на создание или обновление платежа")
public class PaymentRequest {

    @Schema(description = "Идентификатор заказа", example = "1")
    private String orderId;
    @Schema(description = "Статус платежа", example = "PENDING")
    private PaymentStatus status;
    @Schema(description = "Способ оплаты", example = "CARD")
    private PaymentMethod method;
    @Schema(description = "Сумма платежа")
    private BigDecimal amount;
    @Schema(description = "Валюта платежа")
    private String currency;
}
