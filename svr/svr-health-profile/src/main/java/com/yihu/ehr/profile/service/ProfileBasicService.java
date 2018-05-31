package com.yihu.ehr.profile.service;

import com.yihu.ehr.profile.feign.CDADocumentClient;
import com.yihu.ehr.profile.feign.ResourceClient;
import com.yihu.ehr.profile.util.BasicConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

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

    protected Map<String, Object> simpleEvent (Map<String, Object> detailsEvent) {
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
        //追加诊断名称 start
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
        //追加诊断名称 end
        return simpleEvent;
    }
}
