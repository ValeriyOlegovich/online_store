package ru.v_and_a.web;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.v_and_a.application.OrderService;
import ru.v_and_a.domain.model.Order;
import ru.v_and_a.web.dto.OrderResponse;
import ru.v_and_a.web.dto.OrderRequest;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private static final Logger LOGGER = LogManager.getLogger(OrderController.class);

    private final OrderService orderService;

    /**
     * Создаёт новый заказ
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createOrder(@RequestBody OrderRequest request) {
        LOGGER.info("Вызов OrderController.createOrder");
        String orderId = orderService.createOrder(request);
        return orderId;
    }

    /**
     * Возвращает заказ по UUID
     */
    @GetMapping("/{uuid}")
    public OrderResponse getByUuid(@PathVariable String uuid) {
        LOGGER.info("Вызов OrderController.getByUuid для заказа: {}", uuid);
        Order order = orderService.getByUuid(uuid);
        return mapToResponse(order);
    }

    /**
     * Возвращает список всех заказов (с пагинацией)
     */
    @GetMapping
    public Page<OrderResponse> getAll(Pageable pageable) {
        LOGGER.info("Вызов OrderController.getAll");
        return orderService.getAll(pageable).map(this::mapToResponse);
    }

    /**
     * Полное обновление заказа
     */
    @PutMapping("/{uuid}")
    public OrderResponse updateOrder(@PathVariable String uuid, @RequestBody OrderRequest request) {
        LOGGER.info("Обновление заказа с UUID: {}", uuid);
        Order updatedOrder = orderService.updateOrder(uuid, request);
        return mapToResponse(updatedOrder);
    }

    /**
     * Частичное обновление заказа
     */
    @PatchMapping("/{uuid}")
    public OrderResponse partialUpdateOrder(@PathVariable String uuid, @RequestBody OrderRequest patch) {
        LOGGER.info("Частичное обновление заказа: {}", uuid);
        Order patchedOrder = orderService.partialUpdateOrder(uuid, patch);
        return mapToResponse(patchedOrder);
    }

    /**
     * Удаляет заказ
     */
    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable String uuid) {
        LOGGER.info("Удаление заказа: {}", uuid);
        orderService.deleteOrder(uuid);
    }

    // Вспомогательный метод для преобразования сущности в DTO
    private OrderResponse mapToResponse(Order order) {
        if (order == null) return null;
        return OrderResponse.builder()
                .uuid(order.getUuid())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}