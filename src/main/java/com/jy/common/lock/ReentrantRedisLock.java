package com.jy.common.lock;

import org.springframework.util.StringUtils;
import java.util.UUID;


/**
 * Function:
 * token can be customized but must be thread unique
 * @author jianglei
 * Date: 2019/1/7
 * @since JDK 1.8
 */
public class ReentrantRedisLock extends DefaultRedisLock{

    private static ThreadLocal<TokenBody> tokenMap = new ThreadLocal<>();

    private ReentrantRedisLock(RedisLockBuilder builder) {
        super(builder);
    }

    /**
     * Non-blocking reentrant RedisLock
     * @param key mark the method need to synchronize
     * @return boolean
     * @Date 2019/1/7
     */
    public boolean tryLock(String key, String token) {
        token = getLockToken(token);
        // the same thread not need get RedisLock
        return isSameThread(token) || super.tryLock(key, token);
    }

    @Override
    public boolean tryLock(String key, String token, int expireTime) {
        token = getLockToken(token);
        return isSameThread(token) || super.tryLock(key, token,expireTime);
    }

    /**
     * blocking reentrant RedisLock
     * @param key lock key
     * @param token lock val
     * @return void
     * @Date 2019/1/7
     */
    public void lock(String key, String token) throws InterruptedException {
        token = getLockToken(token);
        if(isSameThread(token))
            return;
        super.lock(key,token);
    }


    /**
     * blocking reentrant RedisLock
     * @param key lock key
     * @param token lock val
     * @param tryTime the time to get the RedisLock
     * @return  boolean is get lock
     * @Date 2019/1/8
     */
    public boolean lock(String key, String token, int tryTime) throws InterruptedException {
        token = getLockToken(token);
        return isSameThread(token) || super.lock(key, token, tryTime);
    }

    /**
     * unlock
     *
     * @param key lock key
     * @param token request must be the same as RedisLock request
     * @return boolean
     */
    public boolean unlock(String key, String token) {
        return determineDelete(token) && super.unlock(key, token);
    }


    private boolean isSameThread(String token){
        TokenBody tokenBody = tokenMap.get();
        boolean result = false;
        if(tokenBody != null){
            if(tokenBody.getToken().equals(token)){
                tokenBody.addNum();
                result = true;
            }
        }
        return result;
    }

    /**
     * get RedisLock msg
     * @return java.lang.String
     * @Date 2019/1/7
     */
    private String getLockToken(String token){
        if(StringUtils.isEmpty(token)){
            token = UUID.randomUUID().toString();
        }
        TokenBody tokenBody = tokenMap.get();
        if(tokenBody == null){  // this thread first get RedisLock
            tokenBody = new TokenBody(token,1);
            tokenMap.set(tokenBody);
        }
        return tokenBody.getToken();
    }



    /**
     * Determine can be delete
     * @param token lock key
     * @return boolean
     * @Date 2019/1/7
     *
     */
    private boolean determineDelete(String token){
        TokenBody tokenBody = tokenMap.get();
        boolean canBeDelete = false;
        if(tokenBody != null){
            if(tokenBody.getToken().equals(token)){
                tokenBody.minusNum();
                if(tokenBody.getNum()==0){
                    canBeDelete = true;
                }
            }
        }
        return canBeDelete;
    }



    class TokenBody{

        private String token;
        private long num;

        TokenBody(String token, long num) {
            this.token = token;
            this.num = num;
        }

        String getToken() {
            return token;
        }

        long getNum() {
            return num;
        }

        void addNum(){
            num++;
        }

        void minusNum(){
            num--;
        }

    }
}