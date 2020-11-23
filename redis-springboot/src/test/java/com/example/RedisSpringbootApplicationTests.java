package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

@SpringBootTest
class RedisSpringbootApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        // redis操作字符串的，类似String
        redisTemplate.opsForValue().set("name","yuhang");
        System.out.println(redisTemplate.opsForValue().get("name"));
        // redis操作字符串的，List
        redisTemplate.opsForList().leftPush("name","yuhang");

        // redis操作字符串的，Set
        redisTemplate.opsForSet();

        // redis操作字符串的，Hash
        redisTemplate.opsForHash();

        // redis操作字符串的，Geo
        redisTemplate.opsForGeo();

        // redis操作字符串的，ZSet
        redisTemplate.opsForZSet();

        // redis操作字符串的，HyperLogLog
        redisTemplate.opsForHyperLogLog();

        // redis操作字符串的，Bitmap
        redisTemplate.opsForCluster();

        // 常用的操作,直接用redisTemplate
        // 比如事务和基本的CRUD
        redisTemplate.multi();
        redisTemplate.watch("");
        redisTemplate.exec();
        redisTemplate.delete("");
        redisTemplate.expire("", Duration.ZERO);
        redisTemplate.move("", 1);
        // 获取链接,操作数据方法
        RedisConnection redisConnection = redisTemplate.getConnectionFactory().getConnection();
        redisConnection.flushAll();
        redisConnection.flushDb();
        redisConnection.close();

    }

}
