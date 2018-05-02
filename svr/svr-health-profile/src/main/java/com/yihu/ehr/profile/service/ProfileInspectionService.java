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
    @Autowired
    private RedisService redisService;

    public List inspectionRecords(String demographicId, String filter, String date, String searchParam) throws Exception {
        List<Map<String, Object>> resultList = new ArrayList<>();
        // EHR_000318 报告生成时间
        String query = "{\"q\":\"demographic_id:" + demographicId + " AND (EHR_000318:* OR EHR_000353:*)\"}";
        query = SimpleSolrQueryUtil.getQuery(filter, date, query);
        Envelop masterEnvelop = resource.getMasterData(query, 1, 500, null);
        if (masterEnvelop.isSuccessFlg()) {
            List<Map<String, Object>> masterList = masterEnvelop.getDetailModelList();
            //循环获取结果集
            for (Map<String, Object> temp : masterList) {
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("profileId", temp.get("rowkey"));
                resultMap.put("orgCode", temp.get("org_code"));
                resultMap.put("orgName", temp.get("org_name"));
                resultMap.put("demographicId", temp.get("demographic_id"));
                resultMap.put("cdaVersion", temp.get("cda_version"));
                resultMap.put("eventDate", temp.get("event_date"));
                resultMap.put("profileType", temp.get("profile_type"));
                resultMap.put("eventType", temp.get("event_type"));
                resultMap.put("eventNo", temp.get("event_no"));
                //追加诊断名称 start
                String healthProblemName = "";
                if (!StringUtils.isEmpty(temp.get("health_problem_name"))) {
                    healthProblemName = ((String) temp.get("health_problem_name")).replaceAll(";", "、");
                } else if (!StringUtils.isEmpty(temp.get("health_problem"))) {
                    String [] _hpCode = ((String) temp.get("health_problem")).split(";");
                    for (String code : _hpCode) {
                        String name = redisService.getHealthProblem(code);
                        if (!StringUtils.isEmpty(name)) {
                            healthProblemName += name + "、";
                        }
                    }
                } else if (!StringUtils.isEmpty(temp.get("diagnosis_name"))) {
                    healthProblemName = ((String) temp.get("diagnosis_name")).replaceAll(";", "、");
                } else if (!StringUtils.isEmpty(temp.get("diagnosis"))) {
                    String [] diagnosisCode = ((String) temp.get("diagnosis")).split(";");
                    for (String code : diagnosisCode) {
                        String name = redisService.getIcd10Name(code);
                        if (!StringUtils.isEmpty(name)) {
                            healthProblemName += name + "、";
                        }
                    }
                }
                resultMap.put("healthProblemName", healthProblemName);
                //追加诊断名称 end
                if (!StringUtils.isEmpty(searchParam)) {
                    String orgName = (String) temp.get("org_name");
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
