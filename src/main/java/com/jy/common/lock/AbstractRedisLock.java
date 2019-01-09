package com.jy.common.lock;

import com.jy.common.constant.RedisType;
import com.jy.common.util.ScriptUtil;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisCommands;

public abstract class AbstractRedisLock implements RedisLock, com.jy.common.lock.RedisConnection {


    private JedisConnectionFactory jedisConnectionFactory;
    private RedisTemplate redisTemplate;
    private RedisType type;

    /**
     * lua script
     */
    String script = ScriptUtil.getScript("unlock.lua");


    /**
     * get Redis connection
     * @return RedisCommands
     */
    public JedisCommands getConnection() {
        JedisCommands connection = null;
        if(jedisConnectionFactory == null){
            return connection;
        }
        if (type == RedisType.SINGLE){
            RedisConnection redisConnection = jedisConnectionFactory.getConnection();
            connection = (JedisCommands) redisConnection.getNativeConnection();
        }else if(type == RedisType.CLUSTER){
            RedisClusterConnection clusterConnection = jedisConnectionFactory.getClusterConnection();
            connection = (JedisCommands) clusterConnection.getNativeConnection();
        }
        return connection;
    }




    protected void setJedisConnectionFactory(JedisConnectionFactory jedisConnectionFactory) {
        this.jedisConnectionFactory = jedisConnectionFactory;
    }

    protected void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    protected RedisTemplate getRedisTemplate(){return redisTemplate;}

    protected void setType(RedisType type) {
        this.type = type;
    }
}
