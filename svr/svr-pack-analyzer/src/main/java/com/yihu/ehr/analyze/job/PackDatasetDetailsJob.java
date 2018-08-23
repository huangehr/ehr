package com.yihu.ehr.analyze.job;

import com.yihu.ehr.analyze.service.qc.PackageQcService;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.redis.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by progr1mmer on 2018/7/18.
 */
@Component
public class PackDatasetDetailsJob {

    @Autowired
    private RedisClient redisClient;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    @Scheduled(fixedDelay = 60000)
    public void delayUpdate() throws Exception {
        Map<String, String> datasetDetails = PackageQcService.getDatasetDetails();
        if (!datasetDetails.isEmpty()) {
            List<Map<String, Object>> indexs = new ArrayList<>();
            List<Map<String, Object>> updates = new ArrayList<>();
            datasetDetails.forEach((key, val) -> {
                String [] _key = key.split(";");
                String [] _val = val.split(";");
                Map<String, Object> map = new HashMap<>();
                StringBuilder id = new StringBuilder();
                id.append(_key[0])
                        .append("_")
                        .append(_key[1])
                        .append("_")
                        .append(_key[3])
                        .append("_")
                        .append(_key[4]);
                Map<String, Object> source = elasticSearchUtil.findById("json_archives_qc", "qc_dataset_detail", id.toString());
                map.put("_id", id.toString());
                if (source != null) {
                    map.put("count", Integer.parseInt(source.get("count").toString()) +  Integer.parseInt(_val[0]));
                    map.put("row", Integer.parseInt(source.get("row").toString()) + Integer.parseInt(_val[1]));
                    updates.add(map);
                } else {
                    map.put("org_code", _key[0]);
                    map.put("receive_date", _key[1] + " 00:00:00");
                    map.put("event_type", Integer.parseInt(_key[3]));
                    map.put("dataset", _key[4]);
                    map.put("dataset_name", redisClient.get("std_data_set_" + _key[2] + ":" + _key[4] + ":name"));
                    map.put("count", Integer.parseInt(_val[0]));
                    map.put("row", Integer.parseInt(_val[1]));
                    String orgArea = redisClient.get("organizations:" + _key[0] + ":area");
                    map.put("org_area",orgArea);
                    indexs.add(map);
                }
            });
            elasticSearchUtil.bulkIndex("json_archives_qc", "qc_dataset_detail", indexs);
            elasticSearchUtil.bulkUpdate("json_archives_qc", "qc_dataset_detail", updates);
        }
    }
}
