package com.shashank.cart_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(); // uses spring.redis.* properties
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory conn) {
        RedisTemplate<String, Object> rt = new RedisTemplate<>();
        rt.setConnectionFactory(conn);

        GenericJackson2JsonRedisSerializer jackson = new GenericJackson2JsonRedisSerializer();
        rt.setKeySerializer(new StringRedisSerializer());
        rt.setValueSerializer(jackson);
        rt.setHashKeySerializer(new StringRedisSerializer());
        rt.setHashValueSerializer(jackson);

        rt.afterPropertiesSet();
        return rt;
    }
}
