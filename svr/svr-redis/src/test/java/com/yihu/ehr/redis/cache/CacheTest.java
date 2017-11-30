package com.yihu.ehr.redis.cache;

import com.yihu.ehr.RedisServiceApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Redis缓存测试
 *
 * @author 张进军
 * @date 2017/11/29 08:56
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(RedisServiceApp.class)
@WebAppConfiguration
public class CacheTest {

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void cacheStringTest() {
        ValueOperations<String, Object> valOps = redisTemplate.opsForValue();
        valOps.set("message01", "lucky day 1.");
        valOps.set("message02", "lucky day 2.");
        valOps.set("message03", "lucky day 3.");

//        BoundValueOperations<String, Object> bndValOps = redisTemplate.boundValueOps("name");
//        bndValOps.set("lucky day.");

//        redisTemplate.expire("message01", 10, TimeUnit.SECONDS);
    }

    @Test
    public void cacheListTest() {
//        ListOperations<String, Object> listOps = redisTemplate.opsForList();
//        listOps.leftPush("country", "China");
//        listOps.leftPush("country", "USA");
//        listOps.leftPush("country", "UK");

        BoundListOperations<String, Object> bndListOps = redisTemplate.boundListOps("country");
        bndListOps.leftPush("China");
        bndListOps.leftPush("USA");
        bndListOps.leftPush("UK");

//        redisTemplate.expire("country", 1, TimeUnit.MINUTES);
    }

    @Test
    public void deleteCacheTest() {
        redisTemplate.delete("name");
    }

    @Test
    public void redisCallbackTest() {
        String usedMemory = (String) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
//                connection.bgSave(); // 生成快照
                return connection.info("memory").get("used_memory");
            }
        });

        System.out.println("Redis 内存大小： " + usedMemory);
    }

}
