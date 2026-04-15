package ru.v_and_a.web;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import ru.v_and_a.application.OrderService;
import ru.v_and_a.domain.model.Order;

import java.util.List;

@RestController()
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    protected final Logger LOGGER = LogManager.getLogger(OrderController.class);

    private final OrderService orderService;

    @PostMapping("/createOrder")
    public String createOrder() {
        LOGGER.info("Вызов OrderControlle.createOrder");
        String result = orderService.createOrder();

        return result;
    }

    @GetMapping("/getStatusByUuid/{uuid}")
    public String getStatusByUuid(@PathVariable String uuid) {
        LOGGER.info("Вызов OrderControlle.getStatusByUuid");
        String result = orderService.getStatusByUuid(uuid);
        return result;
    }

    /**
     * Возвращает список всех заказов (READ)
     */
    @GetMapping("/getAll")
    public List<Order> getAllOrders() {
        LOGGER.info("Вызов OrderController.getAllOrders");
        return orderService.getAllOrders();
    }

    /**
     * Полное обновление заказа (UPDATE)
     */
    @PutMapping("/update/{uuid}")
    public Order updateOrder(@PathVariable String uuid, @RequestBody Order updatedOrder) {
        LOGGER.info("Обновление заказа с UUID: {}", uuid);
        return orderService.updateOrder(uuid, updatedOrder);
    }

    /**
     * Частичное обновление заказа (например, только статус)
     */
    @PatchMapping("/partialUpdate/{uuid}")
    public Order partialUpdateOrder(@PathVariable String uuid, @RequestBody Order patch) {
        LOGGER.info("Частичное обновление заказа: {}", uuid);
        return orderService.partialUpdateOrder(uuid, patch);
    }

    /**
     * Удаляет заказ (DELETE)
     */
    @DeleteMapping("/delete/{uuid}")
    public void deleteOrder(@PathVariable String uuid) {
        LOGGER.info("Удаление заказа: {}", uuid);
        orderService.deleteOrder(uuid);
    }
}
