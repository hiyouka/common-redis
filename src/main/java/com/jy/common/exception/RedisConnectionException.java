package com.jy.common.exception;

/**
 * @Date 2019/1/8
 * @Author jianglei
 */
public class RedisConnectionException extends RuntimeException{

    private final static String message = "get redis connection error";

    public RedisConnectionException() {
        super(message);
    }

}
