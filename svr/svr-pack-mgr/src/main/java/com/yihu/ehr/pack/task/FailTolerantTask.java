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
 * 档案包错误重传处理任务
 * 档案包解析错误三次以上则不在进行重复解析
 * 该任务定时将解析错误三次以下的档案包重新放入解析的队列中，如果错误次数超过三次则不再进行处理
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

    //@Scheduled(cron = "0/2 * * * * ?")
    @Scheduled(cron = "0 0 0/1 * * ?")
    public void startTask() {
        try {
            //处理接收时间大于特定值但还未解析的档案包
            if(redisTemplate.opsForList().size(RedisCollection.PackageList) <= 0) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date past = DateUtils.addDays(new Date(), -3);
                String pastStr = dateFormat.format(past);
                List<Package> packageList = packageService.search(null, "archiveStatus=Received,Acquired;receiveDate<" + pastStr, "-receiveDate", 1, 200);
                for(Package pack: packageList) {
                    String packStr = objectMapper.writeValueAsString(pack);
                    MPackage mPackage = objectMapper.readValue(packStr, MPackage.class);
                    pack.setArchiveStatus(ArchiveStatus.Received);
                    packageService.save(pack);
                    redisTemplate.opsForList().leftPush(RedisCollection.PackageList, objectMapper.writeValueAsString(mPackage));
                }
            }
            //处理解析次错误数小于特定值的档案包
            List<Package> packageList = packageService.search(null, "failCount<3;archiveStatus=Failed", "-receiveDate", 1, 100);
            for(Package pack : packageList) {
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
