package ru.v_and_a.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.v_and_a.dto.Order;
import ru.v_and_a.service.OrderService;

@RestController()
@RequestMapping("/orders")
public class OrderController {
    protected final Logger LOGGER = LogManager.getLogger(OrderController.class);

    private final OrderService orderServise;

    public OrderController(OrderService orderServise) {
        this.orderServise = orderServise;
    }

    @PostMapping("/createOrder")
    public String createOrder() {
        LOGGER.info("Вызов OrderControlle.createOrder");
        String result = orderServise.createOrder();

        return result;
    }

    @GetMapping("/getStatusByUuid")
    public String getStatusByUuid() {
        LOGGER.info("Вызов OrderControlle.getStatusByUuid");
        String result = orderServise.getStatusByUuid();
        return result;
    }

}
