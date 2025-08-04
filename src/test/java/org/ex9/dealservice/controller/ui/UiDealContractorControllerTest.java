package org.ex9.dealservice.controller.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ex9.dealservice.dto.DealContractorSaveRequestDto;
import org.ex9.dealservice.service.DealContractorService;
import org.junit.jupiter.api.Test;
import org.ex9.dealservice.exception.DealContractorNotFondException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UiDealContractorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DealContractorService dealContractorService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class Config {
        @Bean
        public DealContractorService dealContractorService() {
            return mock(DealContractorService.class);
        }
    }

    @Test
    @WithMockUser(username = "test", authorities = {"SUPERUSER"})
    void testSaveContractor_success() throws Exception {
        UUID savedId = UUID.randomUUID();
        DealContractorSaveRequestDto request = DealContractorSaveRequestDto.builder()
                .dealId(UUID.randomUUID())
                .contractorId("TST")
                .build();
        when(dealContractorService.saveDealContractor(any(DealContractorSaveRequestDto.class), eq("test"))).thenReturn(savedId);

        mockMvc.perform(put("/ui/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void testSaveContractor_forbidden() throws Exception {
        DealContractorSaveRequestDto request = DealContractorSaveRequestDto.builder()
                .dealId(UUID.randomUUID())
                .contractorId("TST")
                .build();

        mockMvc.perform(put("/ui/deal-contractor/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"DEAL_SUPERUSER"})
    void testDeleteContractor_success() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(dealContractorService).deleteDealContractor(id);

        mockMvc.perform(delete("/ui/deal-contractor/delete/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(authorities = {"DEAL_SUPERUSER"})
    void testDeleteContractor_notFound() throws Exception {
        UUID id = UUID.randomUUID();

        doThrow(new DealContractorNotFondException("Deal contractor not found"))
                .when(dealContractorService).deleteDealContractor(id);

        mockMvc.perform(delete("/ui/deal-contractor/delete/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Deal contractor not found"));
    }

    @Test
    void testDeleteContractor_unauthenticated() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/ui/deal-contractor/delete/" + id))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void testDeleteContractor_forbidden() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/ui/deal-contractor/delete/" + id))
                .andExpect(status().isForbidden());
    }

}