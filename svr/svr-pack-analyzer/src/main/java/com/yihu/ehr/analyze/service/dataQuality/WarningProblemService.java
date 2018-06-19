package com.yihu.ehr.analyze.service.dataQuality;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.entity.quality.DqDatasetWarning;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.redis.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by progr1mmer on 2018/6/12.
 */
@Service
public class WarningProblemService extends BaseJpaService {

    private static final String INDEX = "json_archives_qc";
    private static final String QC_DATASET_INFO = "qc_dataset_info";

    @Value("${quality.version}")
    private String defaultQualityVersion;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private DqDatasetWarningService dqDatasetWarningService;
    @Autowired
    private RedisClient redisClient;

    public List<Map<String, Object>> receiveDataset (String orgCode, String date) throws Exception {
        StringBuilder filters = new StringBuilder();
        filters.append("org_code=").append(orgCode).append(";")
                .append("receive_date>=").append(date).append(" 00:00:00").append(";")
                .append("receive_date<").append(date).append(" 23:59:59").append(";");
        List<Map<String, Object>> qcResult = elasticSearchUtil.list(INDEX, QC_DATASET_INFO, filters.toString());
        Set<String> versions = new HashSet<>();
        versions.add(defaultQualityVersion);
        Map<String, String> upDataset = new HashMap<>(); //上传数据集
        Map<String, String> unUpDataset = new HashMap<>(); //未上传数据集
        for (Map<String, Object> stringObjectMap : qcResult) {
            versions.add((String) stringObjectMap.get("version"));
            List<Map<String, Object>> details = objectMapper.readValue((String) stringObjectMap.get("details"), List.class);
            details.forEach(item -> {
                item.keySet().forEach(item2 -> {
                    upDataset.put(item2, (String) stringObjectMap.get("version"));
                });
            });
        }
        versions.forEach(item -> {
            List<DqDatasetWarning> dqDatasetWarnings = dqDatasetWarningService.findByOrgCodeAndType(orgCode, "1");
            dqDatasetWarnings.forEach(item2 -> {
                if (!upDataset.containsKey(item2.getCode())) {
                    unUpDataset.put(item2.getCode(), item);
                } else {
                    if (!upDataset.get(item2.getCode()).equals(item)) {
                        unUpDataset.put(item2.getCode(), item);
                    }
                }
            });
        });
        List<Map<String, Object>> result = new ArrayList<>();
        int index = 1;
        for (String key : unUpDataset.keySet()) {
            Map<String, Object> data = new HashMap<>();
            data.put("sn", index);
            data.put("version", unUpDataset.get(key));
            data.put("code", key);
            data.put("name", redisClient.get("std_data_set_" + unUpDataset.get(key) + ":" + key + ":name"));
            data.put("status", "未上传");
            result.add(data);
            index ++;
        }
        for (String key : upDataset.keySet()) {
            Map<String, Object> data = new HashMap<>();
            data.put("sn", index);
            data.put("version", upDataset.get(key));
            data.put("code", key);
            data.put("name", redisClient.get("std_data_set_" + upDataset.get(key) + ":" + key + ":name"));
            data.put("status", "已上传");
            result.add(data);
            index ++;
        }
        return result;
    }

}
