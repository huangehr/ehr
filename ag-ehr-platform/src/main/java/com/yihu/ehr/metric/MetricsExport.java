package com.yihu.ehr.metric;

import com.yihu.ehr.api.ServiceApi;
import org.springframework.boot.actuate.metrics.repository.redis.RedisMetricRepository;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author Sand
 * @created 2016.05.18 10:04
 */
public class MetricsExport {
    RedisMetricRepository metricRepo;

    @Scheduled(cron = "0/1 * * * * ? *")
    public void exportMetrics(){
    }
}
