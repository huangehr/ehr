package com.yihu.ehr.pack.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.RedisCollection;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.pack.service.Package;
import com.yihu.ehr.pack.service.PackageService;
import com.yihu.ehr.util.datetime.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Component
public class PackAnalyzeTask {
    private static final Long MaxSize = 20000L;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PackageService packageService;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    /**
     * 每一分钟增加1000条分析数据到队列中.
     * 限流，避免过多占用内存，这部分非主业务流程。
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void delayPushTask() {
        try {
            if (size() > MaxSize) {
                return;
            }

            addToQueue("analyzeStatus=0");  //添加未分析的
            Date date = DateUtil.addDate(-1, new Date());
            addToQueue("analyzeStatus=1;parseDate<" + DateUtil.toString(date));  //添加分析异常的
            addToQueue("analyzeStatus=2;analyzeFailCount<3");   //添加分析错误的
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addToQueue(String filter) throws Exception {
        List<Package> packageList = packageService.search(null, filter, "+receiveDate", 1, 1000);
        for (Package pack : packageList) {
            String packStr = objectMapper.writeValueAsString(pack);
            MPackage mPackage = objectMapper.readValue(packStr, MPackage.class);
            String packString = objectMapper.writeValueAsString(mPackage);
            push(packString);

            pack.setAnalyzeStatus(1);
            pack.setAnalyzeDate(new Date());
            packageService.save(pack);
        }
    }

    private Long size() {
        return redisTemplate.opsForList().size(RedisCollection.AnalyzeQueue);
    }

    private void push(String pack) {
        if (size() > MaxSize) {
            return;
        }

        redisTemplate.opsForList().leftPush(RedisCollection.AnalyzeQueue, pack);
    }
}
