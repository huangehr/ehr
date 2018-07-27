package com.yihu.ehr.analyze.service.dataQuality;

import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.entity.quality.DqDatasetWarning;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by progr1mmer on 2018/6/12.
 */
@Service
public class WarningProblemService extends BaseJpaService {

    @Value("${quality.version}")
    private String defaultQualityVersion;
    @Autowired
    private ElasticSearchUtil elasticSearchUtil;
    @Autowired
    private DqDatasetWarningService dqDatasetWarningService;

    public List<Map<String, Object>> receiveDataset (String orgCode, String date) throws Exception {
        StringBuilder filters = new StringBuilder();
        filters.append("org_code=").append(orgCode).append(";")
                .append("receive_date=").append(date).append(" 00:00:00");
        List<Map<String, Object>> qcResult = elasticSearchUtil.list("json_archives_qc", "qc_dataset_detail", filters.toString());
        Set<String> versions = new HashSet<>();
        versions.add(defaultQualityVersion);
        Map<String, String> upDataset = new HashMap<>(); //上传数据集
        Map<String, String> unUpDataset = new HashMap<>(); //未上传数据集
        qcResult.forEach(item -> upDataset.put((String) item.get("dataset"), (String) item.get("dataset_name")));
        List<DqDatasetWarning> dqDatasetWarnings = dqDatasetWarningService.findByOrgCodeAndType(orgCode, "1");
        dqDatasetWarnings.forEach(item2 -> {
            if (!upDataset.containsKey(item2.getCode())) {
                unUpDataset.put(item2.getCode(), item2.getName());
            }
        });
        List<Map<String, Object>> result = new ArrayList<>();
        int index = 1;
        for (String key : unUpDataset.keySet()) {
            Map<String, Object> data = new HashMap<>();
            data.put("sn", index);
            data.put("version", defaultQualityVersion);
            data.put("code", key);
            data.put("name", unUpDataset.get(key));
            data.put("status", "未上传");
            result.add(data);
            index ++;
        }
        for (String key : upDataset.keySet()) {
            Map<String, Object> data = new HashMap<>();
            data.put("sn", index);
            data.put("version", defaultQualityVersion);
            data.put("code", key);
            data.put("name", upDataset.get(key));
            data.put("status", "已上传");
            result.add(data);
            index ++;
        }
        return result;
    }

}
