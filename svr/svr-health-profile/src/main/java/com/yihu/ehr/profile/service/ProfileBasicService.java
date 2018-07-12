package com.yihu.ehr.profile.service;

import com.yihu.ehr.profile.family.ResourceCells;
import com.yihu.ehr.profile.feign.CDADocumentClient;
import com.yihu.ehr.profile.feign.ResourceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * Created by progr1mmer on 2018/5/31.
 */
public abstract class ProfileBasicService {

    @Value("${spring.application.id}")
    protected String appId;
    @Autowired
    protected ResourceClient resource;
    @Autowired
    protected RedisService redisService;
    @Autowired
    protected CDADocumentClient cdaService;

    protected Map<String, Object> simpleEvent (Map<String, Object> detailsEvent, String searchParam) {
        Map<String, Object> simpleEvent = new HashMap<>();
        simpleEvent.put("profileId", detailsEvent.get(ResourceCells.ROWKEY));
        simpleEvent.put("orgCode", detailsEvent.get(ResourceCells.ORG_CODE));
        simpleEvent.put("orgName", detailsEvent.get(ResourceCells.ORG_NAME));
        simpleEvent.put("demographicId", detailsEvent.get(ResourceCells.DEMOGRAPHIC_ID));
        simpleEvent.put("cdaVersion", detailsEvent.get(ResourceCells.CDA_VERSION));
        simpleEvent.put("eventDate", detailsEvent.get(ResourceCells.EVENT_DATE));
        simpleEvent.put("profileType", detailsEvent.get(ResourceCells.PROFILE_TYPE));
        simpleEvent.put("eventType", detailsEvent.get(ResourceCells.EVENT_TYPE));
        simpleEvent.put("eventNo", detailsEvent.get(ResourceCells.EVENT_NO));
        //诊断名称
        String healthProblemName = "";
        if (!StringUtils.isEmpty(detailsEvent.get(ResourceCells.DIAGNOSIS_NAME))) {
            healthProblemName = ((String) detailsEvent.get(ResourceCells.DIAGNOSIS_NAME)).replaceAll(";", "、");
        } else if (!StringUtils.isEmpty(detailsEvent.get(ResourceCells.DIAGNOSIS))) {
            String [] diagnosisCode = ((String) detailsEvent.get(ResourceCells.DIAGNOSIS)).split(";");
            for (String code : diagnosisCode) {
                String name = redisService.getIcd10Name(code);
                if (!StringUtils.isEmpty(name)) {
                    healthProblemName += name + "、";
                }
            }
        } else if (!StringUtils.isEmpty(detailsEvent.get(ResourceCells.HEALTH_PROBLEM_NAME))) {
            healthProblemName = ((String) detailsEvent.get(ResourceCells.HEALTH_PROBLEM_NAME)).replaceAll(";", "、");
        } else if (!StringUtils.isEmpty(detailsEvent.get(ResourceCells.HEALTH_PROBLEM))) {
            String [] _hpCode = ((String) detailsEvent.get(ResourceCells.HEALTH_PROBLEM)).split(";");
            for (String code : _hpCode) {
                String name = redisService.getHealthProblem(code);
                if (!StringUtils.isEmpty(name)) {
                    healthProblemName += name + "、";
                }
            }
        }
        simpleEvent.put("healthProblemName", healthProblemName);
        if (!StringUtils.isEmpty(searchParam)) {
            Set<String> searchSet = new HashSet<>();
            //诊断检索数据
            if (!StringUtils.isEmpty(detailsEvent.get(ResourceCells.DIAGNOSIS_NAME))) {
                String [] data = detailsEvent.get(ResourceCells.DIAGNOSIS_NAME).toString().split(";");
                for (String datum : data) {
                    searchSet.add(datum);
                }
            }
            if (!StringUtils.isEmpty(detailsEvent.get(ResourceCells.DIAGNOSIS))) {
                String [] data = detailsEvent.get(ResourceCells.DIAGNOSIS).toString().split(";");
                for (String datum : data) {
                    String name = redisService.getIcd10Name(datum);
                    if (!StringUtils.isEmpty(name)) {
                        searchSet.add(name);
                    }
                }
            }
            if (!StringUtils.isEmpty(detailsEvent.get(ResourceCells.HEALTH_PROBLEM_NAME))) {
                String [] data = detailsEvent.get(ResourceCells.HEALTH_PROBLEM_NAME).toString().split(";");
                for (String datum : data) {
                    searchSet.add(datum);
                }
            }
            if (!StringUtils.isEmpty(detailsEvent.get(ResourceCells.HEALTH_PROBLEM))) {
                String [] data = detailsEvent.get(ResourceCells.HEALTH_PROBLEM).toString().split(";");
                for (String datum : data) {
                    String name = redisService.getHealthProblem(datum);
                    if (!StringUtils.isEmpty(name)) {
                        searchSet.add(name);
                    }
                }
            }
            String orgName = "";
            if (!StringUtils.isEmpty(detailsEvent.get(ResourceCells.ORG_NAME))) {
                orgName = (String) detailsEvent.get(ResourceCells.ORG_NAME);
            }
            String disease = org.apache.commons.lang3.StringUtils.join(searchSet.toArray(),";");
            if (disease.contains(searchParam) || orgName.contains(searchParam)) {
                return simpleEvent;
            } else {
                return null;
            }
        }
        return simpleEvent;
    }
}
