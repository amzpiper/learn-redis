package com.jedis.test;

import redis.clients.jedis.Jedis;

public class TestPing {
    public static void main(String[] args) {
        /**
         * 1.连接数据库
         * 连接方式：看源码
         * 注意防火墙
         */
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        // jedis 的所有命令就是我们之前学习的所有指令

        // ping Pong
        System.out.println(jedis.ping());

        /**
         * 常用api
         * string
         * list
         * set
         * hash
         * zset
         * geospatial
         * hyperloglog
         * bitmap
         */
        

    }
}
