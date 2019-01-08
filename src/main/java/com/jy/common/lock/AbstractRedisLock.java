package com.jy.common.lock;

import com.jy.common.constant.RedisType;
import com.jy.common.util.ScriptUtil;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisCommands;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

public abstract class AbstractRedisLock implements RedisLock, com.jy.common.lock.RedisConnection {


    private JedisConnectionFactory jedisConnectionFactory;
    private RedisType type;

    /**
     * lua script
     */
    String script = ScriptUtil.getScript("RedisLock.lua");


    /**
     * get Redis connection
     * @return RedisCommands
     */
    public RedisCommands getConnection() {
        Object connection = null;
        if (type == RedisType.SINGLE){
            RedisConnection redisConnection = jedisConnectionFactory.getConnection();
            connection = redisConnection.getNativeConnection();
        }else if(type == RedisType.CLUSTER){
            RedisClusterConnection clusterConnection = jedisConnectionFactory.getClusterConnection();
            connection = clusterConnection.getNativeConnection() ;
        }
        if(connection instanceof RedisCommands){
            return (RedisCommands) connection;
        }else {
            return null;
        }
    }




    protected void setJedisConnectionFactory(JedisConnectionFactory jedisConnectionFactory) {
        this.jedisConnectionFactory = jedisConnectionFactory;
    }

    protected void setType(RedisType type) {
        this.type = type;
    }
}
