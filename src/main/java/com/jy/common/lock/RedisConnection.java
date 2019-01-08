package com.jy.common.lock;

import org.springframework.data.redis.connection.RedisCommands;

public interface RedisConnection {

    /**
     * get redis connection
     */
    RedisCommands getConnection();

}
