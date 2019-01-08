package com.jy.common.exception;

public class NotFoundLockClassException extends RuntimeException {

    private final static String message = "not found class who extends RedisLock";

    public NotFoundLockClassException() {
        super(message);
    }
}
