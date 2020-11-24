package com.example.config;

import com.example.pojo.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;

public class RedisConfig {

    // 编写我们自己的RedisTemplate
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // 配置具体的序列化方式,配置自己的序列化
        // 例子jackson
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<Object>(new User());
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }
}
