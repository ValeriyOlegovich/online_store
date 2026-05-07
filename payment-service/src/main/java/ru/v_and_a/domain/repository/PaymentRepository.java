package ru.v_and_a.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.v_and_a.domain.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT p FROM Payment p WHERE p.orderUuid = :orderUuid")
    Payment getPaymentByOrderUuid(String orderUuid);

    @Modifying
    @Query("UPDATE Payment p SET p.status = PaymentStatus.CANCELLED WHERE p.id = :paymentId AND p.status != PaymentStatus.PAID")
    int cancelPayment(Long paymentId);
}
