package com.yihu.ehr.redis.cache;

import com.yihu.ehr.RedisServiceApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author 张进军
 * @date 2018/5/30 09:28
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RedisServiceApp.class)
public class CacheTest {

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Test
    public void listTest() {
        ListOperations<String, Object> listOps = redisTemplate.opsForList();
        long num = 100000;
        redisTemplate.delete("list_test");
        for (int i = 1; i <= num; i++) {
            listOps.leftPush("list_test", "test-" + i);
        }

        long start = System.currentTimeMillis();
        while (true) {
            Object obj = listOps.rightPop("list_test");
            if (obj == null) {
                break;
            }
            System.out.println(obj.toString());
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("条数：" + num + "，用时：" + time);
    }

}
