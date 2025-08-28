package org.ex9.dealservice.controller;

import com.redis.testcontainers.RedisContainer;
import org.ex9.dealservice.config.RedisConfig;
import org.ex9.dealservice.dto.DealTypeDto;
import org.ex9.dealservice.model.DealStatus;
import org.ex9.dealservice.model.DealType;
import org.ex9.dealservice.repository.DealStatusRepository;
import org.ex9.dealservice.repository.DealTypeRepository;
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

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class DealTypeControllerTest {

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
    DealTypeRepository dealTypeRepository;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        DealTypeRepository mockDealTypeRepository() {
            return Mockito.mock(DealTypeRepository.class);
        }
    }

    @BeforeEach
    void setup() {
        cacheManager.getCache(RedisConfig.DEALS_SUB).clear();

        reset(dealTypeRepository);

        DealType entity1 = DealType.builder()
                .id("CREDIT")
                .name("Кредит")
                .isActive(true)
                .build();
        DealType entity2 = DealType.builder()
                .id("OVERDRAFT")
                .name("Овердрафт")
                .isActive(true)
                .build();

        when(dealTypeRepository.findAllByIsActiveTrue())
                .thenReturn(List.of(entity1, entity2));
        when(dealTypeRepository.save(entity1))
                .thenReturn(entity1);
    }

    @Test
    void getAll_shouldUseRedisCache_betweenCalls() throws Exception {
        mockMvc.perform(get("/deal-type/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(get("/deal-type/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(dealTypeRepository, times(1)).findAllByIsActiveTrue();
    }

    @Test
    void save_shouldUpdateRedisCache() throws Exception {
        mockMvc.perform(get("/deal-type/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        var dto = DealTypeDto.builder()
                        .id("CREDIT")
                        .name("Кредит")
                        .build();
        var request = new ObjectMapper().writeValueAsString(dto);
        var response = mockMvc.perform(put("/deal-type/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(request)
                );
        response.andExpect(status().isOk());

        mockMvc.perform(get("/deal-type/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(dealTypeRepository, times(2)).findAllByIsActiveTrue();
    }

}