package ru.v_and_a.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.v_and_a.domain.model.Order;
import ru.v_and_a.web.dto.OrderRequest;
import ru.v_and_a.web.dto.OrderResponse;

public interface OrderService {

    /**
     * Создаёт новый заказ на основе данных из запроса.
     *
     * @param request данные заказа
     * @return UUID созданного заказа
     */
    OrderResponse createOrder(OrderRequest request);

    /**
     * Возвращает заказ по его UUID.
     *
     * @param uuid идентификатор заказа
     * @return сущность заказа
     */
    Order getByUuid(String uuid);

    /**
     * Возвращает страницу заказов (с пагинацией).
     *
     * @param pageable параметры пагинации (страница, размер, сортировка)
     * @return страница с заказами в виде DTO
     */
    Page<Order> getAll(Pageable pageable);

    /**
     * Полностью обновляет заказ.
     *
     * @param uuid идентификатор заказа
     * @param request новые данные заказа
     * @return обновлённый заказ
     */
    Order updateOrder(String uuid, OrderRequest request);

    /**
     * Частично обновляет заказ (например, только статус).
     *
     * @param uuid идентификатор заказа
     * @param request поля для обновления
     * @return обновлённый заказ
     */
    Order partialUpdateOrder(String uuid, OrderRequest request);

    /**
     * Удаляет заказ по UUID.
     *
     * @param uuid идентификатор заказа
     */
    void deleteOrder(String uuid);
}