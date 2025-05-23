package com.techzhao.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    private String redisHost = "127.0.0.1";

    // 从配置文件中读取Redis端口信息
    private int redisPort = 6379;

    // 配置Redis连接工厂
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // 创建Redis的单机配置
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        // 返回Lettuce连接工厂
        return new LettuceConnectionFactory(config);
    }

    // 配置RedisTemplate
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        // 创建RedisTemplate实例
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        // 设置连接工厂
        template.setConnectionFactory(connectionFactory);
        // 设置默认的序列化器为GenericJackson2JsonRedisSerializer，用于序列化键和值为JSON格式
        template.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        // 设置键的序列化器为StringRedisSerializer
        template.setKeySerializer(new StringRedisSerializer());
        // 设置值的序列化器为GenericJackson2JsonRedisSerializer
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        // 返回配置好的RedisTemplate实例
        return template;
    }
}
