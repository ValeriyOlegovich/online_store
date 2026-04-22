package ru.v_and_a.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.v_and_a.domain.model.IdempotencyKey;

import java.util.Optional;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {

    @Query(value = """
            SELECT *
            FROM idempotency_key ik
            WHERE ik.idempotency_key = :key
              AND ik.created_at > DATEADD('HOUR', -1, NOW())
            """,
            nativeQuery = true)
    Optional<IdempotencyKey> findByKey(String key);
}
