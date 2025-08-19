package com.example.Book.Catalog.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.Map;

@Configuration
public class CacheConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(){
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisCacheManager cacheManager(LettuceConnectionFactory connectionFactory){

        //configures default time to live , so that entries expire automatically
        //stores values as JSON
        RedisCacheConfiguration base = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith( //serialize the values
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer()) // using jackson JSON for values
                )
                .entryTtl(Duration.ofMinutes(5)); //default TTL for caches : 5 minutes

        Map<String, RedisCacheConfiguration> perCache = Map.of(
                "bookById",base.entryTtl(Duration.ofMinutes(10)),// by id reads changes less often
                "bookSearch",base.entryTtl(Duration.ofMinutes(2)) // stales search results faster
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(base) // apply base to all caches by default
                .withInitialCacheConfigurations(perCache) // apply per cache overrides where defined
                .build();


    }
}


