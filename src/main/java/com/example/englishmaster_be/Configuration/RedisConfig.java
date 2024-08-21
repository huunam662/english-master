package com.example.englishmaster_be.Configuration;

import com.example.englishmaster_be.Model.Response.Otp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
@Configuration
public class RedisConfig {
    @Value("localhost")
    private String redisHost;
    @Value("6380")
    private String redisPort;

    @Bean
    public RedisConnectionFactory redisConnectionFactory(){
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setPort(Integer.parseInt(redisPort));
        factory.setHostName(redisHost);
        return factory;
    }
    @Bean
    public RedisTemplate<String,Otp> redisTemplate(){
        RedisTemplate<String, Otp> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
