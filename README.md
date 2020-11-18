# learn-redis
>Redis 是一个开源（BSD许可）的，内存中的数据结构存储系统，它可以用作数据库、缓存和消息中间件。 它支持多种类型的数据结构，如 字符串（strings）， 散列（hashes）， 列表（lists）， 集合（sets）， 有序集合（sorted sets） 与范围查询， bitmaps， hyperloglogs 和 地理空间（geospatial） 索引半径查询。 Redis 内置了 复制（replication），LUA脚本（Lua scripting）， LRU驱动事件（LRU eviction），事务（transactions） 和不同级别的 磁盘持久化（persistence）， 并通过 Redis哨兵（Sentinel）和自动 分区（Cluster）提供高可用性（high availability）。

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
# 2.五大基础类型
## 2.1.String类型
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

## 2.2.List类型
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

## 2.3.Set(集合)
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
## 2.4.Hash~Map
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
## Zset(有序集合)
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

