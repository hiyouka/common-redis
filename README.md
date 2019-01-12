# common-redis

## RedisLock

RedisLock have tow implementation class:   
 &emsp; **DefauleRedisLock** : not support reentrant lock   
 &emsp; **ReentrantRedisLock** : support thread reentrant


## Quick Start:

### Example:

1.get bean
```java
@Configuration
public class RedisLimitConfig {
  
    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    @Bean
    public RedisLock redisLock(){
        return LockPayLoad.newBuilder()
                .lockType(ReentrantRedisLock.class) //default DefaultRedisLock
                .redisType(RedisType.SINGLE)        //default single
                .sleepTime(100)                     //default 100
                .lockPrefix("lock_")                //default lock_
                .build(jedisConnectionFactory);
    }
    
    @Bean
    public RedisLock redisLock(){
        return LockPayLoad.newBuilder()
                .lockType(ReentrantRedisLock.class) //default DefaultRedisLock
                .redisType(RedisType.SINGLE)        //default single
                .sleepTime(100)                     //default 100
                .lockPrefix("lock_")                //default lock_
                .build(redisTemplate);
    }
}
```
2.use bean
```java
public class Test{
    @Autowired
    private RedisLock redisLock ;

    public void use() {
        String key = "key";
        String token = UUID.randomUUID().toString();
        try {
            boolean locked = redisLock.tryLock(key, token);
            if (!locked) {
                return;
            }
            //do something
        } finally {
            redisLock.unlock(key,token) ;
        }

    }
    
    public void useBlocking() {
            String key = "key";
            String token = UUID.randomUUID().toString();
            try {
                redisLock.lock(key, token);
                //do something
            } finally {
                redisLock.unlock(key,token) ;
            }
    
        }
    
}
```

