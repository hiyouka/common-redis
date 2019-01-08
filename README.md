# common-redis

## RedisLock

RedisLock have tow implementation class:   
 &emsp; DefauleRedisLock : not support reentrant lock   
 &emsp; ReentrantLock : support thread reentrant

### Example:

1.get bean
```java
@Configuration
public class RedisLimitConfig {
  
    @Autowired
    private JedisConnectionFactory jedisConnectionFactory;
    
    @Bean
    public RedisLock redisLock(){
        return LockPayLoad.newBuilder()
                .connection(jedisConnectionFactory) //default get from spring ioc
                .lockType(ReentrantRedisLock.class) //default DefaultRedisLock
                .redisType(RedisType.SINGLE)        //default single
                .sleepTime(100)                     //default 100
                .lockPrefix("lock_")                //default lock_
                .build();
    }
}
```
2.use bean
```java
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
```

