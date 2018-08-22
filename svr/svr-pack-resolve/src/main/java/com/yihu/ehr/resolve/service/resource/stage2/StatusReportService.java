package com.yihu.ehr.resolve.service.resource.stage2;

import com.yihu.ehr.elasticsearch.ElasticSearchUtil;
import com.yihu.ehr.profile.ArchiveStatus;
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

    public void reportStatus(String _id, ArchiveStatus archiveStatus, int errorType, String message, Map<String, Object> callback) {
        Map<String, Object> updateSource = new HashMap<>();
        if (archiveStatus == ArchiveStatus.Finished) {
            //入库成功
            updateSource.put("profile_id", callback.get("profile_id"));
            updateSource.put("demographic_id", callback.get("demographic_id"));
            updateSource.put("event_type", callback.get("event_type"));
            updateSource.put("event_no", callback.get("event_no"));
            updateSource.put("event_date", callback.get("event_date"));
            updateSource.put("patient_id", callback.get("patient_id"));
            updateSource.put("dept", callback.get("dept"));
            updateSource.put("delay",  callback.get("delay"));
            updateSource.put("re_upload_flg", callback.get("re_upload_flg"));
            updateSource.put("finish_date", DATE_FORMAT.format(new Date()));
            updateSource.put("resourced", 1);
            updateSource.put("defect", callback.get("defect"));
            updateSource.put("patient_name", callback.get("patient_name"));
        } else if (archiveStatus == ArchiveStatus.Acquired) {
            //开始入库
            updateSource.put("parse_date", DATE_FORMAT.format(new Date()));
        } else {
            //入库失败
            updateSource.put("finish_date", null);
            if (3 <= errorType && errorType <= 7) {
                updateSource.put("fail_count", 3);
            } else {
                Map<String, Object> sourceMap = elasticSearchUtil.findById(MAIN_INDEX, MAIN_INFO, _id);
                if (null == sourceMap) {
                    return;
                }
                if ((int)sourceMap.get("fail_count") < 3) {
                    int failCount = (int)sourceMap.get("fail_count");
                    updateSource.put("fail_count", failCount + 1);
                }
            }
            updateSource.put("resourced", 0);
        }
        updateSource.put("message", message);
        updateSource.put("error_type", errorType);
        updateSource.put("archive_status", archiveStatus.ordinal());
        elasticSearchUtil.voidUpdate(MAIN_INDEX, MAIN_INFO, _id, updateSource);
    }

    public Map<String, Object> getJsonArchiveById(String id){
        return  elasticSearchUtil.findById(MAIN_INDEX, MAIN_INFO, id);
    }

}
