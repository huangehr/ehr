package com.yihu.ehr.pack.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.model.packs.EsSimplePackage;
import com.yihu.ehr.profile.queue.RedisCollection;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.yihu.ehr.util.datetime.DateUtil.DEFAULT_YMDHMSDATE_FORMAT;

/**
 * 档案包质控容错处理任务
 * 1. 当质控队列为空，将状态为待质控的档案包加入质控队列
 * 2. 将质控状态为失败且错误次数小于三次的档案包重新加入质控队列
 * 3. 将质控状态为正在进行质控处理但质控开始时间超过当前时间一定范围内的档案包重新加入质控队列
 * Created by progr1mmer on 2017/12/18.
 */
@Component
public class PackAnalyzeTask {

    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private static final String INDEX = "json_archives";
    private static final String TYPE = "info";

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Scheduled(fixedDelay = 30000)
    public void delayPushTask() throws Exception {
        List<String> esSimplePackageList = new ArrayList<>(200);
        //当质控队列为空，将状态为待质控的档案包加入质控队列
        if (redisTemplate.opsForSet().size(RedisCollection.AnalyzeQueueVice) <= 0 ) {
            Page<Map<String, Object>> result = elasticSearchUtil.page(INDEX, TYPE, "analyze_status=0", "+receive_date", 1, 1000);
            for (Map<String, Object> pack : result) {
                pack.put("analyze_status",1);
                pack.put("message","加入消息队列,等待质控");
                Map<String, Object> update = elasticSearchUtil.update(INDEX, TYPE, pack.get("_id") + "", pack);
                String packStr = objectMapper.writeValueAsString(update);
                EsSimplePackage esSimplePackage = objectMapper.readValue(packStr, EsSimplePackage.class);
                redisTemplate.opsForSet().add(RedisCollection.AnalyzeQueueVice, objectMapper.writeValueAsString(esSimplePackage));
            }
        }
        //将质控状态为失败且错误次数小于三次的档案包重新加入质控队列
        Page<Map<String, Object>> result = elasticSearchUtil.page(INDEX, TYPE, "analyze_status=2;analyze_fail_count<3", "+receive_date", 1, 100);
        List<Map<String, Object>> updateSourceList = new ArrayList<>();
        for (Map<String, Object> pack : result) {
            Map<String, Object> updateSource = new HashMap<>();
            updateSource.put("_id", pack.get("_id"));
            updateSource.put("analyze_status", 1);
            updateSource.put("message","重新加入消息队列,等待质控");
            updateSourceList.add(updateSource);
            String packStr = objectMapper.writeValueAsString(pack);
            EsSimplePackage esSimplePackage = objectMapper.readValue(packStr, EsSimplePackage.class);
            esSimplePackageList.add(objectMapper.writeValueAsString(esSimplePackage));
        }
        //将质控状态为正在进行质控处理但质控开始时间超过当前时间一定范围内的档案包重新加入质控队列
        Date past = DateUtils.addDays(new Date(), -1);
        String pastStr = dateFormat.format(past) + " 00:00:00";
        result = elasticSearchUtil.page(INDEX, TYPE, "analyze_status=1;analyze_date<" + pastStr, "+receive_date", 1, 100);
        for (Map<String, Object> pack : result) {
            Map<String, Object> updateSource = new HashMap<>();
            updateSource.put("_id", pack.get("_id"));
            updateSource.put("analyze_date",DateUtil.toStringLong(new Date()));
            updateSource.put("analyze_status", 1);
            updateSource.put("message","重新加入消息队列,等待质控");
            updateSourceList.add(updateSource);
            String packStr = objectMapper.writeValueAsString(pack);
            EsSimplePackage esSimplePackage = objectMapper.readValue(packStr, EsSimplePackage.class);
            esSimplePackageList.add(objectMapper.writeValueAsString(esSimplePackage));
        }
        elasticSearchUtil.bulkUpdate(INDEX, TYPE, updateSourceList);
        esSimplePackageList.forEach(item -> redisTemplate.opsForSet().add(RedisCollection.AnalyzeQueueVice, item));
    }
}
