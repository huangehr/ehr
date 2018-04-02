package com.yihu.ehr.profile.service;

import com.yihu.ehr.profile.feign.ResourceClient;
import com.yihu.ehr.profile.util.SimpleSolrQueryUtil;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    public List inspectionRecords(String demographicId, String filter, String date, String searchParam) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        // EHR_000318 报告生成时间
        String query = "{\"q\":\"demographic_id:" + demographicId + " AND (EHR_000318:* OR EHR_000353:*)\"}";
        query = SimpleSolrQueryUtil.getQuery(filter, date, query);
        Envelop masterEnvelop = resource.getMasterData(query, 1, 500, null);
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
                //追加诊断名称 start
                String subQ = "{\"q\":\"profile_id:" + masterMap.get("rowkey") + "\"}";
                Envelop subEnvelop = resource.getSubData(subQ, 1, 500, null);
                List<Map<String, Object>> subEventList = subEnvelop.getDetailModelList();
                String healthProblemName = "";
                //根据诊断名称或根据字典值进行取值
                for (Map<String ,Object> temp2 : subEventList) {
                    String diagnosis = "";
                    if (!StringUtils.isEmpty(temp2.get("EHR_000112")) || !StringUtils.isEmpty(temp2.get("EHR_000109_VALUE"))) {
                        diagnosis = temp2.get("EHR_000112") != null ? (String) temp2.get("EHR_000112") : (String) temp2.get("EHR_000109_VALUE");
                    }
                    if (StringUtils.isEmpty(diagnosis) && (!StringUtils.isEmpty(temp2.get("EHR_000295")) || !StringUtils.isEmpty(temp2.get("EHR_000293_VALUE")))) {
                        diagnosis = temp2.get("EHR_000295") != null ? (String) temp2.get("EHR_000295") : (String) temp2.get("EHR_000293_VALUE");
                    }
                    if (StringUtils.isEmpty(diagnosis) && (!StringUtils.isEmpty(temp2.get("EHR_000820")) || !StringUtils.isEmpty(temp2.get("EHR_000819_VALUE")))) {
                        diagnosis = temp2.get("EHR_000820") != null ? (String) temp2.get("EHR_000820") : (String) temp2.get("EHR_000819_VALUE");
                    }
                    if (!StringUtils.isEmpty(diagnosis)) {
                        healthProblemName += diagnosis + "、";
                    }
                }
                resultMap.put("healthProblemName", healthProblemName);
                //追加诊断名称 end
                if (!StringUtils.isEmpty(searchParam)) {
                    String orgName = (String) masterMap.get("org_name");
                    if (orgName.contains(searchParam) || healthProblemName.contains(searchParam)) {
                        resultList.add(resultMap);
                    }
                } else {
                    resultList.add(resultMap);
                }
            }
        }
        return resultList;
    }

}
