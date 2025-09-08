package org.ex9.dealservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;

/**
 * Конфигурация Redis для сделок и справочной информации о сделках.
 * <p>Для сделок TTL = 30 мин., для справочников 1 час.
 * Имеет JSON-сериализацию (GenericJackson2JsonRedisSerializer)</p>
 *
 * @author Краковцев Артём
 */
@Configuration
@EnableCaching
@RequiredArgsConstructor
public class RedisConfig {

    public static final String DEALS = "deals";
    public static final String DEALS_SUB = "deal_metadata";

    private final ObjectMapper objectMapper;

    /**
     * Бин RedisCacheManager с настройками времени жизни данных и JSON-сериализацией.
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper cacheObjectMapper = objectMapper.copy();
        cacheObjectMapper.activateDefaultTyping(cacheObjectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                PROPERTY);
        var dealsSerializer = RedisSerializationContext.SerializationPair
                .fromSerializer(new GenericJackson2JsonRedisSerializer(cacheObjectMapper));

        RedisCacheConfiguration dealsConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .serializeValuesWith(dealsSerializer);

        RedisCacheConfiguration subConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .serializeValuesWith(dealsSerializer);

        Map<String, RedisCacheConfiguration> cacheCfgs = new HashMap<>();
        cacheCfgs.put(DEALS, dealsConfig);
        cacheCfgs.put(DEALS_SUB, subConfig);

        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(cacheCfgs)
                .build();
    }

}
