package ru.v_and_a.application;

import org.springframework.data.domain.Pageable;
import ru.v_and_a.web.dto.DeliveryRequest;
import ru.v_and_a.web.dto.DeliveryResponse;

import java.util.List;

public interface DeliveryService {

    /**
     * Создаёт новую доставку
     * @param deliveryRequest данные для создания доставки
     * @return детали созданной доставки
     */
    DeliveryResponse create(DeliveryRequest deliveryRequest);

    /**
     * Возвращает список всех доставок с пагинацией
     * @param pageable параметры пагинации
     * @return список деталей доставок
     */
    List<DeliveryResponse> getAll(Pageable pageable);

    /**
     * Возвращает детали доставки по ID
     * @param deliveryId идентификатор доставки
     * @return детали доставки
     */
    DeliveryResponse getById(Long deliveryId);

    /**
     * Обновляет доставку по ID
     * @param deliveryId идентификатор доставки
     * @param deliveryRequest новые данные доставки
     * @return обновлённые детали доставки
     */
    DeliveryResponse update(Long deliveryId, DeliveryRequest deliveryRequest);

    /**
     * Удаляет доставку по ID
     * @param deliveryId идентификатор доставки
     */
    void delete(Long deliveryId);
}