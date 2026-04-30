package ru.v_and_a.application;

import org.springframework.data.domain.Pageable;
import ru.v_and_a.application.command.DeliveryCommand;
import ru.v_and_a.application.dto.DeliveryDetails;

import java.util.List;

public interface DeliveryService {

    /**
     * Создаёт новую доставку
     * @param deliveryCommand данные для создания доставки
     * @return детали созданной доставки
     */
    String create(DeliveryCommand deliveryCommand);

    /**
     * Возвращает список всех доставок с пагинацией
     * @param pageable параметры пагинации
     * @return список деталей доставок
     */
    List<DeliveryDetails> getAll(Pageable pageable);

    /**
     * Возвращает детали доставки по ID
     * @param deliveryId идентификатор доставки
     * @return детали доставки
     */
    DeliveryDetails getById(Long deliveryId);

    /**
     * Обновляет доставку по ID
     * @param deliveryId идентификатор доставки
     * @param deliveryCommand новые данные доставки
     * @return обновлённые детали доставки
     */
    DeliveryDetails update(Long deliveryId, DeliveryCommand deliveryCommand);

    /**
     * Удаляет доставку по ID
     * @param deliveryId идентификатор доставки
     */
    void delete(Long deliveryId);
}