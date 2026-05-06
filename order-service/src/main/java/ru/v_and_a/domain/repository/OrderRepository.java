package ru.v_and_a.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.v_and_a.domain.model.Order;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    /**
     * Находит заказ по UUID.
     *
     * @param uuid идентификатор заказа
     * @return Optional с заказом, если найден
     */
    Optional<Order> findById(String uuid);

    /**
     * Проверяет, существует ли заказ с указанным UUID.
     *
     * @param uuid идентификатор заказа
     * @return true, если заказ существует
     */
    boolean existsById(String uuid);

    // Метод для отмены заказов
    @Modifying
    @Query("UPDATE Order o SET o.status = ru.v_and_a.domain.model.OrderStatus.CANCELLED WHERE o.uuid = :orderUuid")
    void cancelOrder(String orderUuid);
}
