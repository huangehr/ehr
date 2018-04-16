package com.yihu.ehr.pack.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.RedisCollection;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.model.packs.EsSimplePackage;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
* 档案包解析容错处理任务
 * 1. 当解析队列为空，将数据库中状态为缓存状态的档案包加入解析队列
 * 2. 将解析状态为失败且错误次数小于三次的档案包重新加入解析队列
 * 3. 将解析状态为正在解析但解析开始时间超过当前时间一定范围内的档案包重新加入解析队列
 * Created by progr1mmer on 2017/12/18.
 */
@Component
public class FailTolerantTask {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String INDEX = "json_archives";
    private static final String TYPE = "info";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void delayPushTask() throws Exception {
        List<Map<String, Object>> filters = new ArrayList<>();
        Map<String, Object> temp = new HashMap<>();
        temp.put("andOr", "and");
        temp.put("condition", "=");
        temp.put("field", "archive_status");
        temp.put("value", 0);
        filters.add(temp);
        //当解析队列为空，将数据库中状态为缓存状态的档案包加入解析队列
        if (redisTemplate.opsForList().size(RedisCollection.PackageList) <= 0) {
            List<Map<String, Object>> resultList = elasticSearchUtil.page(INDEX, TYPE, filters, "receive_date asc", 1, 1000);
            for (Map<String, Object> pack : resultList) {
                String packStr = objectMapper.writeValueAsString(pack);
                EsSimplePackage esSimplePackage = objectMapper.readValue(packStr, EsSimplePackage.class);
                redisTemplate.opsForList().leftPush(RedisCollection.PackageList, objectMapper.writeValueAsString(esSimplePackage));
            }
        }
    }

    @Scheduled(cron = "30 0/1 * * * ?")
    public void exceptionTask() throws Exception{
        List<Map<String, Object>> filters = new ArrayList<>();
        //将解析状态为失败且错误次数小于三次的档案包重新加入解析队列
        Map<String, Object> temp = new HashMap<>();
        temp.put("andOr", "and");
        temp.put("condition", "=");
        temp.put("field", "archive_status");
        temp.put("value", 2);
        filters.add(temp);
        Map<String, Object> temp1 = new HashMap<>();
        temp1.put("andOr", "and");
        temp1.put("condition", "<");
        temp1.put("field", "fail_count");
        temp1.put("value", 3);
        filters.add(temp1);
        List<Map<String, Object>> resultList = elasticSearchUtil.page(INDEX, TYPE, filters, "receive_date asc", 1, 100);
        for (Map<String, Object> pack : resultList) {
            String packStr = objectMapper.writeValueAsString(pack);
            EsSimplePackage esSimplePackage = objectMapper.readValue(packStr, EsSimplePackage.class);
            Map<String, Object> updateSource = new HashMap<>();
            updateSource.put("archive_status", 0);
            elasticSearchUtil.update(INDEX, TYPE, esSimplePackage.get_id(), updateSource);
            redisTemplate.opsForList().leftPush(RedisCollection.PackageList, objectMapper.writeValueAsString(esSimplePackage));
        }
        filters.clear();
        temp.clear();
        temp1.clear();
        //将解析状态为正在解析但解析开始时间超过当前时间一定范围内的档案包重新加入解析队列
        temp.put("andOr", "and");
        temp.put("condition", "=");
        temp.put("field", "archive_status");
        temp.put("value", 1);
        filters.add(temp);
        temp1.put("andOr", "and");
        temp1.put("condition", "<");
        temp1.put("field", "parse_date");
        Date past = DateUtils.addDays(new Date(), -1);
        String pastStr = dateFormat.format(past);
        temp1.put("value", pastStr + " 00:00:00");
        filters.add(temp1);
        resultList = elasticSearchUtil.page(INDEX, TYPE, filters, "receive_date asc", 1, 100);
        for (Map<String, Object> pack : resultList) {
            String packStr = objectMapper.writeValueAsString(pack);
            EsSimplePackage esSimplePackage = objectMapper.readValue(packStr, EsSimplePackage.class);
            Map<String, Object> updateSource = new HashMap<>();
            updateSource.put("archive_status", 0);
            elasticSearchUtil.update(INDEX, TYPE, esSimplePackage.get_id(), updateSource);
            redisTemplate.opsForList().leftPush(RedisCollection.PackageList, objectMapper.writeValueAsString(esSimplePackage));
        }
    }
}
