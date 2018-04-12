package com.yihu.ehr.pack.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.RedisCollection;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.model.packs.EsSimplePackage;
import com.yihu.ehr.util.datetime.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

@Component
public class PackAnalyzeTask {

    private static final String INDEX = "json_archives";
    private static final String TYPE = "info";
    private static final Long MAX_SIZE = 20000L;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    /**
     * 每一分钟增加1000条分析数据到队列中.
     * 限流，避免过多占用内存，这部分非主业务流程。
     */
    @Scheduled(cron = "0 0/10 * * * ?")
    public void delayPushTask() throws Exception {
        if (size() > MAX_SIZE) {
            return;
        }
        List<Map<String, Object>> filters = new ArrayList<>();
        //添加未分析的
        Map<String, Object> temp = new HashMap<>();
        temp.put("andOr", "and");
        temp.put("condition", "=");
        temp.put("field", "analyze_status");
        temp.put("value", 0);
        filters.add(temp);
        addToQueue(filters);
        filters.clear();
        temp.clear();
        //添加分析异常的
        temp.put("andOr", "and");
        temp.put("condition", "=");
        temp.put("field", "analyze_status");
        temp.put("value", 1);
        filters.add(temp);
        Map<String, Object> temp1 = new HashMap<>();
        temp1.put("andOr", "and");
        temp1.put("condition", "<");
        temp1.put("field", "analyze_date");
        Date date = DateUtil.addDate(-3, new Date());
        temp1.put("value",  DateUtil.toString(date) + " 00:00:00");
        filters.add(temp1);
        addToQueue(filters);
        filters.clear();
        temp.clear();
        temp1.clear();
        //添加分析错误的
        temp.put("andOr", "and");
        temp.put("condition", "=");
        temp.put("field", "analyze_status");
        temp.put("value", 2);
        filters.add(temp);
        temp1.put("andOr", "and");
        temp1.put("condition", "<");
        temp1.put("field", "analyze_fail_count");
        temp1.put("value", 3);
        filters.add(temp1);
        addToQueue(filters);
    }

    private void addToQueue( List<Map<String, Object>>  filters) throws Exception {
        List<Map<String, Object>> resultList = elasticSearchUtil.page(INDEX, TYPE, filters, "receive_date asc", 1, 100);
        for (Map<String, Object> pack : resultList) {
            String packStr = objectMapper.writeValueAsString(pack);
            EsSimplePackage esSimplePackage = objectMapper.readValue(packStr, EsSimplePackage.class);
            String packString = objectMapper.writeValueAsString(esSimplePackage);
            push(packString);
            savePack(esSimplePackage);
        }
    }

    private void push(String pack) {
        if (size() > MAX_SIZE) {
            return;
        }
        redisTemplate.opsForList().leftPush(RedisCollection.AnalyzeQueue, pack);
    }

    @Async
    public void savePack(EsSimplePackage pack) {
        Map<String, Object> updateSource = new HashMap<>();
        updateSource.put("analyze_status", 1);
        updateSource.put("analyze_date", DateUtil.toStringLong(new Date()));
        elasticSearchUtil.update(INDEX, TYPE, pack.get_id(), updateSource);
    }

    private Long size() {
        return redisTemplate.opsForList().size(RedisCollection.AnalyzeQueue);
    }
}
