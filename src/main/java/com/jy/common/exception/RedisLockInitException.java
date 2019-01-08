package com.jy.common.exception;

public class RedisLockInitException extends RuntimeException{

    private final static String message = "redis lock init error";

    public RedisLockInitException() {
        super(message);
    }

}
