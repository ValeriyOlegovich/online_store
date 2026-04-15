package ru.v_and_a.application;

import org.springframework.stereotype.Service;
import ru.v_and_a.domain.model.Order;

import java.util.List;

@Service
public interface OrderService {
    /**
     * Создаёт новый заказ.
     *
     * @return UUID созданного заказа
     */
    String createOrder();

    /**
     * Возвращает статус заказа по его UUID.
     *
     * @param uuid идентификатор заказа
     * @return строковое представление статуса заказа
     */
    String getStatusByUuid(String uuid);

    /**
     * Возвращает список всех заказов.
     *
     * @return список заказов
     */
    List<Order> getAllOrders();

    /**
     * Полностью обновляет заказ по его UUID.
     *
     * @param uuid идентификатор заказа
     * @param updatedOrder обновлённые данные заказа
     * @return обновлённый заказ
     */
    Order updateOrder(String uuid, Order updatedOrder);

    /**
     * Частично обновляет заказ (например, только статус).
     *
     * @param uuid идентификатор заказа
     * @param patch частичные данные для обновления
     * @return обновлённый заказ
     */
    Order partialUpdateOrder(String uuid, Order patch);

    /**
     * Удаляет заказ по его UUID.
     *
     * @param uuid идентификатор заказа
     */
    void deleteOrder(String uuid);
}
