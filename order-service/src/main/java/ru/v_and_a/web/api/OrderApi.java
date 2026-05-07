package ru.v_and_a.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.v_and_a.web.dto.OrderRequest;
import ru.v_and_a.web.dto.OrderResponse;

import java.util.List;

@Tag(name = "Order API", description = "Управление заказами")
@RequestMapping("/api/v1/orders")
public interface OrderApi {

    @Operation(
            summary = "Создать заказ",
            description = "Создаёт новый заказ на основе переданных данных",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Заказ успешно создан",
                            content = @Content(schema = @Schema(implementation = OrderResponse.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные")
            }
    )
    @PostMapping
    OrderResponse createOrder(@RequestBody OrderRequest request);

    @Operation(
            summary = "Получить все заказы",
            description = "Возвращает список всех заказов с пагинацией"
    )
    @GetMapping
    Page<OrderResponse> getAll(Pageable pageable);

    @Operation(
            summary = "Получить заказ по UUID",
            description = "Возвращает информацию о заказе по его уникальному идентификатору"
    )
    @GetMapping("/{uuid}")
    OrderResponse getByUuid(
            @Parameter(description = "UUID заказа", example = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8")
            @PathVariable("uuid") String uuid
    );

    @Operation(
            summary = "Полное обновление заказа",
            description = "Заменяет все поля заказа новыми значениями"
    )
    @PutMapping("/{uuid}")
    OrderResponse updateOrder(
            @Parameter(description = "UUID заказа", example = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8")
            @PathVariable("uuid") String uuid,
            @RequestBody OrderRequest request
    );

    @Operation(
            summary = "Частичное обновление заказа",
            description = "Обновляет только указанные поля заказа, например статус"
    )
    @PatchMapping("/{uuid}")
    OrderResponse partialUpdateOrder(
            @Parameter(description = "UUID заказа", example = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8")
            @PathVariable("uuid") String uuid,
            @RequestBody OrderRequest request
    );

    @Operation(
            summary = "Удалить заказ",
            description = "Удаляет заказ по его уникальному идентификатору"
    )
    @DeleteMapping("/{uuid}")
    void deleteOrder(
            @Parameter(description = "UUID заказа", example = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8")
            @PathVariable("uuid") String uuid
    );

    @Operation(
            summary = "Отменить заказ",
            description = "Изменяет статус заказа на CANCELLED. Допустимо только для заказов в статусах NEW или PAID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Заказ успешно отменён",
                            content = @Content(schema = @Schema(implementation = OrderResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Заказ не найден"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Заказ нельзя отменить (уже отменён, доставлен и т.п.)"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Некорректный UUID"
                    )
            }
    )
    @PatchMapping("/{uuid}/cancel")
    OrderResponse cancelOrder(
            @Parameter(description = "UUID заказа", example = "a1b2c3d4-e5f6-7890-g1h2-i3j4k5l6m7n8")
            @PathVariable("uuid") String uuid
    );
}