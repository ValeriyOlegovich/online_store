package ru.v_and_a.web.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.v_and_a.domain.model.DeliveryStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Запрос на создание или обновление доставки")
public class DeliveryRequest {

    @Schema(description = "Идентификатор заказа", example = "1")
    private String orderUuid;
    @Schema(description = "Статус доставки", example = "CREATED")
    private DeliveryStatus status;
}
