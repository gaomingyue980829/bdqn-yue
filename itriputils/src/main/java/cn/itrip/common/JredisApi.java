package cn.itrip.common;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
@Component

public class JredisApi {
    Jedis redis = new Jedis("127.0.0.1",6379);
    public void setRedis(String key,String value,int num){
        redis.auth("123456");
        redis.setex(key,num,value);
    }

    public String getRedis(String key){
        redis.auth("123456");
        return redis.get(key);
    }

}
