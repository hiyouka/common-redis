package com.jy.common.lock;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import java.util.Collections;


public class DefaultRedisLock extends AbstractRedisLock{

    private static final String LOCK_MSG = "OK";

    private static final Long UNLOCK_MSG = 1L;

    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";


    private String lockPrefix;

    private int sleepTime;

    /**
     * time millisecond
     */
    private static final int TIME = 1000;

    DefaultRedisLock(RedisLockBuilder builder) {
        setJedisConnectionFactory(builder.getJedisConnectionFactory());
        setType(builder.getType());
        this.lockPrefix = builder.getLockPrefix();
        this.sleepTime = builder.getSleepTime();
    }

    /**
     * Non-blocking RedisLock
     *
     * @param key     RedisLock business type
     * @param request value
     * @return true RedisLock success
     * false RedisLock fail
     */
    public boolean tryLock(String key, String request) {
        //get connection
        Object connection = getConnection();
        String result = setRedisVal(key,request,-1,connection);
        return LOCK_MSG.equals(result);
    }

    /**
     * blocking RedisLock
     *  @param key lock key
     * @param request lock val
     */
    public boolean lock(String key, String request) throws InterruptedException {
        //get connection
        Object connection = getConnection();
        String result ;
        for (; ;) {
            result = setRedisVal(key, request,-1,connection);
            if (LOCK_MSG.equals(result)) {
                return true;
            }
            Thread.sleep(sleepTime);
        }
    }

    protected String setRedisVal(String key, String request, int expireTime, Object connection) {
        String result;
        if(expireTime < 0){
            expireTime = Integer.MAX_VALUE; // no expire time
        }
        if (connection instanceof Jedis){
            result = ((Jedis)connection).set(lockPrefix + key, request, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
            if (LOCK_MSG.equals(result)){
                ((Jedis) connection).close();
            }
        }else {
            result = ((JedisCluster)connection).set(lockPrefix + key, request, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
        }
        return result;
    }

    /**
     * blocking RedisLock,custom time
     *
     * @param key lock key
     * @param request lock val
     * @param blockTime custom time
     * @return boolean
     * @throws InterruptedException e
     */
    public boolean lock(String key, String request, int blockTime) throws InterruptedException {

        //get connection
        Object connection = getConnection();
        while (blockTime >= 0) {
            String result = setRedisVal(key, request, -1, connection);
            if (LOCK_MSG.equals(result)) {
                return true;
            }
            blockTime -= sleepTime;

            Thread.sleep(sleepTime);
        }
        return false;
    }


    /**
     * Non-blocking RedisLock
     *
     * @param key        RedisLock business type
     * @param request    value
     * @param expireTime custom expireTime
     * @return true RedisLock success
     * false RedisLock fail
     */
    public boolean tryLock(String key, String request, int expireTime) {
        Object connection = getConnection();
        String result = setRedisVal(key,request,expireTime,connection);
        return LOCK_MSG.equals(result);
    }


    /**
     * unlock
     * @param key lock key
     * @param request request must be the same as RedisLock request
     * @return boolean
     */
    public boolean unlock(String key, String request) {
        Object connection = getConnection();
        Object result;
        if (connection instanceof Jedis) {
            result = ((Jedis) connection).eval(script, Collections.singletonList(lockPrefix + key), Collections.singletonList(request));
            ((Jedis) connection).close();
        } else if (connection instanceof JedisCluster) {
            result = ((JedisCluster) connection).eval(script, Collections.singletonList(lockPrefix + key), Collections.singletonList(request));

        } else {
            return false;
        }
        return UNLOCK_MSG.equals(result);
    }

}
