package org.ex9.dealservice.listener;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.ex9.dealservice.dto.rabbit.ContractorDto;
import org.ex9.dealservice.exception.DealContractorNotFondException;
import org.ex9.dealservice.model.InboxEvent;
import org.ex9.dealservice.repository.InboxEventRepository;
import org.ex9.dealservice.service.DealContractorService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Слушатель очереди сообщений о контрагентах.
 * <p>
 * Обрабатывает сообщения из dealContractorsQueue.
 * Использует шаблон inbox для защиты от повторной обработки сообщений (каждое сообщение проверяется по уникальному {@code messageId}).
 * </p>
 *
 * @author Краковев Артём
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class ContractorListener {

    @Value("${spring.rabbitmq.retryCount}")
    private Long retryCount;

    private final DealContractorService dealContractorService;
    private final InboxEventRepository inboxEventRepository;

    /**
     * Логика обработки:
     * <ul>
     *   <li>Если сообщение уже обработано — подтверждается (ACK) и игнорируется.</li>
     *   <li>Если обработка успешна — обновляет данные контрагента и сохраняет запись в {@link InboxEventRepository}.</li>
     *   <li>Если контрагент не найден — пишет запись в inbox и подтверждает (ACK), чтобы не застревало в очереди.</li>
     *   <li>Если возникает ошибка (например, проблемы с БД) — сообщение отклоняется (REJECT) и попадает в DLQ.</li>
     * </ul>
     */
    @RabbitListener(queues = "${spring.rabbitmq.queues.contractor}", containerFactory = "rabbitListenerContainerFactory")
    public void handle(ContractorDto contractorDto,
                       Channel channel,
                       @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                       @Header(AmqpHeaders.MESSAGE_ID) String messageId,
                       @Header(name = "x-death", required = false) Map<String, Object> xDeathHeader) throws IOException {
        log.info("Received contractor: {}", contractorDto);

        UUID msgId = UUID.fromString(messageId);
        try {
            if (inboxEventRepository.existsById(msgId)) {
                log.info("Skip duplicate message {}", msgId);
                channel.basicAck(deliveryTag, false);
                return;
            }

            dealContractorService.updateDealContractorFomRabbit(contractorDto);

            inboxEventRepository.save(new InboxEvent(msgId, "ContractorUpdate", LocalDateTime.now()));

            log.info("Updated contractor: {}", contractorDto);

            channel.basicAck(deliveryTag, false);
        } catch (DealContractorNotFondException ex) {
            log.warn("DealContractorNotFondException: {}", ex.getMessage());
            inboxEventRepository.save(new InboxEvent(msgId, "ContractorUpdate", LocalDateTime.now()));
            channel.basicAck(deliveryTag, false);
        } catch (RuntimeException e) {
            if (checkRetryCount(xDeathHeader)) {
                log.warn("Maximum retry for message: {}", msgId);
                channel.basicAck(deliveryTag, false);
            } else {
                log.error("Error while updating contractor: {}", e.getMessage());
                channel.basicReject(deliveryTag, false);
            }
        }
    }

    private boolean checkRetryCount(Map<String, Object> xDeathHeader) {
        if (xDeathHeader != null && !xDeathHeader.isEmpty()) {
            Long count = (Long) xDeathHeader.get("count");
            return count >= retryCount;
        }

        return false;
    }

}
