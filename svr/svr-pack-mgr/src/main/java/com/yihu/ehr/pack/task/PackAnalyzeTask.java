package com.yihu.ehr.pack.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.RedisCollection;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.model.packs.EsSimplePackage;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class PackAnalyzeTask {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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
     * 错开任务触发时间，并增加条件，获取的最早的数据也要是五小时前的，
     * 尽量防止同时操作一份数据，出现错误。
     */
    @Scheduled(cron = "45 0/10 * * * ?")
    public void delayPushTask() throws Exception {
        if (size() >= MAX_SIZE) {
            return;
        }
        List<Map<String, Object>> updateSourceList = new ArrayList<>();
        List<String> esSimplePackageList = new ArrayList<>();
        String fHours = dateFormat.format(DateUtils.addHours(new Date(), -5));
        //添加未分析的
        addToQueue("analyze_status=0;receive_date<" + fHours, updateSourceList, esSimplePackageList);
        //添加分析错误的
        addToQueue("analyze_status=2;analyze_fail_count<3;receive_date<" + fHours , updateSourceList, esSimplePackageList);
        //添加分析异常的
        String date = DateUtil.toString(DateUtil.addDate(-3, new Date())) + " 00:00:00";
        addToQueue("analyze_status=1;analyze_date<" + date, updateSourceList, esSimplePackageList);
        savePack(updateSourceList); //更新
        esSimplePackageList.forEach(item -> push(item)); //推送消息
    }

    private void addToQueue(String filters, List<Map<String, Object>> updateSourceList, List<String> pushList) throws Exception {
        List<Map<String, Object>> resultList = elasticSearchUtil.page(INDEX, TYPE, filters, "-receive_date", 1, 1000);
        for (Map<String, Object> pack : resultList) {
            Map<String, Object> updateSource = new HashMap<>();
            updateSource.put("_id", pack.get("_id"));
            updateSource.put("analyze_status", 1);
            updateSource.put("analyze_date", DateUtil.toStringLong(new Date()));
            updateSourceList.add(updateSource);
            String packStr = objectMapper.writeValueAsString(pack);
            EsSimplePackage esSimplePackage = objectMapper.readValue(packStr, EsSimplePackage.class);
            pushList.add(objectMapper.writeValueAsString(esSimplePackage));
        }
    }

    private void push(String pack) {
        if (size() > MAX_SIZE) {
            return;
        }
        redisTemplate.opsForList().leftPush(RedisCollection.AnalyzeQueue, pack);
    }

    @Async
    public void savePack(List<Map<String, Object>> sourceList) {
        elasticSearchUtil.bulkUpdate(INDEX, TYPE, sourceList);
    }

    private Long size() {
        return redisTemplate.opsForList().size(RedisCollection.AnalyzeQueue);
    }
}
