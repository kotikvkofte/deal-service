package org.ex9.dealservice.listener;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.ex9.dealservice.config.RabbitMQConfig;
import org.ex9.dealservice.dto.rabbit.ContractorDto;
import org.ex9.dealservice.exception.DealContractorNotFondException;
import org.ex9.dealservice.model.InboxEvent;
import org.ex9.dealservice.repository.InboxEventRepository;
import org.ex9.dealservice.service.DealContractorService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Слушатель очереди сообщений о контрагентах.
 * <p>
 * Обрабатывает сообщения из {@link RabbitMQConfig#DEALS_CONTRACTOR_QUEUE}.
 * Использует шаблон inbox для защиты от повторной обработки сообщений (каждое сообщение проверяется по уникальному {@code messageId}).
 * </p>
 * @author Краковев Артём
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class ContractorListener {

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
    @RabbitListener(queues = RabbitMQConfig.DEALS_CONTRACTOR_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void handle(ContractorDto contractorDto,
                       Channel channel,
                       @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag,
                       @Header(AmqpHeaders.MESSAGE_ID) String messageId) throws IOException {
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
            log.error("Error while updating contractor: {}", e.getMessage());
            channel.basicReject(deliveryTag, false);
        }

    }

}
