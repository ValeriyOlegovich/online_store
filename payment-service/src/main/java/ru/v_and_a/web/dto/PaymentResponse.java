package ru.v_and_a.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.v_and_a.domain.model.PaymentMethod;
import ru.v_and_a.domain.model.PaymentStatus;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Ответ с данными платежа")
public class PaymentResponse {

    @Schema(description = "Идентификатор платежа", example = "1")
    private Long id;
    @Schema(description = "Идентификатор заказа", example = "1")
    private String orderId;
    @Schema(description = "Статус платежа", example = "CAPTURED")
    private PaymentStatus status;
    @Schema(description = "Способ оплаты", example = "CARD")
    private PaymentMethod method;
    @Schema(description = "Сумма платежа")
    private BigDecimal amount;
    @Schema(description = "Валюта платежа")
    private String currency;
    @Schema(description = "Дата и время создания платежа")
    private LocalDateTime createdAt;
    @Schema(description = "Дополнительное сообщение: результат операции, ошибка или предупреждение")
    private String message;
}
