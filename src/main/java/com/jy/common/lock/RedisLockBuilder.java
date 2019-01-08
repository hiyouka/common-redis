package com.jy.common.lock;

import com.jy.common.constant.RedisToolsConstant;
import com.jy.common.constant.RedisType;
import com.jy.common.exception.NotFoundLockClassException;
import com.jy.common.exception.RedisLockInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class RedisLockBuilder {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * default sleep time
     */
    private static final int DEFAULT_SLEEP_TIME = 100;

    private Class<? extends RedisLock> redisLock = DefaultRedisLock.class;

    @Autowired
    private JedisConnectionFactory jedisConnectionFactory ;

    private RedisType type = RedisType.SINGLE;
    private String lockPrefix = RedisToolsConstant.DEFAULT_LOCK_PREFIX;
    private int sleepTime = DEFAULT_SLEEP_TIME;

    RedisLockBuilder() {}

    public RedisLockBuilder connection(JedisConnectionFactory jedisConnectionFactory){
        this.jedisConnectionFactory = jedisConnectionFactory;
        return this;
    }

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

    public RedisLock build() {
        if(jedisConnectionFactory == null){
            logger.error("can not get redis connection ... ");
            throw new NotFoundLockClassException();
        }
        RedisLock lock;
        try {
            Constructor<? extends RedisLock> constructor = redisLock.getConstructor(RedisLockBuilder.class);
            lock = constructor.newInstance(this);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RedisLockInitException();
        }
        return lock;
    }

}