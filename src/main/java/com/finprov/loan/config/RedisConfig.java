package com.finprov.loan.config;

import java.time.Duration;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@EnableCaching
public class RedisConfig {

  @Bean
  public RedisCacheConfiguration cacheConfiguration() {
    return RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofMinutes(60)) // Default TTL 60 minutes
        .disableCachingNullValues()
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer()));
  }

  @Bean
  public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer(
      RedisCacheConfiguration cacheConfiguration) {
    return (builder) ->
        builder
            .withCacheConfiguration(
                "customers",
                cacheConfiguration.entryTtl(
                    Duration.ofMinutes(1))) // Specific TTL for "customers" cache
            .withCacheConfiguration(
                "users",
                cacheConfiguration.entryTtl(
                    Duration.ofMinutes(1))) // Specific TTL for "users" cache
            .withCacheConfiguration(
                "roles",
                cacheConfiguration.entryTtl(
                    Duration.ofMinutes(1440))); // Specific TTL for "roles" cache (24 hours)
  }
}
