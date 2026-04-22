package ru.v_and_a.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.v_and_a.domain.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
