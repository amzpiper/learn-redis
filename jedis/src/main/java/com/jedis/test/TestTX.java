package com.jedis.test;

import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

public class TestTX {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", "kuangshen");
        jsonObject.put("pass", "admin");

        // 开启事务
        Transaction multi = jedis.multi();
        String result = jsonObject.toJSONString();
        jedis.watch("user1");
        try {
            multi.set("user1", result);
            multi.set("user2", result);
            multi.exec();
        } catch (Exception e) {
            // 放弃事务
            multi.discard();
            e.printStackTrace();
        } finally {
            System.out.println(jedis.get("user1"));
            System.out.println(jedis.get("user2"));

            // 关闭连接
            jedis.close();
        }

    }
}
