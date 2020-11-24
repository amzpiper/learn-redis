package com.example.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class RedisConfig {

    // 固定的模板
    // 编写我们自己的RedisTemplate
    @Bean
    @SuppressWarnings("all")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // 我们为了自己方便一般用String, Object类型
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // json序列化配置,转义：
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // string序列化配置
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // 配置具体的序列化方式,配置自己的序列化
        // key采用string序列化
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也用sting序列化
        template.setHashKeySerializer(stringRedisSerializer);
        // value使用json序列化
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value使用json序列化
        template.setValueSerializer(jackson2JsonRedisSerializer);

        return template;
    }
}
