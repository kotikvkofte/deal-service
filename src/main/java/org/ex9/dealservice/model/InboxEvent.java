package org.ex9.dealservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность события входящего сообщения.
 * <p>
 * Используется для реализации паттерна Inbox.
 * </p>
 * @author Краковцев Артём
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inbox_event")
public class InboxEvent {

    /**Уникальный идентификатор сообщения (соответствует {@code messageId} из RabbitMQ)*/
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    /**Тип события (например, {@code ContractorUpdate})*/
    @NotNull
    @Column(name = "type", nullable = false, length = Integer.MAX_VALUE)
    private String type;

    /**Дата и время получения события*/
    @NotNull
    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

}
