package org.ex9.dealservice.repository;

import org.ex9.dealservice.model.InboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Репозиторий для работы с таблицей {@code inbox_event}.
 * @author Краковцев Артём
 */
@Repository
public interface InboxEventRepository extends JpaRepository<InboxEvent, UUID> {
}
