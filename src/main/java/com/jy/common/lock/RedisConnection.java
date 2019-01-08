package com.jy.common.lock;

import redis.clients.jedis.JedisCommands;

public interface RedisConnection {

    /**
     * get redis connection
     */
    JedisCommands getConnection();

}
