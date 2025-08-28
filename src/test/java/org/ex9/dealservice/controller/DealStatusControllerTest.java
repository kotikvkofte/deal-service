package org.ex9.dealservice.controller;

import com.redis.testcontainers.RedisContainer;
import org.ex9.dealservice.config.RedisConfig;
import org.ex9.dealservice.mapper.DealMapper;
import org.ex9.dealservice.model.DealStatus;
import org.ex9.dealservice.repository.DealStatusRepository;
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

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class DealStatusControllerTest {

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
    DealStatusRepository dealStatusRepository;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        DealStatusRepository mockDealStatusRepository() {
            return Mockito.mock(DealStatusRepository.class);
        }
    }

    @BeforeEach
    void setup() {
        cacheManager.getCache(RedisConfig.DEALS_SUB).clear();

        reset(dealStatusRepository);

        DealStatus entity1 = DealStatus.builder()
                .id("ACTIVE")
                .name("Утвержденная")
                .isActive(true)
                .build();
        DealStatus entity2 = DealStatus.builder()
                .id("DRAFT")
                .name("Черновик")
                .isActive(true)
                .build();

        when(dealStatusRepository.findAllByIsActiveIsTrue())
                .thenReturn(List.of(entity1, entity2));
    }

    @Test
    void getAll_shouldUseRedisCache_betweenCalls() throws Exception {
        mockMvc.perform(get("/deal-status/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(get("/deal-status/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(dealStatusRepository, times(1)).findAllByIsActiveIsTrue();
    }

    @Test
    void afterCacheClear_shouldCallRepositoryAgain() throws Exception {
        mockMvc.perform(get("/deal-status/all")).andExpect(status().isOk());
        verify(dealStatusRepository, times(1)).findAllByIsActiveIsTrue();

        cacheManager.getCache(RedisConfig.DEALS_SUB).clear();

        mockMvc.perform(get("/deal-status/all")).andExpect(status().isOk());
        verify(dealStatusRepository, times(2)).findAllByIsActiveIsTrue();
    }

}