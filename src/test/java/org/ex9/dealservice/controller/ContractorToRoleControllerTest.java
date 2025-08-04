package org.ex9.dealservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ex9.dealservice.dto.ContractorToRoleDto;
import org.ex9.dealservice.exception.ContractorRoleNotFondException;
import org.ex9.dealservice.exception.DealContractorNotFondException;
import org.ex9.dealservice.service.ContractorToRoleService;
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
import org.springframework.test.web.servlet.MockMvc;
import org.ex9.dealservice.exception.ContractorToRoleNotFondException;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ContractorToRoleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContractorToRoleService service;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public ContractorToRoleService contractorToRoleService() {
            return Mockito.mock(ContractorToRoleService.class);
        }
    }

    @BeforeEach
    void setUp() {
        reset(service);
    }

    @Test
    void testAddRoleToContractor_success() throws Exception {
        ContractorToRoleDto request = new ContractorToRoleDto();
        request.setContractorId(UUID.fromString("11111111-2222-3333-4444-555555555555"));
        request.setRoleId("BORROWER");

        doNothing().when(service).addNewRole(any(ContractorToRoleDto.class));

        mockMvc.perform(post("/contractor-to-role/add")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(service, times(1)).addNewRole(any(ContractorToRoleDto.class));
    }

    @Test
    void testAddRoleToContractor_validationError_nullContractorId() throws Exception {
        ContractorToRoleDto request = new ContractorToRoleDto();
        request.setRoleId("BORROWER");

        mockMvc.perform(post("/contractor-to-role/add")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(service, never()).addNewRole(any());
    }

    @Test
    void testDeleteRoleToContractor_success() throws Exception {
        ContractorToRoleDto request = new ContractorToRoleDto();
        request.setContractorId(UUID.fromString("11111111-2222-3333-4444-555555555555"));
        request.setRoleId("BORROWER");

        doNothing().when(service).deleteRole(any(ContractorToRoleDto.class));

        mockMvc.perform(delete("/contractor-to-role/delete")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteRole(any(ContractorToRoleDto.class));
    }

    @Test
    void testDeleteRoleToContractor_notFound() throws Exception {
        ContractorToRoleDto request = new ContractorToRoleDto();
        request.setContractorId(UUID.fromString("11111111-2222-3333-4444-555555555555"));
        request.setRoleId("BORROWER");
        String errorMessage = "Role 'BORROWER' for contractor '11111111-2222-3333-4444-555555555555' not found";

        doThrow(new ContractorToRoleNotFondException(errorMessage))
                .when(service).deleteRole(any(ContractorToRoleDto.class));

        mockMvc.perform(delete("/contractor-to-role/delete")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verify(service, times(1)).deleteRole(any(ContractorToRoleDto.class));
    }

    @Test
    void testDeleteRoleToContractor_validationError_nullRoleId() throws Exception {
        ContractorToRoleDto request = new ContractorToRoleDto();
        request.setContractorId(UUID.fromString("11111111-2222-3333-4444-555555555555"));

        mockMvc.perform(delete("/contractor-to-role/delete")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(service, never()).deleteRole(any());
    }

    @Test
    void testAddRoleToContractor_contractorNotFound() throws Exception {
        ContractorToRoleDto request = new ContractorToRoleDto();
        request.setContractorId(UUID.fromString("11111111-2222-3333-4444-555555555555"));
        request.setRoleId("BORROWER");
        String errorMessage = "Deal contractor with id '11111111-2222-3333-4444-555555555555' not found";

        doThrow(new DealContractorNotFondException(errorMessage))
                .when(service).addNewRole(request);

        mockMvc.perform(post("/contractor-to-role/add")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verify(service, times(1)).addNewRole(request);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testAddRoleToContractor_roleNotFound() throws Exception {
        ContractorToRoleDto request = new ContractorToRoleDto();
        request.setContractorId(UUID.fromString("11111111-2222-3333-4444-555555555555"));
        request.setRoleId("BORROWER");
        String errorMessage = "Contractor role with id 'BORROWER' not found";

        doThrow(new ContractorRoleNotFondException(errorMessage))
                .when(service).addNewRole(request);

        mockMvc.perform(post("/contractor-to-role/add")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verify(service, times(1)).addNewRole(request);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testAddRoleToContractor_serverError() throws Exception {
        ContractorToRoleDto request = new ContractorToRoleDto();
        request.setContractorId(UUID.fromString("11111111-2222-3333-4444-555555555555"));
        request.setRoleId("BORROWER");
        String errorMessage = "Unexpected server error";

        doThrow(new RuntimeException(errorMessage))
                .when(service).addNewRole(request);

        mockMvc.perform(post("/contractor-to-role/add")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verify(service, times(1)).addNewRole(request);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testDeleteRoleToContractor_serverError() throws Exception {
        ContractorToRoleDto request = new ContractorToRoleDto();
        request.setContractorId(UUID.fromString("11111111-2222-3333-4444-555555555555"));
        request.setRoleId("BORROWER");

        doThrow(new RuntimeException("Unexpected server error"))
                .when(service).deleteRole(refEq(request));

        mockMvc.perform(delete("/contractor-to-role/delete")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected server error"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verify(service, times(1)).deleteRole(request);
        verifyNoMoreInteractions(service);
    }

    @Test
    void testDeleteRoleToContractor_invalidContractorIdFormat() throws Exception {
        String invalidRequest = "{\"contractorId\": \"invalid-uuid\", \"roleId\": \"BORROWER\"}";

        mockMvc.perform(delete("/contractor-to-role/delete")
                        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid UUID format"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.timestamp").isString());

        verifyNoInteractions(service);
    }

}