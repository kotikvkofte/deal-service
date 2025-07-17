package org.ex9.dealservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ex9.dealservice.dto.DealContractorSaveRequestDto;
import org.ex9.dealservice.exception.DealContractorNotFondException;
import org.ex9.dealservice.exception.DealNotFondException;
import org.ex9.dealservice.service.DealContractorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DealContractorController.class)
class DealContractorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DealContractorService service;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public DealContractorService dealContractorService() {
            return Mockito.mock(DealContractorService.class);
        }
    }

    @BeforeEach
    void setUp() {
        reset(service);
    }

    @Test
    void testSaveContractor_success() throws Exception {
        DealContractorSaveRequestDto request = new DealContractorSaveRequestDto();
        request.setDealId(UUID.fromString("11111111-2222-3333-4444-555555555555"));
        request.setContractorId("66666666-7777-8888-9999-000000000000");
        UUID dealContractorId = UUID.randomUUID();

        when(service.saveDealContractor(any(DealContractorSaveRequestDto.class))).thenReturn(dealContractorId);

        mockMvc.perform(put("/deal-contractor/save")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("\"" + dealContractorId.toString() + "\""));

        verify(service, times(1)).saveDealContractor(any(DealContractorSaveRequestDto.class));
    }

    @Test
    void testSaveContractor_validationError_nullDealId() throws Exception {
        DealContractorSaveRequestDto request = new DealContractorSaveRequestDto();
        request.setContractorId("66666666-7777-8888-9999-000000000000");

        mockMvc.perform(put("/deal-contractor/save")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(service, never()).saveDealContractor(any());
    }

    @Test
    void testSaveContractor_validationError_nullContractorId() throws Exception {
        DealContractorSaveRequestDto request = new DealContractorSaveRequestDto();
        request.setDealId(UUID.fromString("11111111-2222-3333-4444-555555555555"));

        mockMvc.perform(put("/deal-contractor/save")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed: must not be null"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verifyNoInteractions(service);
    }

    @Test
    void testSaveContractor_invalidDealIdFormat() throws Exception {
        String invalidRequest = "{\"dealId\": \"invalid-uuid\", \"contractorId\": \"contractorId\"}";

        mockMvc.perform(put("/deal-contractor/save")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid UUID format"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verifyNoInteractions(service);
    }

    @Test
    void testSaveContractor_contractorNotFound() throws Exception {
        DealContractorSaveRequestDto request = new DealContractorSaveRequestDto();
        request.setDealId(UUID.fromString("11111111-2222-3333-4444-555555555555"));
        request.setContractorId("contractorId");
        String errorMessage = "Contractor with id 'contractorId' not found";

        doThrow(new DealContractorNotFondException(errorMessage))
                .when(service).saveDealContractor(request);

        mockMvc.perform(put("/deal-contractor/save")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verify(service, times(1)).saveDealContractor(request);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testSaveContractor_dealNotFound() throws Exception {
        DealContractorSaveRequestDto request = new DealContractorSaveRequestDto();
        request.setDealId(UUID.fromString("11111111-2222-3333-4444-555555555555"));
        request.setContractorId("contractorId");
        String errorMessage = "Deal with id '11111111-2222-3333-4444-555555555555' not found";

        doThrow(new DealNotFondException(errorMessage))
                .when(service).saveDealContractor(request);

        mockMvc.perform(put("/deal-contractor/save")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verify(service, times(1)).saveDealContractor(request);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testSaveContractor_serverError() throws Exception {
        DealContractorSaveRequestDto request = new DealContractorSaveRequestDto();
        request.setDealId(UUID.fromString("11111111-2222-3333-4444-555555555555"));
        request.setContractorId("contractorId");

        doThrow(new RuntimeException("Unexpected server error"))
                .when(service).saveDealContractor(request);

        mockMvc.perform(put("/deal-contractor/save")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected server error"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verify(service, times(1)).saveDealContractor(request);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testDeleteContractor_success() throws Exception {
        UUID id = UUID.fromString("11111111-2222-3333-4444-555555555555");

        doNothing().when(service).deleteDealContractor(id);

        mockMvc.perform(delete("/deal-contractor/delete/" + id)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteDealContractor(id);
    }

    @Test
    void testDeleteContractor_notFound() throws Exception {
        UUID id = UUID.fromString("11111111-2222-3333-4444-555555555555");
        String errorMessage = "Contractor with id '" + id + "' not found";

        doThrow(new DealContractorNotFondException(errorMessage))
                .when(service).deleteDealContractor(id);

        mockMvc.perform(delete("/deal-contractor/delete/" + id)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verify(service, times(1)).deleteDealContractor(id);
    }

    @Test
    void testDeleteContractor_serverError() throws Exception {
        UUID id = UUID.fromString("c9ddcc2a-d927-4904-89a0-7e666aae1644");

        doThrow(new RuntimeException("Unexpected server error"))
                .when(service).deleteDealContractor(eq(id));

        mockMvc.perform(delete("/deal-contractor/delete/" + id)
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected server error"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verify(service, times(1)).deleteDealContractor(eq(id));
        verifyNoMoreInteractions(service);
    }

}