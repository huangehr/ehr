package com.yihu.ehr.profile.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.profile.feign.ResourceClient;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by progr1mmer on 2018/3/13.
 */
@Service
public class ProfileInspectionService {

    @Autowired
    private ResourceClient resource; //资源服务
    @Autowired
    private ObjectMapper objectMapper;

    public List inspectionRecords(String demographicId, String hpCode, String date, String eventType) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        String query = "{\"q\":\"demographic_id:" + demographicId + " AND (EHR_000379:* OR EHR_000353:* OR EHR_000341:* OR EHR_000338:* OR EHR_000366:*)\"}";
        query = getQuery(hpCode, date, eventType, query);
        Envelop masterEnvelop = resource.getMasterData(query, null, null, null);
        if (masterEnvelop.isSuccessFlg()) {
            List<Map<String, Object>> masterList = masterEnvelop.getDetailModelList();
            //循环获取结果集
            for (Map<String, Object> masterMap : masterList) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("profileId", masterMap.get("rowkey"));
                resultMap.put("orgCode", masterMap.get("org_code"));
                resultMap.put("orgName", masterMap.get("org_name"));
                resultMap.put("demographicId", masterMap.get("demographic_id"));
                resultMap.put("cdaVersion", masterMap.get("cda_version"));
                resultMap.put("eventDate", masterMap.get("event_date"));
                resultMap.put("profileType", masterMap.get("profile_type"));
                resultMap.put("eventType", masterMap.get("event_type"));
                resultMap.put("eventNo", masterMap.get("event_no"));
                resultList.add(resultMap);
            }
        }
        return resultList;
    }

    private String getQuery(String hpCode, String date, String eventType, String q) throws Exception {
        Map<String, String> qMap = objectMapper.readValue(q, Map.class);
        String param = qMap.get("q");
        if (hpCode != null) {
            param += " AND health_problem:*" + hpCode + "*";
        }
        if (date != null) {
            Map<String, String> dateMap = objectMapper.readValue(date, Map.class);
            if (dateMap.get("start") != null) {
                param += " AND event_date:[" + dateMap.get("start") + " TO *]";
            }
            if (dateMap.get("end") != null) {
                param += " AND event_date:[* TO " + dateMap.get("end") + "]";
            }
        }
        if (eventType != null) {
            param += " AND event_type:" + eventType;
        }
        qMap.put("q", param);
        return objectMapper.writeValueAsString(qMap);
    }
}
