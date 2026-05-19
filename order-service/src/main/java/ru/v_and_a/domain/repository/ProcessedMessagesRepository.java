package ru.v_and_a.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.v_and_a.domain.model.DeliveryCreatedMessage;

@Repository
public interface ProcessedMessagesRepository extends JpaRepository<DeliveryCreatedMessage, String> {

    @Query(
            value = """
                    SELECT * 
                    FROM delivery_created_message 
                    WHERE idempotent_key = :idempotencyKey
                      AND processed_at > DATEADD('DAY', -7, NOW())
                    """
            , nativeQuery = true
    )
    DeliveryCreatedMessage getByIdempotencyKey(String idempotencyKey);

    @Query(
            value = """
                    SELECT count(*) > 0
                    FROM delivery_created_message 
                    WHERE idempotent_key = :idempotencyKey
                      AND processed_at > DATEADD('DAY', -7, NOW())
                    """
            , nativeQuery = true
    )
    boolean isAlreadyProcessed(@Param("idempotencyKey") String idempotentKey);
}