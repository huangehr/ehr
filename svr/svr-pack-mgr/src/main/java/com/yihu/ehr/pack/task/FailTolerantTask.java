package com.yihu.ehr.pack.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.RedisCollection;
import com.yihu.ehr.model.packs.MPackage;
import com.yihu.ehr.pack.service.Package;
import com.yihu.ehr.pack.service.PackageService;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
* 档案包解析容错处理任务
 * 1. 当解析队列为空，将数据库中状态为缓存状态的档案包加入解析队列
 * 2. 将解析状态为失败且错误次数小于三次的档案包重新加入解析队列
 * 3. 将解析状态为正在解析但解析开始时间超过当前时间一定范围内的档案包重新加入解析队列
 * Created by progr1mmer on 2017/12/18.
 */
@Component
public class FailTolerantTask {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PackageService packageService;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;

    @Scheduled(cron = "0/30 * * * * ?")
    public void delayPushTask(){
        try {
            //当解析队列为空，将数据库中状态为缓存状态的档案包加入解析队列
            if(redisTemplate.opsForList().size(RedisCollection.PackageList) <= 0) {
                List<Package> packageList = packageService.search(null, "archiveStatus=Received", "+receiveDate", 1, 1000);
                for(Package pack: packageList) {
                    String packStr = objectMapper.writeValueAsString(pack);
                    MPackage mPackage = objectMapper.readValue(packStr, MPackage.class);
                    packageService.save(pack);
                    redisTemplate.opsForList().leftPush(RedisCollection.PackageList, objectMapper.writeValueAsString(mPackage));
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    //@Scheduled(cron = "0/2 * * * * ?")
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void exceptionTask() {
        try {
            //将解析状态为失败且错误次数小于三次的档案包重新加入解析队列
            List<Package> packageList = packageService.search(null, "failCount<3;archiveStatus=Failed", "+receiveDate", 1, 200);
            for(Package pack : packageList) {
                String packStr = objectMapper.writeValueAsString(pack);
                MPackage mPackage = objectMapper.readValue(packStr, MPackage.class);
                pack.setArchiveStatus(ArchiveStatus.Received);
                packageService.save(pack);
                redisTemplate.opsForList().leftPush(RedisCollection.PackageList, objectMapper.writeValueAsString(mPackage));
            }
            //将解析状态为正在解析但解析开始时间超过当前时间一定范围内的档案包重新加入解析队列
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date past = DateUtils.addDays(new Date(), -2);
            String pastStr = dateFormat.format(past);
            packageList = packageService.search(null, "archiveStatus=Acquired;parseDate<" + pastStr, "+receiveDate", 1, 200);
            for(Package pack: packageList) {
                String packStr = objectMapper.writeValueAsString(pack);
                MPackage mPackage = objectMapper.readValue(packStr, MPackage.class);
                pack.setArchiveStatus(ArchiveStatus.Received);
                packageService.save(pack);
                redisTemplate.opsForList().leftPush(RedisCollection.PackageList, objectMapper.writeValueAsString(mPackage));
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
