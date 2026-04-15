package ru.v_and_a.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.v_and_a.domain.model.Order;
import ru.v_and_a.domain.model.OrderStatus;
import ru.v_and_a.domain.repository.OrderRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Override
    public String createOrder() {
        String uuid = UUID.randomUUID().toString();
        Order order = new Order();
        order.setUuid(uuid);
        order.setStatus(OrderStatus.CREATED);

        orderRepository.save(order);
        return uuid;
    }

    @Override
    public String getStatusByUuid(String uuid) {
        Order order = orderRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Заказ с UUID " + uuid + " не найден"));
        return order.getStatus().getValue();
    }

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order updateOrder(String uuid, Order updatedOrder) {
        if (!orderRepository.existsById(uuid)) {
            throw new IllegalArgumentException("Заказ с UUID " + uuid + " не существует");
        }
        updatedOrder.setUuid(uuid); // гарантируем, что UUID не изменился
        orderRepository.save(updatedOrder);
        return updatedOrder;
    }

    @Override
    public Order partialUpdateOrder(String uuid, Order patch) {
        Order existingOrder = orderRepository.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Заказ с UUID " + uuid + " не найден"));

        if (patch.getStatus() != null) {
            existingOrder.setStatus(patch.getStatus());
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