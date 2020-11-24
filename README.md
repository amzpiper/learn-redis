# learn-redis
>Redis 是一个开源（BSD许可）的，内存中的数据结构存储系统，它可以用作数据库、缓存和消息中间件。 它支持多种类型的数据结构，如 字符串（strings）， 散列（hashes）， 列表（lists）， 集合（sets）， 有序集合（sorted sets） 与范围查询， bitmaps， hyperloglogs 和 地理空间（geospatial） 索引半径查询。 Redis 内置了 复制（replication），LUA脚本（Lua scripting）， LRU驱动事件（LRU eviction），事务（transactions） 和不同级别的 磁盘持久化（persistence）， 并通过 Redis哨兵（Sentinel）和自动 分区（Cluster）提供高可用性（high availability）。
# 基础
## 1.基本命令
```
ping                    # 测试联通：PONG
set name value          # 设置KEY
get name                # 获取KEY
keys *                  # 查所有KEY
select 0                # 切换数据库
flushall                # 清空所有数据库
flushdb                 # 清空所选数据库
expire name time        # 设置KEY过期时间
ttl name                # 查看剩余时间，-2为已过期
exist name              # 判断KEY是否存在
```   
## 2.五大基础类型
### 2.1.String类型
```
append                  # 追加数据，name不存在set
strlen                  # 数据长度

<!-- 微信公众号浏览量,实现i++、i-- -->
set views 0
incr views              # i++,持久化可以持久过去，暂时并不是要mysql
decr views              # i--

<!-- 实现java步长 -->
incrby views 10         # 在基础上增加10
decrby views 10         # 在基础上减少10

<!-- 截取字符串(左闭右开区间) -->
getrange name start end # 获得截取字符串
getrange name 0 3       
getrange name 0 -1      # 查看全部数据 

<!-- 替换数据 -->
setrange name 1 xx      # 0 -1，替换指定位置开始的字符串

setex key seconds value # 如果存在,再设置过期时间
setex key3 30 "hello"
setnx key seconds value # 如果不存在,创建设置过期时间(分布式锁常常用)，如果已存在创建失败
setnx name "mongoDB"    # 不存在再设置值，1成功，0失败
mset k1 v1 k2 v2 k3 v3  # 设置多个
mget k1 k2 k3           # 获取多个
msetnx                  # 不存在设置成功，存在设置失败（1个存在都失败，原子性）
mget 

<!-- 设置user对象 -->
<!-- value为json来保存1个对象 -->
set user:1 {name:zhangsan,age:3}
mset user:1:name zhangsan user:1:age 2  # mset user:{id}:{field}
mget user:1name user:1:age
set article:1000:views  #设置当前文章浏览量，key可以复用

<!-- 组合命令：如果不存再返回nil,如果存在返回旧值，并设置新值 -->
getset db redis         # 先get再set
getset db mongoDB       # 得出来redis,再get db 为mongoDB 
```
>String使用场景：value可以是int、string，计数器、统计多单位数量、粉丝数、对象储存
```
set uid:9555:follow 0 incr
```

### 2.2.List类型
>基本数据类型，增加点规则玩成：栈、队列
>所有List命令都是l开头
```
lpush list one  # 将1个或多个插入到列表的头部
lpush list two
lrange list 0 -1
rpush list one  # 将1个或多个插入到列表的底部
lpop   # 去除左边的，头部
rpop   # 去除右边的，底部
Lindex list index  # 获取下表数值0
Llen list   # 列表长度
lrem list 1 one  # 移除list中的one且1个
lrem list 2 one  # 移除list中的one且2个
ltrim list 1 2  # 通过下表截取指定长度，list被改变，只剩下截取的元素
rpoplpush source des # 移除列表的最后一个元素，且移动到新list
lset list 0 item  # 指定下标添加时，list不存在就报错
lpush list item
lrange list 0 0
lset list 0 newItem  # 若存在，把0号元素更新掉
lset list 1 newItem  # 不存在的下标就把报错 
linsert key befor|after pivot value # list前或后
rpush list hello
rpush list world
linsert list before world other # 在world上面插入other
linsert list after world other # 在world下面插入other
```
>List实际是1个链条，key不存在创建新链表，key存在新增内容
>空链表也代表不存在，两边改动效率 最高
>Lpush Rpop：队列
>Lpush Lpop：栈

### 2.3.Set(集合)
>set中的值不能重复
```
sadd myset hello        # 在myset中添加hello
sadd myset kangshen  
smembers myset          # 查看set所有制
sismember myset hello   # 判断元素是否存在
scard myset             # 查询set个数
srem myset hello        # 移除hello
srandmember myset       # 随机读取1个
srandmember myset 2     # 随机读取指定个数
spop myset              # 随机移除1个
smove myset myset2 "ku" # 移动ku到myset2
<!-- 例子：共同关注：交集 -->
sdiff key1 key2         # 差集，显示不同的
sinter key1 key2        # 交集:共同好友
sunion key1 key2        # 并集
<!-- 例子：微博A用户所有关注的人放在SET集合中 -->
<!-- 共同关注,共同爱好，六度理论 -->
```
### 2.4.Hash~Map
>Map集合，key-value,村的也是键值对，但是value是个map集合
```
hset key field value    # 添加key-value到Hash
hget key field          # 获取keyHash中的field
hmset myhash k1 v1 k2 v2# 插入多个值
hmget myhash k1 k2      # 获取多个值
hgetall myhash          # 获取所有的值
hdel key field          # 删除field字段
hlen key                # 获取hash表字段数量
hexists myhash field    # 判断hash指定字段是否存在
# 只获得所有field
hkeys myhash
# 后的所有的值
kvals myhash
# 指定自增 i++ / i--
hincrby myhash field3 1 
hdecr myhash field 3 1 
hsetnx myhash field4 hello  #不存在会创建，存在就不创建
<!-- 例子:hash变更数据,设置对象 -->
hset user:1 name qinjing
hget user:1 name
<!-- hash更适合存储对象 -->
```
### 2.5.Zset(有序集合)
>在set的基础上，增加1个值，set k1 v1 / zset k1 score1 v1
```
zadd myzset 1 one           # 添加1个值
zadd myzset 2 two 3 three   # 添加多个值
zrange myset 0 -1           # 查询所有值
<!-- 排序 -->
zadd salary 2500 xiaohong 5000 zhangsan 500 kuangshen
zrangebyscore salary -inf +inf              # 从小到大
1) "kuangshen"
2) "xiaohong" 
3) "zhangsan"
zrangebyscore salary -inf +inf withscores   # 从小到大，并输出score
zrem salary xiaohong        # 移除小红
zcard salary                # 获取个数
zrevrange salary 0 -1       # 从大到小:反转
zcount salary key min max   # 判断区间个数(闭区间)
```
>案例思路：set->排序、班级成绩表、工资表、带权重的消息、排行榜(有序集合zset中)

## 3.三种特殊数据类型
### 3.1.geospatial地理位置
>朋友的定位，附近的人，打车距离怎么算？城市经度纬度，redis3.2版本的geo就退出了。
只有6个命令：GEOADD、GEODIST、GEOHASH、GEOPOS、GEORADIUS、GEORADIUSBYMEMBER
```
geoadd                      #添加地理位置
geoadd china:city 116.40 39.90 beijing
geoadd china:city 121.47 31.23 shanghai
geoadd china:city 160.50 29.53 chongqing 114.05 22.52 shenzhen
# 规则：两级无法直接添加，我们一般会下载城市数据，用JAVA一次导入
# 参数：key 维度 精度 名称
geopos key name             # 获取精度和纬度
# geodist 单位，两地之间距离：
# m 米
# km 千米
# mi 盈利
# ft 英尺
geodist key beijin shanghai km # 两地之间距离直线，单位千米
<!-- 例子：我附近的人,找朋友： -->
<!-- georedius:以给定的经纬度为中心，找出半径内的元素 -->
# 1、记录所有人的经纬度
# 2、获得所有附近的人的地址，定位最近的老师！
# 以经纬度为精心，显示城市到我某距离内的指定人数
georadius key logitude latitude radius m|km|ft|mi withdist withcoord count num
georadius china:city 110 120 1000 km withdist withcoord count 3
# 以经纬度为精心，显示城市到我有多远:距离
georadius china:city 110 120 1000 km withdist
# 以经纬度为精心，显示城市到我某距离内所有人到我的经纬度信息
georadius china:city 110 120 1000 km withcoord
# 以北京元素为中心找出距离我某距离的所有城市
<!-- 例子：导航定位，georadiusbymember：找出位于指定元素周围的其他元素 -->
georadiusbymember china:city beijing 1000 km
georadiusbymember china:city shanghai 400 km
# geohash:找到1个或多个元素的geohash表示:52点证书编码，返回11个字符的字符串，所以没有精度,还是两个元素距离：将二位经纬度转换为1维的经纬度hash字符串表示：2个字符串长得越像，越接近
geohash china:city beijing shanghai 
```
>geo实现原理：基于Zset,可以使用Zset操作geo
>所以用zset命令进行删除修改
```
zrange china:city 0 -1              # 查看所有城市
zrem china:city beijing             # 删除北京
```
### 3.2.Hyperloglog基数统计
>什么是基数？不重复的元素
>A {1,3,5,7,8,7}
>B {1,3,5,7,8}
>基数：1,3,5,7,8
>Redis 2.8.9 半根更新了Heyperloglog数据结构
>统计网页UV(1个人访问1个网站多次，但是还算1个人)
>传统方法，set保存用户id,然后用set元素数量作为统计标准
>这种方式保存大量id比较麻烦，占内存。因为目的是计数，不是存id
>Hyperloglog：
>优点:占用内存固定，2^64不同的元素，12kb内存。内存角度比较比set好
>缺点:有0.81%的错误率
```
PFadd key element [element ...]
PFadd mykey a b c d e f g       # 添加数据到第一组
PFadd mykey2 a b c d e f g      # 添加数据到第二组
PFCOUNT mykey                   # 统计mykey中元素基数的数量
PFMERGE mykey3 mykey mykey2     # 合并mykey与mykey2到mykey3：删除重复
PFCOUNT mykey3                  # 查看并集的数量
# 如果允许容错，一定要使用Hyperloglog !
# 如果不允许容错，就要使用set或者自己的数据类型 !
```
### 3.3.Bitmaps 位存储
>筛选用户：用0101最快:
>例子：1、统计疫情感染人数：0 1 0 1 0 1，2、活跃不活跃，3、是否登录，4、打卡（mysql:user: status date）这样很慢，所有两个状态都可以使用Bitmaps!
>Bitmaps位图，数据结构！都是操作魏晋至进行记录，就只有0和1两个状态。
>365天 = 365bit/8bit = 46kb
```
<!-- 使用bitmap记录周一到周日打卡！ -->
# 周一：1 周二：0 周三：1 周四：1 周五：0 周六：1 周日：1
# 统计有多少个1
setbit sign 1 0
setbit sign 2 0
setbit sign 3 0
setbit sign 4 0
setbit sign 5 0
setbit sign 6 0
setbit sign 7 0
getbit sign 1
getbit sign 2
getbit sign 3
getbit sign 4
getbit sign 5
getbit sign 6
getbit sign 7
# 统计打卡天数
bitcount sign [start end]
bitcount sign                       # 统计打卡天数，默认所有
```
## 4.Redis的基本事务操作
>redis单条命令保存原子性，但是redis事务不保证原子性，没有隔离集合概念！
>Redis事务本质：一组命令的集合！一个手游命令被序列化，进行顺序执行！
>一次性、顺序性、排他性！这样执行一系列命令
>redis的事务：
```
# 正常执行事务：
1、开启事务(multi)
2、命令入队(...)
>set k1 v1
>set k2 v2
>get k2
3、执行事务(exec)
```
>执行完后都要重新开启新事务
```
# 放弃执行事务：
1、开启事务(multi)
2、命令入队(...)
>set k1 v1
>set k2 v2
>get k2
3、取消事务(discard)
```
>执行错误:
>1、编译型异常(代码问题~命令问题):事务所有命令不会执行.
>multi
>set k1 v1
>setget k1 v1
>exec
>EXECABORT Transaction discarded because of previous errors.
>2、运行异常(1/0):若事务命令存在语法错误，其他命令可以正常执行，错误命令抛出异常. 
>multi
>incr k1
>set k1 v1
>exec
>ERR value is not an integer or out of range
## 5.监控！Watch：Redis实现乐观锁、悲观锁
```
# 悲观锁：
· 很悲观，认为什么时候都出问题，所以什么时候都会加锁！
# 乐观锁：
· 很乐观，认为什么时候都不会出现问题，什么时候都不上锁，更新时候会判断一下，此期间是否有人修改过这个数据
· 获取version
· 更新时候比较version
· 常用，效率高
```
>Redis监视测试
```
# 正常执行：成功
multi                       # 开启事务
set money 100               # 钱包100元
get out 0                   # 记录支出0
watch money                 # 监视money钱包
decrby money 20             # 钱包花去20
incrby out 20               # 支出增加20
exec                        # 执行事务
```
>watch相当于乐观锁
```
# 多个窗口执行时：
1:watch money                 # 线程1：监视钱包
1:multi                       # 线程1：开启事务
1:decrby money 20             # 线程1：钱包花去20
1:incrby out 20               # 线程1：支出增加20
2:get money                   # 线程2：查看钱包
2:set money 1000              # 线程2：修改钱包
1:exec                        # 线程1：执行事务时，前边的watch会提醒当前事务一定执行失败，因为线程2修改了我们的值，执行失败
>nil                          # 修改失败
1:unwatch                     # 事务执行失败先解锁，放弃监视
1:watch monet                 # 再监视获取最新的值，执行别的操作
1:exec                        # 执行事务时会比对监视的值是否发生变化
```
>分布式秒杀乐观锁
## 6.Jedis
>我们要使用JAVA来操作Redis
>什么是Jedis?是Redis官方推荐的java连接工具！使用Java操作Redis中间件！如果你要是用java操作redis，那么一定要对jedis十分熟悉.
1、导入依赖
```
<!-- https://mvnrepository.com/artifact/redis.clients/jedis -->
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>3.2.0</version>
</dependency>

<!-- https://mvnrepository.com/artifact/com.alibaba/fastjson -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.62</version>
</dependency>
```
2、编码测试
2.1.连接数据库
2.2.操作命令
2.3.断开连接
## 7.SpringBoot整合
>springboot操作数据：springdata封装了 jpa jdbc mongdb redis，springdata也是和springboot齐名的项目
>说明：在springboot2.x之后，原来的jedis替换为lettuce!
>jedis:采用直连：多线程不安全，避免不安全，得用jedis pool连接池！更像BIO模式
>lettuce:采用netty异步请求，实例可以在多个线程中共享，不存在线程不安全的情况！更像NIO模式

```
源码分析：
/*
 * Copyright 2012-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.data.redis;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring Data's Redis support.
 *
 * @author Dave Syer
 * @author Andy Wilkinson
 * @author Christian Dupuis
 * @author Christoph Strobl
 * @author Phillip Webb
 * @author Eddú Meléndez
 * @author Stephane Nicoll
 * @author Marco Aust
 * @author Mark Paluch
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedisOperations.class)
@EnableConfigurationProperties(RedisProperties.class)
@Import({ LettuceConnectionConfiguration.class, JedisConnectionConfiguration.class })
public class RedisAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(name = "redisTemplate")
	@ConditionalOnSingleCandidate(RedisConnectionFactory.class)
	public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		// 默认的RedisTemplate没有过多的设置，redis对象都是需要序列化！Dubbo
		// 两个反省都是object,object类型，后续使用都要强转
		// 我们可以自己定义一个RedisTemplate
		RedisTemplate<Object, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnSingleCandidate(RedisConnectionFactory.class)
	//由于String是Redis中最常用的类型,所以我们要单独提出来一个bean！
	public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
		StringRedisTemplate template = new StringRedisTemplate();
		template.setConnectionFactory(redisConnectionFactory);
		return template;
	}

}

```
>整合测试一下
```
1.导入配置
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

2.配置连接
# Springboot 所有得配置类都有一个自动配置类 RedisAutoConfiguration
# 自动配置类都会绑定一个properties配置文件  RedisProperties
spring.redis.host=127.0.0.1
spring.redis.port=6379
# spring.redis.database=0
# spring.redis.lettuce.pool.max-active=

3.测试！
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
```
```
\\ 编写自己的序列化配置
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
```
>所有的redis操作，对于开发人员来说，重要的是要理解redis的思想和每一种数据结构的用处与作用场景
# 高级
## 8.Redis.conf详解
>启动时候通过配置文件启动的！
```
# 单位:units大小写不敏感
# Redis configuration file example.
#
# Note that in order to read the configuration file, Redis must be
# started with the file path as first argument:
#
# ./redis-server /path/to/redis.conf

# Note on units: when memory size is needed, it is possible to specify
# it in the usual form of 1k 5GB 4M and so forth:
#
# 1k => 1000 bytes
# 1kb => 1024 bytes
# 1m => 1000000 bytes
# 1mb => 1024*1024 bytes
# 1g => 1000000000 bytes
# 1gb => 1024*1024*1024 bytes
#
# units are case insensitive so 1GB 1Gb 1gB are all the same.
```
```
# 包含文件
################################## INCLUDES ###################################

# Include one or more other config files here.  This is useful if you
# have a standard template that goes to all Redis servers but also need
# to customize a few per-server settings.  Include files can include
# other files, so use this wisely.
#
# Notice option "include" won't be rewritten by command "CONFIG REWRITE"
# from admin or Redis Sentinel. Since Redis always uses the last processed
# line as value of a configuration directive, you'd better put includes
# at the beginning of this file to avoid overwriting config change at runtime.
#
# If instead you are interested in using includes to override configuration
# options, it is better to use include as the last line.
# 导入文件
# include /path/to/local.conf
# include /path/to/other.conf

################################## MODULES #####################################
```
```
# 网络
bind 127.0.0.1      #绑定ip
protected-mode yes  # 保护模式
port 6379           # 端口设置
```
```
# 通用配置
daemonize yes       # 以守护的方式进行，默认no不开启
pidfile /var/run/redis_6379.pid             #如果以后太方式运行需要文件
```
```
# 日志
# Specify the server verbosity level.
# This can be one of:
# debug (a lot of information, useful for development/testing)              # 开发阶段
# verbose (many rarely useful info, but not a mess like the debug level)    # debug
# notice (moderately verbose, what you want in production probably)         # 生产环境
# warning (only very important / critical messages are logged)              # 警告
loglevel notice

logfile ""          # 日志文件位置名，
database 16         # 数据库数量，默认16
always-show-logo yes# 是否总是显示露沟
```
```
# 快照：持久化时，在规定时间内执行了多少次操作会持久化生成文件(.rdb/aof)
# 若不持久化就会丢
# 持久化规则,我们以后自己定义
save 900 1          # 如果1个key进行了修改在900s内就进行持久化操作
save 300 10         # 如果10个key进行了修改在300s内就进行持久化操作
save 60 10000       # 如果10000个key进行了修改在60s内就进行持久化操作，高并发

stop-writes-on-bgsave-error yes # 持久化出错是否还继续持久化操作

rdbcompression yes              # 是否压缩rdb文件，耗cpu资源
rdbchecksum yes                 # 保存时是否检查rdb文件校验并修复
dir ./                          # rdb保存目录
```
```
# REPLICATION 主从复制时再看，多个Redis
```
```

################################## SECURITY ###################################

# Require clients to issue AUTH <PASSWORD> before processing any other
# commands.  This might be useful in environments in which you do not trust
# others with access to the host running redis-server.
#
# This should stay commented out for backward compatibility and because most
# people do not need auth (e.g. they run their own servers).
#
# Warning: since Redis is pretty fast an outside user can try up to
# 150k passwords per second against a good box. This means that you should
# use a very strong password otherwise it will be very easy to break.
#
# requirepass foobared
# 密码
requirepass 123456

# 命令设置获取密码
config get requirpass
config set requirpass "123456"
auth 123456                 # 验证密码登录
```
```
CLIENTS
maxclients 10000            # 设置redis最大客户端数量
maxmemory                   # 最大内存数量

```

## 9.Redis持久化
>

## 10.Redis发布订阅
>微信公众号推送订阅

## 11.Redis主从复制
>高可用：主从复制、哨兵模式

## 12.Redis缓存穿透和雪崩
>布隆过滤器

