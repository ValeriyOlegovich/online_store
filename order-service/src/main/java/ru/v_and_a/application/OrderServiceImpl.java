package ru.v_and_a.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.v_and_a.domain.model.Order;
import ru.v_and_a.domain.model.OrderStatus;
import ru.v_and_a.domain.repository.OrderRepository;
import ru.v_and_a.rabbitMQ.config.RabbitConfig;
import ru.v_and_a.rabbitMQ.events.OrderCancelledEvent;
import ru.v_and_a.web.client.PaymentClient;
import ru.v_and_a.web.dto.OrderRequest;
import ru.v_and_a.web.dto.OrderResponse;
import ru.v_and_a.web.dto.PaymentRequest;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        log.info("Вызов OrderServiceImpl.createOrder :", request);
        String uuid = UUID.randomUUID().toString();
        Order order = new Order();
        order.setUuid(uuid);
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(request.getTotalAmount());

        var paymentRequest = new PaymentRequest();
        paymentRequest.setOrderId(order.getUuid());
        paymentRequest.setAmount(order.getTotalAmount());
        paymentRequest.setCurrency("RUB");
        String idempotencyKey = order.getUserId() + "-" + order.hashCode();

       var response = paymentClient.createPayment(idempotencyKey, paymentRequest);
        orderRepository.save(order);

        return OrderResponse.builder()
                .message(response.getMessage())
                .uuid(uuid)
                .build();
    }

    @Override
    public Order getByUuid(String uuid) {
        return orderRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Заказ с UUID " + uuid + " не найден"));
    }

    @Override
    public Page<Order> getAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Override
    public Order updateOrder(String uuid, OrderRequest request) {
        Order existingOrder = orderRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Заказ с UUID " + uuid + " не найден"));

        existingOrder.setTotalAmount(request.getTotalAmount());
        existingOrder.setItems(request.getItems());

        orderRepository.save(existingOrder);

        return existingOrder;
    }

    @Override
    public Order partialUpdateOrder(String uuid, OrderRequest request) {
        Order existingOrder = orderRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Заказ с UUID " + uuid + " не найден"));

        if (request.getStatus() != null) {
            existingOrder.setStatus(request.getStatus());
        }

        orderRepository.save(existingOrder);
        return existingOrder;
    }

    @Override
    public void deleteOrder(String uuid) {
        if (!orderRepository.existsById(uuid)) {
            throw new IllegalArgumentException("Заказ с UUID " + uuid + " не найден");
        }
        orderRepository.deleteById(uuid);
    }

    @Override
    @Transactional
    public OrderResponse cancel(String orderUuid) {
        var message = new OrderCancelledEvent(orderUuid, OffsetDateTime.now(), "user cancelled");

        rabbitTemplate.convertAndSend(
                RabbitConfig.ORDER_EXCHANGE,
                RabbitConfig.ORDER_CANCELLED_ROUTING_KEY,
                message
        );

        orderRepository.cancelOrder(orderUuid);
        return OrderResponse.builder()
                .status(OrderStatus.CANCELLED)
                .uuid(orderUuid)
                .build();
    }
}