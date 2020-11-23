package com.jedis.test;

import redis.clients.jedis.Jedis;

public class TestKey {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);

        System.out.println("清空数据"+jedis.flushDB());
        System.out.println("判断某个键是否存在"+jedis.exists("username"));
        System.out.println("新增username"+jedis.set("username","admin"));
        System.out.println("新增password"+jedis.set("password","admin"));
        System.out.println("查看所有的键"+jedis.keys("*"));
        System.out.println("删除password"+jedis.del("password"));
        System.out.println("查看username类型"+jedis.type("username"));
        System.out.println("随即返回key空间的一个"+jedis.randomKey());
        System.out.println("重命名key"+jedis.rename("username","uname"));
        System.out.println("取出后改name"+jedis.get("uname"));
        System.out.println("按索引查询"+jedis.select(0));
        System.out.println("删除当前选择数据库中的所有key"+jedis.flushDB());
        System.out.println("返回当前数据库中key的数目"+jedis.dbSize());
        System.out.println("删除数据库中的所有key"+jedis.flushAll());
    }
}
