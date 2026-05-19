package ru.v_and_a.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.v_and_a.domain.model.OrderSagaState;

public interface OrderSagaStateRepository extends JpaRepository<OrderSagaState, String> {

}
