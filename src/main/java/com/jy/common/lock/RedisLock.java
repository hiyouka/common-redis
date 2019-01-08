package com.jy.common.lock;

public interface RedisLock {

    /**
     * Non-blocking RedisLock
     *
     * @param key     RedisLock business type
     * @param token value
     * @return true RedisLock success
     * false RedisLock fail
     */
    boolean tryLock(String key, String token) ;



    /**
     * Non-blocking RedisLock
     * @param key        RedisLock business type
     * @param token    value
     * @param expireTime custom expireTime
     * @return true RedisLock success
     * false RedisLock fail
     */
    boolean tryLock(String key, String token, int expireTime) ;


    /**
     * blocking RedisLock
     *  @param key
     * @param token
     */
    boolean lock(String key, String token) throws InterruptedException;

    /**
     * blocking RedisLock,custom time
     *
     * @param key
     * @param token
     * @param blockTime custom time
     * @return
     * @throws InterruptedException
     */
    boolean lock(String key, String token, int blockTime) throws InterruptedException;



    /**
     * unlock
     * @param key
     * @param token request must be the same as RedisLock request
     * @return
     */
    boolean unlock(String key, String token);


}
