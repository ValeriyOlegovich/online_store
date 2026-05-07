package ru.v_and_a.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import ru.v_and_a.application.OrderService;
import ru.v_and_a.domain.model.Order;
import ru.v_and_a.web.api.OrderApi;
import ru.v_and_a.web.dto.OrderRequest;
import ru.v_and_a.web.dto.OrderResponse;

@RestController
@RequiredArgsConstructor
@Slf4j
public class OrderController implements OrderApi {

    private final OrderService orderService;

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        return orderService.createOrder(request);
    }

    @Override
    public Page<OrderResponse> getAll(Pageable pageable) {
        log.info("Вызов OrderController.getAll");
        return orderService.getAll(pageable).map(this::mapToResponse);
    }

    @Override
    public OrderResponse getByUuid(String uuid) {
        return mapToResponse(orderService.getByUuid(uuid));
    }

    @Override
    public OrderResponse updateOrder(String uuid, OrderRequest request) {
        return mapToResponse(orderService.updateOrder(uuid, request));
    }

    @Override
    public OrderResponse partialUpdateOrder(String uuid, OrderRequest request) {
        return mapToResponse(orderService.partialUpdateOrder(uuid, request));
    }

    @Override
    public void deleteOrder(String uuid) {
        orderService.deleteOrder(uuid);
    }

    // Вспомогательный метод для преобразования сущности в DTO
    private OrderResponse mapToResponse(Order order) {
        if (order == null) return null;
        return OrderResponse.builder()
                .uuid(order.getUuid())
                .status(order.getStatus())
                .build();
    }

    @Override
    public OrderResponse cancelOrder(String OrderUuid) {
        return orderService.cancel(OrderUuid);
    }
}