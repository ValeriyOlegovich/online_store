package ru.v_and_a.application;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.v_and_a.domain.model.Order;
import ru.v_and_a.domain.model.OrderStatus;
import ru.v_and_a.domain.repository.OrderRepository;
import ru.v_and_a.web.client.PaymentClient;
import ru.v_and_a.web.dto.OrderRequest;
import ru.v_and_a.web.dto.PaymentRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;

    @Override
    public String createOrder(OrderRequest request) {
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

        paymentClient.createPayment(idempotencyKey, paymentRequest);

        orderRepository.save(order);

        return uuid;
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

        // Обновляем поля (пример)
        // existingOrder.setCustomerId(request.getCustomerId());

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
}