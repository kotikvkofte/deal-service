package org.ex9.dealservice.controller.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ex9.dealservice.dto.DealChangeStatusDto;
import org.ex9.dealservice.dto.DealResponseDto;
import org.ex9.dealservice.dto.DealSaveRequestDto;
import org.ex9.dealservice.dto.DealSearchRequestDto;
import org.ex9.dealservice.service.DealService;
import org.ex9.dealservice.exception.DealNotFondException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UiDealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DealService dealService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public DealService dealService() {
            return Mockito.mock(DealService.class);
        }
    }

    @Test
    @WithMockUser(authorities = {"SUPERUSER"})
    void testGetDealById_success() throws Exception {
        UUID id = UUID.randomUUID();
        DealResponseDto dto = new DealResponseDto();
        dto.setId(id);

        when(dealService.getDealById(id)).thenReturn(dto);

        mockMvc.perform(get("/ui/deal/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    @WithMockUser(authorities = {"SUPERUSER"})
    void testGetDealById_notFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(dealService.getDealById(id)).thenThrow(new DealNotFondException("Deal not found"));

        mockMvc.perform(get("/ui/deal/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Deal not found"));
    }

    @Test
    void testGetDealById_unauthorized() throws Exception {
        mockMvc.perform(get("/ui/deal/" + UUID.randomUUID()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"SUPERUSER"})
    void testSaveDeal_success() throws Exception {
        DealSaveRequestDto requestDto = new DealSaveRequestDto();
        requestDto.setDescription("Test Deal");
        UUID dealId = UUID.randomUUID();

        when(dealService.dealSave(any(DealSaveRequestDto.class), eq("admin"))).thenReturn(dealId);

        mockMvc.perform(put("/ui/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void testSaveDeal_forbidden() throws Exception {
        DealSaveRequestDto requestDto = new DealSaveRequestDto();
        requestDto.setDescription("Test");


        mockMvc.perform(put("/ui/deal/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"DEAL_SUPERUSER"})
    void testChangeStatus_success() throws Exception {
        DealChangeStatusDto statusDto = new DealChangeStatusDto();
        statusDto.setDealId(UUID.randomUUID());
        statusDto.setStatusId("APPROVED");

        doNothing().when(dealService).changeStatus(statusDto);

        mockMvc.perform(patch("/ui/deal/change/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void testChangeStatus_forbidden() throws Exception {
        DealChangeStatusDto statusDto = new DealChangeStatusDto();
        statusDto.setDealId(UUID.randomUUID());
        statusDto.setStatusId("APPROVED");

        mockMvc.perform(patch("/ui/deal/change/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(authorities = {"CREDIT_USER"})
    void testSearchDeals_success() throws Exception {
        DealSearchRequestDto searchRequest = new DealSearchRequestDto();
        searchRequest.setTypeIds(List.of("CREDIT"));
        searchRequest.setPage(0);
        searchRequest.setSize(10);

        Page<DealResponseDto> responsePage = Page.empty();
        when(dealService.searchDeals(any())).thenReturn(responsePage);

        mockMvc.perform(post("/ui/deal/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(authorities = {"USER"})
    void testSearchDeals_forbidden() throws Exception {
        DealSearchRequestDto searchRequest = new DealSearchRequestDto();
        searchRequest.setTypeIds(List.of("CREDIT"));
        searchRequest.setPage(0);
        searchRequest.setSize(10);

        mockMvc.perform(post("/ui/deal/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isForbidden());
    }

}