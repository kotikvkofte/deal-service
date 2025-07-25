package org.ex9.dealservice.controller.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ex9.dealservice.dto.ContractorToRoleDto;
import org.ex9.dealservice.exception.ContractorRoleNotFondException;
import org.ex9.dealservice.service.ContractorToRoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UiContractorToRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContractorToRoleService contractorToRoleService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class Config {
        @Bean
        public ContractorToRoleService contractorToRoleService() {
            return mock(ContractorToRoleService.class);
        }
    }

    @Test
    @WithMockUser(authorities = {"DEAL_SUPERUSER"})
    void testAddRoleToContractor_success() throws Exception {
        ContractorToRoleDto request = new ContractorToRoleDto(UUID.randomUUID(),"BORROWER");
        mockMvc.perform(post("/ui/contractor-to-role/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(contractorToRoleService).addNewRole(any(ContractorToRoleDto.class));
    }

    @Test
    @WithMockUser(authorities = {"DEAL_SUPERUSER"})
    void testAddRoleToContractor_notFound() throws Exception {
        ContractorToRoleDto request = new ContractorToRoleDto(UUID.randomUUID(),"BORROWER");

        doThrow(new ContractorRoleNotFondException("Role not found"))
                .when(contractorToRoleService).addNewRole(any());

        mockMvc.perform(post("/ui/contractor-to-role/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Role not found"));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void testAddRoleToContractor_forbidden() throws Exception {
        ContractorToRoleDto request = new ContractorToRoleDto(UUID.randomUUID(),"BORROWER");

        mockMvc.perform(post("/ui/contractor-to-role/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testAddRoleToContractor_unauthorized() throws Exception {
        ContractorToRoleDto request = new ContractorToRoleDto(UUID.randomUUID(),"BORROWER");

        mockMvc.perform(post("/ui/contractor-to-role/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"DEAL_SUPERUSER"})
    void testDeleteRoleToContractor_success() throws Exception {
        ContractorToRoleDto request = new ContractorToRoleDto(UUID.randomUUID(),"BORROWER");

        mockMvc.perform(delete("/ui/contractor-to-role/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(contractorToRoleService).deleteRole(any(ContractorToRoleDto.class));
    }

    @Test
    @WithMockUser(authorities = {"DEAL_SUPERUSER"})
    void testDeleteRoleToContractor_notFound() throws Exception {
        ContractorToRoleDto request = new ContractorToRoleDto(UUID.randomUUID(),"BORROWER");

        doThrow(new ContractorRoleNotFondException("Role not found"))
                .when(contractorToRoleService).deleteRole(any());

        mockMvc.perform(delete("/ui/contractor-to-role/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Role not found"));
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void testDeleteRoleToContractor_forbidden() throws Exception {
        ContractorToRoleDto request = new ContractorToRoleDto(UUID.randomUUID(),"BORROWER");

        mockMvc.perform(delete("/ui/contractor-to-role/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteRoleToContractor_unauthorized() throws Exception {
        ContractorToRoleDto request = new ContractorToRoleDto(UUID.randomUUID(),"BORROWER");

        mockMvc.perform(delete("/ui/contractor-to-role/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

}