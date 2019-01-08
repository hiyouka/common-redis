package com.jy.common.lock;

public class LockPayLoad {

    public static RedisLockBuilder newBuilder(){
        return new RedisLockBuilder();
    }

}
