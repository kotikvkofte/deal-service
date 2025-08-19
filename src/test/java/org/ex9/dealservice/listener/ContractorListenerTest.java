package org.ex9.dealservice.listener;

import com.rabbitmq.client.Channel;
import org.ex9.dealservice.dto.rabbit.ContractorDto;
import org.ex9.dealservice.exception.DealContractorNotFondException;
import org.ex9.dealservice.model.InboxEvent;
import org.ex9.dealservice.repository.InboxEventRepository;
import org.ex9.dealservice.service.DealContractorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContractorListenerTest {

    @Mock
    private DealContractorService dealContractorService;

    @Mock
    private InboxEventRepository inboxEventRepository;

    @Mock
    private Channel channel;

    @InjectMocks
    private ContractorListener contractorListener;

    private ContractorDto contractorDto;
    private final UUID messageId = UUID.randomUUID();
    private final long deliveryTag = 1L;

    @BeforeEach
    void setUp() {
        contractorDto = ContractorDto.builder()
                .id("123")
                .name("Test contractor")
                .inn("77021653651")
                .modifyDateTime(LocalDateTime.now())
                .build();
        ReflectionTestUtils.setField(contractorListener, "retryCount", 5L);
    }

    @Test
    void handle_successfulMessage_processingAndSaveInbox() throws Exception {
        when(inboxEventRepository.existsById(messageId)).thenReturn(false);

        contractorListener.handle(contractorDto, channel, deliveryTag, messageId.toString(), null);

        verify(dealContractorService).updateDealContractorFomRabbit(contractorDto);
        verify(inboxEventRepository).save(any(InboxEvent.class));
        verify(channel).basicAck(deliveryTag, false);
    }

    @Test
    void handle_duplicateMessage_shouldSkip() throws Exception {
        when(inboxEventRepository.existsById(messageId)).thenReturn(true);

        contractorListener.handle(contractorDto, channel, deliveryTag, messageId.toString(), null);

        verify(dealContractorService, never()).updateDealContractorFomRabbit(any());
        verify(channel).basicAck(deliveryTag, false);
    }

    @Test
    void handle_notFoundException_shouldAck() throws Exception {
        when(inboxEventRepository.existsById(messageId)).thenReturn(false);
        doThrow(new DealContractorNotFondException("not found"))
                .when(dealContractorService).updateDealContractorFomRabbit(any());

        contractorListener.handle(contractorDto, channel, deliveryTag, messageId.toString(), null);

        verify(inboxEventRepository).save(any(InboxEvent.class));
        verify(channel).basicAck(deliveryTag, false);
    }

    @Test
    void handle_runtimeException_shouldReject() throws Exception {
        when(inboxEventRepository.existsById(messageId)).thenReturn(false);
        doThrow(new RuntimeException("DB error"))
                .when(dealContractorService).updateDealContractorFomRabbit(any());

        contractorListener.handle(contractorDto, channel, deliveryTag, messageId.toString(), null);

        verify(channel).basicReject(deliveryTag, false);
    }

    @Test
    void handle_runtimeException_shouldAckWhenRetryCount() throws Exception {
        when(inboxEventRepository.existsById(messageId)).thenReturn(false);

        doThrow(new RuntimeException("DB error"))
                .when(dealContractorService).updateDealContractorFomRabbit(any());

        Map<String, Object> xDeath = Map.of("count", 5, "messageId", messageId.toString());

        contractorListener.handle(contractorDto, channel, deliveryTag, messageId.toString(), xDeath);

        verify(channel).basicAck(deliveryTag, false);
        verify(channel, never()).basicReject(deliveryTag, false);
    }

}