package ru.v_and_a.web.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.v_and_a.domain.model.DeliveryStatus;

@Data
@AllArgsConstructor
@Schema(description = "Ответ с данными доставки")
public class DeliveryResponse {

    @Schema(description = "Идентификатор доставки", example = "1")
    private Long id;
    @Schema(description = "Идентификатор заказа", example = "1")
    private String orderUuid;
    @Schema(description = "Статус доставки", example = "IN_TRANSIT")
    private DeliveryStatus status;
    @Schema(description = "Трек-номер", example = "TRK-10002")
    private String trackingNumber;}
