package ru.v_and_a.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.v_and_a.domain.model.OutboxEvent;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, String> {
    @Modifying(clearAutomatically = true)
    @Query(value="UPDATE outbox SET published=true WHERE id=?1",
            nativeQuery = true)
    void markAsPublished(Long eventId);

    @Query(value = "SELECT * FROM outbox WHERE published = false LIMIT 50",
            nativeQuery = true)
    List<OutboxEvent> find50ByPublishedFalse();
}
