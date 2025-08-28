package org.ex9.dealservice.controller;

import com.redis.testcontainers.RedisContainer;
import org.ex9.dealservice.config.RedisConfig;
import org.ex9.dealservice.dto.DealChangeStatusDto;
import org.ex9.dealservice.dto.DealResponseDto;
import org.ex9.dealservice.dto.DealSaveRequestDto;
import org.ex9.dealservice.dto.DealSumDto;
import org.ex9.dealservice.mapper.DealMapper;
import org.ex9.dealservice.model.Deal;
import org.ex9.dealservice.model.DealStatus;
import org.ex9.dealservice.repository.DealRepository;
import org.ex9.dealservice.repository.DealStatusRepository;
import org.ex9.dealservice.repository.DealSumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class DealControllerRedisTest {

    @Container
    static final RedisContainer REDIS = new RedisContainer("redis:latest");

    @DynamicPropertySource
    static void redisProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", REDIS::getFirstMappedPort);
    }

    @Autowired
    MockMvc mockMvc;
    @Autowired
    CacheManager cacheManager;
    @Autowired
    DealRepository dealRepository;
    @Autowired
    DealStatusRepository dealStatusRepository;
    @Autowired
    DealSumRepository dealSumRepository;
    @Autowired
    DealMapper dealMapper;
    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        DealRepository mockDealRepository() {
            return Mockito.mock(DealRepository.class);
        }
        @Bean
        @Primary
        DealStatusRepository mockDealStatusRepository() {
            return Mockito.mock(DealStatusRepository.class);
        }
        @Bean
        @Primary
        DealMapper mockDealMapper() {
            return Mockito.mock(DealMapper.class);
        }
        @Bean
        @Primary
        DealSumRepository mockDealSumRepository() {
            return Mockito.mock(DealSumRepository.class);
        }
    }

    UUID id = UUID.randomUUID();

    @BeforeEach
    void setup() {
        cacheManager.getCache(RedisConfig.DEALS).clear();

        reset(dealRepository);
        Deal deal = Deal.builder()
                .id(id)
                .build();
        DealResponseDto dto = DealResponseDto.builder()
                .id(id)
                .build();
        when(dealRepository.findByIdAndIsActiveTrue(any()))
                .thenReturn(Optional.of(deal));
        when(dealMapper.toDealResponseDto(any())).thenReturn(dto);
    }

    @Test
    void getById_shouldUseRedisCache_betweenCalls() throws Exception {

        mockMvc.perform(get("/deal/deal/"+id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/deal/deal/"+id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(dealRepository, times(1)).findByIdAndIsActiveTrue(id);
    }

    @Test
    void getById_shouldUpdateRedisCache_whenUpdateStatus() throws Exception {

        DealStatus status = new DealStatus("ACTIVE", "Утвержденная", true);
        when(dealStatusRepository.findByIdAndIsActiveIsTrue(any())).thenReturn(Optional.of(status));
        Deal deal = Deal.builder()
                .id(id)
                .build();
        when(dealRepository.save(any())).thenReturn(deal);

        mockMvc.perform(get("/deal/deal/"+id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        DealChangeStatusDto dto = new DealChangeStatusDto(id, "ACTIVE");

        mockMvc.perform(patch("/deal/change/status")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                )
                .andExpect(status().isOk());

        mockMvc.perform(get("/deal/deal/"+id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));



        verify(dealRepository, times(3)).findByIdAndIsActiveTrue(id);
    }

    @Test
    void getById_shouldUpdateRedisCache_whenSaveDeal() throws Exception {
        Deal deal = Deal.builder()
                .id(id)
                .build();
        when(dealRepository.save(any())).thenReturn(deal);
        when(dealRepository.findById(any())).thenReturn(Optional.of(deal));
        when(dealSumRepository.save(any())).thenReturn(null);
        mockMvc.perform(get("/deal/deal/"+id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        DealSaveRequestDto dto = DealSaveRequestDto.builder()
                .id(id)
                .description("test")
                .sum(new DealSumDto("123.00", "test"))

                .build();

        mockMvc.perform(put("/deal/save")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(dto))
                )
                .andExpect(status().isOk());

        mockMvc.perform(get("/deal/deal/"+id).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));



        verify(dealRepository, times(2)).findByIdAndIsActiveTrue(id);
    }

}
