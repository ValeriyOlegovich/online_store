package ru.v_and_a.web.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.v_and_a.web.dto.PaymentRequest;
import ru.v_and_a.web.dto.PaymentResponse;

import java.util.List;

@RequestMapping("/api/v1/payments")
@Tag(name = "Payment API", description = "Управление платежами")
public interface PaymentApi {

    @Operation(
            summary = "Создать платёж",
            description = "Создаёт новый платёж на основе переданных данных",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Платёж успешно создан",
                            content = @Content(schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные")
            }
    )
    @PostMapping
    String create(@RequestHeader("X-Idempotency-Key") String idempotencyKeyHeader,
                           @RequestBody PaymentRequest paymentRequest);

    @Operation(
            summary = "Получить все платежи",
            description = "Возвращает список всех платежей с пагинацией"
    )
    @GetMapping
    List<PaymentResponse> getAll(Pageable pageable);

    @Operation(
            summary = "Получить платёж по ID",
            description = "Возвращает информацию о платеже по его идентификатору"
    )
    @GetMapping("/{paymentId}")
    PaymentResponse getById(
            @Parameter(description = "Идентификатор платежа", example = "1")
            @PathVariable("paymentId") Long paymentId
    );

    @Operation(
            summary = "Полное обновление платежа",
            description = "Заменяет все поля платежа новыми значениями"
    )
    @PutMapping("/{paymentId}")
    PaymentResponse update(
            @Parameter(description = "Идентификатор платежа", example = "1")
            @PathVariable("paymentId") Long paymentId,
            @RequestBody PaymentRequest paymentRequest
    );

    @Operation(
            summary = "Частичное обновление платежа",
            description = "Обновляет только указанные поля платежа, например статус"
    )
    @PatchMapping("/{paymentId}")
    PaymentResponse partialUpdate(
            @Parameter(description = "Идентификатор платежа", example = "1")
            @PathVariable("paymentId") Long paymentId,
            @RequestBody PaymentRequest paymentRequest
    );

    @Operation(
            summary = "Удалить платёж",
            description = "Удаляет платёж по его идентификатору"
    )
    @DeleteMapping("/{paymentId}")
    void delete(
            @Parameter(description = "Идентификатор платежа", example = "1")
            @PathVariable("paymentId") Long paymentId
    );
}