package com.yihu.ehr.pack.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.model.packs.EsSimplePackage;
import com.yihu.ehr.profile.queue.RedisCollection;
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
 * 1. 当解析队列为空，将完成质控且状态为待解析的档案包加入解析队列
 * 2. 将完成质控且解析状态为失败且错误次数小于三次的档案包重新加入解析队列
 * 3. 将完成质控且解析状态为正在解析但解析开始时间超过当前时间一定范围内的档案包重新加入解析队列
 * Created by progr1mmer on 2017/12/18.
 */
@Component
public class PackResolveTask {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String INDEX = "json_archives";
    private static final String TYPE = "info";
    private static final String QUEUE = RedisCollection.ResolveQueue;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Scheduled(fixedDelay = 30000)
    public void delayPushTask() throws Exception {
        List<String> esSimplePackageList = new ArrayList<>(200);
        //当解析队列为空，将数据库中状态为缓存状态的档案包加入解析队列
        if (redisTemplate.opsForList().size(QUEUE) <= 0) {
            List<Map<String, Object>> resultList = elasticSearchUtil.page(INDEX, TYPE, "analyze_status=3;archive_status=0", "+receive_date", 1, 1000);
            for (Map<String, Object> pack : resultList) {
                String packStr = objectMapper.writeValueAsString(pack);
                EsSimplePackage esSimplePackage = objectMapper.readValue(packStr, EsSimplePackage.class);
                redisTemplate.opsForList().leftPush(QUEUE, objectMapper.writeValueAsString(esSimplePackage));
            }
        }
        //将解析状态为失败且错误次数小于三次的档案包重新加入解析队列
        List<Map<String, Object>> resultList = elasticSearchUtil.page(INDEX, TYPE, "analyze_status=3;archive_status=2;fail_count<3", "+receive_date", 1, 100);
        List<Map<String, Object>> updateSourceList = new ArrayList<>(resultList.size());
        for (Map<String, Object> pack : resultList) {
            Map<String, Object> updateSource = new HashMap<>();
            updateSource.put("_id", pack.get("_id"));
            updateSource.put("archive_status", 0);
            updateSourceList.add(updateSource);
            String packStr = objectMapper.writeValueAsString(pack);
            EsSimplePackage esSimplePackage = objectMapper.readValue(packStr, EsSimplePackage.class);
            esSimplePackageList.add(objectMapper.writeValueAsString(esSimplePackage));
        }
        //将解析状态为正在解析但解析开始时间超过当前时间一定范围内的档案包重新加入解析队列
        Date past = DateUtils.addDays(new Date(), -1);
        String pastStr = dateFormat.format(past) + " 00:00:00";
        resultList = elasticSearchUtil.page(INDEX, TYPE, "analyze_status=3;archive_status=1;parse_date<" + pastStr, "+receive_date", 1, 100);
        for (Map<String, Object> pack : resultList) {
            Map<String, Object> updateSource = new HashMap<>();
            updateSource.put("_id", pack.get("_id"));
            updateSource.put("archive_status", 0);
            updateSourceList.add(updateSource);
            String packStr = objectMapper.writeValueAsString(pack);
            EsSimplePackage esSimplePackage = objectMapper.readValue(packStr, EsSimplePackage.class);
            esSimplePackageList.add(objectMapper.writeValueAsString(esSimplePackage));
        }
        elasticSearchUtil.bulkUpdate(INDEX, TYPE, updateSourceList);
        esSimplePackageList.forEach(item -> redisTemplate.opsForList().leftPush(QUEUE, item));
    }
}
