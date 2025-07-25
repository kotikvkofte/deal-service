package org.ex9.dealservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ex9.dealservice.dto.*;
import org.ex9.dealservice.exception.DealNotFondException;
import org.ex9.dealservice.exception.DealStatusNotFondException;
import org.ex9.dealservice.service.DealService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DealService service;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public DealService dealService() {
            return Mockito.mock(DealService.class);
        }
    }

    @BeforeEach
    void setUp() {
        reset(service);
    }

    @Test
    void testSave_newDeal_success() throws Exception {
        DealSumDto sum = new DealSumDto("1000.00", "USD");

        DealSaveRequestDto request = DealSaveRequestDto.builder()
                .description("Test Deal")
                .agreementNumber("AGR-001")
                .agreementDate(LocalDate.now())
                .sum(sum)
                .build();

        UUID dealId = UUID.fromString("c9ddcc2a-d927-4904-89a0-7e666aae1644");

        when(service.dealSave(any(DealSaveRequestDto.class))).thenReturn(dealId);

        mockMvc.perform(put("/deal/save")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(content().string("\"" + dealId.toString() + "\""));

        verify(service, times(1)).dealSave(any(DealSaveRequestDto.class));
    }

    @Test
    void testSave_updateDeal_success() throws Exception {
        DealSumDto sum = new DealSumDto("2000.00", "EUR");

        DealSaveRequestDto request = DealSaveRequestDto.builder()
                .id(UUID.fromString("c9ddcc2a-d927-4904-89a0-7e666aae1644"))
                .description("Updated Deal")
                .agreementNumber("AGR-002")
                .agreementDate(LocalDate.now())
                .sum(sum)
                .build();

        UUID dealId = request.getId();

        when(service.dealSave(any(DealSaveRequestDto.class))).thenReturn(dealId);

        mockMvc.perform(put("/deal/save")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(content().string("\"" + dealId.toString() + "\""));

        verify(service, times(1)).dealSave(any(DealSaveRequestDto.class));
    }

    @Test
    void testSave_validationError_nullDescription() throws Exception {
        DealSumDto sum = new DealSumDto("1000.00", "USD");
        DealSaveRequestDto request = DealSaveRequestDto.builder()
                .agreementNumber("AGR-001")
                .agreementDate(LocalDate.now())
                .sum(sum)
                .build();

        mockMvc.perform(put("/deal/save")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed: description must not be null"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verifyNoInteractions(service);
    }

    @Test
    void testSave_validationError_invalidSumValue() throws Exception {
        String invalidRequest = "{" +
                "\"description\": \"Test Deal\", " +
                "\"agreementNumber\": \"AGR-001\", " +
                "\"agreementDate\": \"" + LocalDate.now() + "\", " +
                "\"sum\": {" +
                "\"value\": \"invalid\", \"currency\": \"USD\"" +
                "}" +
                "}";

        mockMvc.perform(put("/deal/save")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed: value must be a valid decimal number with two decimal places (100000.00)"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verifyNoInteractions(service);
    }

    @Test
    void testSave_validationError_nullCurrency() throws Exception {
        DealSumDto sum = new DealSumDto("100000.00", null);
        DealSaveRequestDto request = DealSaveRequestDto.builder()
                .description("Test Deal")
                .agreementNumber("CREDIT-2023-001")
                .agreementDate(LocalDate.now())
                .sum(sum)
                .build();

        mockMvc.perform(put("/deal/save")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed: currency must not be null"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verifyNoInteractions(service);
    }

    @Test
    void testSave_dealNotFound() throws Exception {
        DealSumDto sum = new DealSumDto("1000.00", "USD");

        DealSaveRequestDto request = DealSaveRequestDto.builder()
                .id(UUID.fromString("c9ddcc2a-d927-4904-89a0-7e666aae1644"))
                .description("Test Deal")
                .agreementNumber("AGR-001")
                .agreementDate(LocalDate.now())
                .sum(sum)
                .build();
        String errorMessage = "Deal with id '" + request.getId() + "' not found";

        when(service.dealSave(any(DealSaveRequestDto.class)))
                .thenThrow(new DealNotFondException(errorMessage));

        mockMvc.perform(put("/deal/save")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verify(service, times(1)).dealSave(any(DealSaveRequestDto.class));
    }

    @Test
    void testSave_invalidIdFormat() throws Exception {
        DealSumDto sum = new DealSumDto("1000.00", "USD");
        String invalidRequest = "{\"id\": \"invalid-uuid\", \"description\": \"Test Deal\", \"agreementNumber\": \"AGR-001\", \"agreementDate\": \"" + LocalDate.now() + "\", \"sum\": {\"sum\": \"1000.00\", \"currency\": \"USD\"}}";

        mockMvc.perform(put("/deal/save")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid UUID format"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verifyNoInteractions(service);
    }

    @Test
    void testSave_serverError() throws Exception {
        DealSumDto sum = new DealSumDto("1000.00", "USD");
        DealSaveRequestDto request = DealSaveRequestDto.builder()
                .description("Test Deal")
                .agreementNumber("AGR-001")
                .agreementDate(LocalDate.now())
                .sum(sum)
                .build();

        when(service.dealSave(request))
                .thenThrow(new RuntimeException("Unexpected server error"));

        mockMvc.perform(put("/deal/save")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected server error"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verify(service, times(1)).dealSave(request);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testChangeStatus_success() throws Exception {
        DealChangeStatusDto request = new DealChangeStatusDto(UUID.fromString("11111111-2222-3333-4444-555555555555"), "APPROVED");

        doNothing().when(service).changeStatus(any(DealChangeStatusDto.class));

        mockMvc.perform(patch("/deal/change/status")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(service, times(1)).changeStatus(any(DealChangeStatusDto.class));
    }

    @Test
    void testChangeStatus_statusNotFound() throws Exception {
        DealChangeStatusDto request = new DealChangeStatusDto(UUID.fromString("11111111-2222-3333-4444-555555555555"), "APPROVED");
        String errorMessage = "Deal Status with id 'APPROVED' not found";

        doThrow(new DealStatusNotFondException(errorMessage))
                .when(service).changeStatus(any(DealChangeStatusDto.class));

        mockMvc.perform(patch("/deal/change/status")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verify(service, times(1)).changeStatus(any(DealChangeStatusDto.class));
    }

    @Test
    void testChangeStatus_validationError_nullDealId() throws Exception {
        DealChangeStatusDto request = new DealChangeStatusDto(null, "APPROVED");

        mockMvc.perform(patch("/deal/change/status")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed: dealId must not be null"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verifyNoInteractions(service);
    }

    @Test
    void testChangeStatus_validationError_nullStatusId() throws Exception {
        DealChangeStatusDto request = new DealChangeStatusDto(UUID.fromString("11111111-2222-3333-4444-555555555555"), null);

        mockMvc.perform(patch("/deal/change/status")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed: statusId must not be null"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verifyNoInteractions(service);
    }

    @Test
    void testChangeStatus_invalidDealIdFormat() throws Exception {
        String invalidRequest = "{\"dealId\": \"invalid-uuid\", \"statusId\": \"APPROVED\"}";

        mockMvc.perform(patch("/deal/change/status")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid UUID format"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verifyNoInteractions(service);
    }

    @Test
    void testChangeStatus_dealNotFound() throws Exception {
        DealChangeStatusDto request = new DealChangeStatusDto(UUID.fromString("11111111-2222-3333-4444-555555555555"), "APPROVED");
        String errorMessage = "Deal with id '11111111-2222-3333-4444-555555555555' not found";

        doThrow(new DealNotFondException(errorMessage))
                .when(service).changeStatus(request);

        mockMvc.perform(patch("/deal/change/status")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verify(service, times(1)).changeStatus(request);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testChangeStatus_serverError() throws Exception {
        DealChangeStatusDto request = new DealChangeStatusDto(UUID.fromString("11111111-2222-3333-4444-555555555555"), "APPROVED");

        doThrow(new RuntimeException("Unexpected server error"))
                .when(service).changeStatus(request);

        mockMvc.perform(patch("/deal/change/status")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected server error"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verify(service, times(1)).changeStatus(request);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testGetDeal_success() throws Exception {
        UUID id = UUID.randomUUID();

        DealResponseDto response = new DealResponseDto();
        response.setId(id);
        response.setDescription("Test Deal");
        response.setAgreementNumber("AGR-001");
        response.setAgreementDate(LocalDate.now());
        response.setStatus(new DealStatusDto("ACTIVE", "Утвержденная"));

        when(service.getDealById(id)).thenReturn(response);

        mockMvc.perform(get("/deal/deal/" + id)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.description").value("Test Deal"))
                .andExpect(jsonPath("$.agreementNumber").value("AGR-001"));


        verify(service, times(1)).getDealById(id);
    }

    @Test
    void testGetDeal_invalidIdFormat() throws Exception {
        String invalidId = "invalid-uuid";

        mockMvc.perform(get("/deal/deal/" + invalidId)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected server error"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verifyNoInteractions(service);
    }

    @Test
    void testGetDeal_notFound() throws Exception {
        UUID id = UUID.fromString("11111111-2222-3333-4444-555555555555");
        String errorMessage = "Deal with id '" + id + "' not found";

        when(service.getDealById(id)).thenThrow(new DealNotFondException(errorMessage));

        mockMvc.perform(get("/deal/deal/" + id)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verify(service, times(1)).getDealById(id);
    }

    @Test
    void testSearchDeals_success_withSorting() throws Exception {
        DealSearchRequestDto request = new DealSearchRequestDto();
        request.setDescription("Test Deal");
        request.setAgreementNumber("AGR-001");
        request.setStatusIds(List.of("ACTIVE"));
        request.setPage(0);
        request.setSize(10);
        request.setSortBy("description");
        request.setSortDirection("ASC");

        DealResponseDto deal = new DealResponseDto();
        deal.setId(UUID.randomUUID());
        deal.setDescription("Test Deal");
        deal.setAgreementNumber("AGR-001");
        deal.setAgreementDate(LocalDate.now());
        deal.setStatus(new DealStatusDto("ACTIVE", "Утвержденная"));
        Page<DealResponseDto> page = new PageImpl<>(List.of(deal), PageRequest.of(0, 10), 1);

        when(service.searchDeals(any(DealSearchRequestDto.class))).thenReturn(page);

        mockMvc.perform(post("/deal/search")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].description").value("Test Deal"))
                .andExpect(jsonPath("$.content[0].agreementNumber").value("AGR-001"))
                .andExpect(jsonPath("$.content[0].status.id").value("ACTIVE"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));

        verify(service, times(1)).searchDeals(any(DealSearchRequestDto.class));
    }

    @Test
    void testSearchDeals_emptyRequest_success() throws Exception {
        DealSearchRequestDto request = new DealSearchRequestDto();
        request.setPage(0);
        request.setSize(10);

        Page<DealResponseDto> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);

        when(service.searchDeals(request)).thenReturn(page);

        mockMvc.perform(post("/deal/search")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));

        verify(service, times(1)).searchDeals(request);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testSearchDeals_validationError_negativePage() throws Exception {
        DealSearchRequestDto request = new DealSearchRequestDto();
        request.setPage(-1);
        request.setSize(10);

        mockMvc.perform(post("/deal/search")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed: page must be greater than or equal to 0"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verifyNoInteractions(service);
    }

    @Test
    void testSearchDeals_serverError() throws Exception {
        DealSearchRequestDto request = new DealSearchRequestDto();
        request.setPage(0);
        request.setSize(10);

        when(service.searchDeals(request)).thenThrow(new RuntimeException("Unexpected server error"));

        mockMvc.perform(post("/deal/search")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected server error"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verify(service, times(1)).searchDeals(request);
        verifyNoMoreInteractions(service);
    }

}