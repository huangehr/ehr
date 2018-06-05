package com.yihu.ehr.profile.service;

import com.yihu.ehr.profile.feign.CDADocumentClient;
import com.yihu.ehr.profile.feign.ResourceClient;
import com.yihu.ehr.profile.util.BasicConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        simpleEvent.put("profileId", detailsEvent.get(BasicConstant.rowkey));
        simpleEvent.put("orgCode", detailsEvent.get(BasicConstant.orgCode));
        simpleEvent.put("orgName", detailsEvent.get(BasicConstant.orgName));
        simpleEvent.put("demographicId", detailsEvent.get(BasicConstant.demographicId));
        simpleEvent.put("cdaVersion", detailsEvent.get(BasicConstant.cdaVersion));
        simpleEvent.put("eventDate", detailsEvent.get(BasicConstant.eventDate));
        simpleEvent.put("profileType", detailsEvent.get(BasicConstant.profileType));
        simpleEvent.put("eventType", detailsEvent.get(BasicConstant.eventType));
        simpleEvent.put("eventNo", detailsEvent.get(BasicConstant.eventNo));
        //诊断名称
        String healthProblemName = "";
        if (!StringUtils.isEmpty(detailsEvent.get(BasicConstant.diagnosisName))) {
            healthProblemName = ((String) detailsEvent.get(BasicConstant.diagnosisName)).replaceAll(";", "、");
        } else if (!StringUtils.isEmpty(detailsEvent.get(BasicConstant.diagnosis))) {
            String [] diagnosisCode = ((String) detailsEvent.get(BasicConstant.diagnosis)).split(";");
            for (String code : diagnosisCode) {
                String name = redisService.getIcd10Name(code);
                if (!StringUtils.isEmpty(name)) {
                    healthProblemName += name + "、";
                }
            }
        } else if (!StringUtils.isEmpty(detailsEvent.get(BasicConstant.healthProblemName))) {
            healthProblemName = ((String) detailsEvent.get(BasicConstant.healthProblemName)).replaceAll(";", "、");
        } else if (!StringUtils.isEmpty(detailsEvent.get(BasicConstant.healthProblem))) {
            String [] _hpCode = ((String) detailsEvent.get(BasicConstant.healthProblem)).split(";");
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
            if (!StringUtils.isEmpty(detailsEvent.get(BasicConstant.diagnosisName))) {
                String [] data = detailsEvent.get(BasicConstant.diagnosisName).toString().split(";");
                for (String datum : data) {
                    searchSet.add(datum);
                }
            }
            if (!StringUtils.isEmpty(detailsEvent.get(BasicConstant.diagnosis))) {
                String [] data = detailsEvent.get(BasicConstant.diagnosis).toString().split(";");
                for (String datum : data) {
                    String name = redisService.getIcd10Name(datum);
                    if (!StringUtils.isEmpty(name)) {
                        searchSet.add(name);
                    }
                }
            }
            if (!StringUtils.isEmpty(detailsEvent.get(BasicConstant.healthProblemName))) {
                String [] data = detailsEvent.get(BasicConstant.healthProblemName).toString().split(";");
                for (String datum : data) {
                    searchSet.add(datum);
                }
            }
            if (!StringUtils.isEmpty(detailsEvent.get(BasicConstant.healthProblem))) {
                String [] data = detailsEvent.get(BasicConstant.healthProblem).toString().split(";");
                for (String datum : data) {
                    String name = redisService.getHealthProblem(datum);
                    if (!StringUtils.isEmpty(name)) {
                        searchSet.add(name);
                    }
                }
            }
            String orgName = "";
            if (!StringUtils.isEmpty(detailsEvent.get(BasicConstant.orgName))) {
                orgName = (String) detailsEvent.get(BasicConstant.orgName);
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
