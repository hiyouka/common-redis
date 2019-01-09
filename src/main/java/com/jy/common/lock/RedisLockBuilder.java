package com.jy.common.lock;

import com.jy.common.constant.RedisToolsConstant;
import com.jy.common.constant.RedisType;
import com.jy.common.exception.RedisConnectionException;
import com.jy.common.exception.RedisLockInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class RedisLockBuilder {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * default sleep time
     */
    private static final int DEFAULT_SLEEP_TIME = 100;

    private Class<? extends RedisLock> redisLock = DefaultRedisLock.class;

    private JedisConnectionFactory jedisConnectionFactory ;

    private RedisTemplate redisTemplate ;

    private RedisType type = RedisType.SINGLE;
    private String lockPrefix = RedisToolsConstant.DEFAULT_LOCK_PREFIX;
    private int sleepTime = DEFAULT_SLEEP_TIME;

    RedisLockBuilder() {}

    public RedisLockBuilder redisType(RedisType type){
        this.type = type;
        return this;
    }

    public RedisLockBuilder lockPrefix(String lockPrefix) {
        this.lockPrefix = lockPrefix;
        return this;
    }

    public RedisLockBuilder sleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }

    public <T extends RedisLock> RedisLockBuilder lockType(Class<T> redisLock) {
        this.redisLock = redisLock;
        return this;
    }

    protected JedisConnectionFactory getJedisConnectionFactory() {
        return jedisConnectionFactory;
    }

    protected RedisType getType() {
        return type;
    }

    protected String getLockPrefix() {
        return lockPrefix;
    }

    protected int getSleepTime() {
        return sleepTime;
    }

    protected RedisTemplate getRedisTemplate(){return redisTemplate;}

    public RedisLock build(JedisConnectionFactory connection) {
        if(connection == null){
            logger.error("can not get redis connection ... ");
            throw new RedisConnectionException();
        }
        this.jedisConnectionFactory = connection;
        RedisType type = getType();
        getRedisLock();
        try{
            if(type.equals(RedisType.SINGLE)){
                Jedis jedis = (Jedis) connection.getConnection();//try connection
                jedis.close();
            }else {
                JedisCluster jedis = (JedisCluster) connection.getConnection();
            }
        }catch (Exception e){
            throw new RedisConnectionException();
        }

        return getRedisLock();
    }

    public RedisLock build(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        return getRedisLock();
    }

    private RedisLock getRedisLock(){
        RedisLock lock;
        try {
            Constructor<? extends RedisLock> constructor = redisLock.getDeclaredConstructor(RedisLockBuilder.class);
            constructor.setAccessible(true);
            lock = constructor.newInstance(this);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RedisLockInitException();
        }
        return lock;
    }


}