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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.redis.redis.models.Product;
import com.redis.redis.services.PubSubService;

@Configuration
@EnableCaching
public class RedisConfig {


        @Bean
        public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory connectionFactory){
                RedisTemplate<String,Object> template = new RedisTemplate<>();
                template.setConnectionFactory(connectionFactory);
                template.setKeySerializer(new StringRedisSerializer());
                template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
                return template;
        }


//ttl padrão 30 segundos
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

    //pub/sub setup
    @Bean
    public ChannelTopic topic(){
        return new ChannelTopic("product-updates");
    }

    @Bean
    public MessageListenerAdapter messageListener(PubSubService subscriber){
        return new MessageListenerAdapter(subscriber,"onMessage");
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
    MessageListenerAdapter listenerAdapter,ChannelTopic topic){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, topic);
        return container;
    }

}
