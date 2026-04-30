package ru.v_and_a.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.v_and_a.web.dto.DeliveryRequest;
import ru.v_and_a.web.dto.DeliveryResponse;

import java.util.List;

@Tag(name = "Delivery API", description = "Управление доставками")
@RequestMapping("/api/v1/deliveries")
public interface DeliveryApi {

    @Operation(
            summary = "Создать новую доставку",
            description = "Создаёт запись о доставке",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Доставка успешно создана",
                            content = @Content(schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
            }
    )
    @PostMapping
    String create(@RequestBody DeliveryRequest deliveryRequest);

    @Operation(
            summary = "Получить все доставки",
            description = "Возвращает список всех доставок с поддержкой пагинации"
    )
    @GetMapping
    List<DeliveryResponse> getAll(Pageable pageable);

    @Operation(
            summary = "Получить доставку по ID",
            description = "Возвращает информацию о доставке по её уникальному идентификатору"
    )
    @GetMapping("/{id}")
    DeliveryResponse getById(
            @Parameter(description = "Идентификатор доставки", example = "1")
            @PathVariable("id") Long id
    );

    @Operation(
            summary = "Полное обновление доставки",
            description = "Заменяет все поля доставки новыми значениями"
    )
    @PutMapping("/{id}")
    DeliveryResponse update(
            @Parameter(description = "Идентификатор доставки", example = "1")
            @PathVariable("id") Long id,
            @RequestBody DeliveryRequest deliveryRequest
    );

    @Operation(
            summary = "Удалить доставку",
            description = "Удаляет запись о доставке по её идентификатору"
    )
    @DeleteMapping("/{id}")
    void delete(@PathVariable("id") Long id);
}