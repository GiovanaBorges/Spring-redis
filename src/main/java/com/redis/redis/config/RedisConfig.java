package com.redis.redis.config;

import java.time.Duration;
import java.util.List;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.redis.redis.models.Product;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        ObjectMapper objectMapper = new ObjectMapper();
        CollectionType listType = objectMapper
                .getTypeFactory()
                .constructCollectionType(List.class, Product.class);

        Jackson2JsonRedisSerializer<List<Product>> valueSerializer =
                new Jackson2JsonRedisSerializer<>(listType);

        StringRedisSerializer keySerializer = new StringRedisSerializer();

        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(1))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer));

        RedisCacheConfiguration productsCacheConfig = defaultCacheConfig
                .entryTtl(Duration.ofSeconds(30));

        return RedisCacheManager.builder(connectionFactory)
                .withCacheConfiguration("products", productsCacheConfig)
                .cacheDefaults(defaultCacheConfig)
                .build();
    }
}
