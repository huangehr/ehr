package com.yihu.ehr.analyze.service.qc;

import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.profile.AnalyzeStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by progr1mmer on 2018/6/19.
 */
@Service
public class StatusReportService {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String MAIN_INDEX = "json_archives";
    private static final String MAIN_INFO = "info";

    @Autowired
    private ElasticSearchUtil elasticSearchUtil;

    public void reportStatus(String _id, AnalyzeStatus analyzeStatus, int errorType, String message) {
        Map<String, Object> updateSource = new HashMap<>();
        if (analyzeStatus == AnalyzeStatus.Failed) {
            if (3 <= errorType && errorType <= 7) {
                updateSource.put("analyze_fail_count", 3);
            } else {
                Map<String, Object> sourceMap = elasticSearchUtil.findById(MAIN_INDEX, MAIN_INFO, _id);
                if (null == sourceMap) {
                    return;
                }
                if ((int)sourceMap.get("analyze_fail_count") < 3) {
                    int failCount = (int)sourceMap.get("analyze_fail_count");
                    updateSource.put("analyze_fail_count", failCount + 1);
                }
            }
        } else if (analyzeStatus == AnalyzeStatus.Acquired) {
            updateSource.put("analyze_date", DATE_FORMAT.format(new Date()));
        }
        if(StringUtils.isNoneBlank(message)){
            updateSource.put("message", message);
        }
        updateSource.put("error_type", errorType);
        updateSource.put("analyze_status", analyzeStatus.ordinal());
        elasticSearchUtil.voidUpdate(MAIN_INDEX, MAIN_INFO, _id, updateSource);
    }

    public Map<String, Object> getJsonArchiveById(String id){
        return  elasticSearchUtil.findById(MAIN_INDEX, MAIN_INFO, id);
    }
}
