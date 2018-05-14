package com.yihu.ehr.profile.service;

import com.yihu.ehr.profile.feign.ResourceClient;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by progr1mmer on 2018/5/14.
 */
@Service
public class ProfileMotherhoodService {

    @Autowired
    private ResourceClient resourceClient;

    public Map<String, Object> overview(String demographicId, String version) {
        String masterQ = "{\"q\":\"demographic_id:" + demographicId + "\"}";
        Envelop envelop = resourceClient.getMasterData(masterQ, 1, 500, null);
        List<Map<String, Object>> list = envelop.getDetailModelList();
        Map<String, Object> patientMap = new HashMap<>();
        if (list != null && list.size() > 0) {
            Map<String, Object> result = new HashMap<>();
            if (list.size() == 1) {
                result = list.get(0);
            } else {
                //合并数据
                for (Map<String, Object> obj : list) {
                    for (String key : obj.keySet()) {
                        if (!result.containsKey(key) && obj.get(key) != null) {
                            result.put(key, obj.get(key));
                        }
                    }
                }
            }
            //初潮年龄
            if (result.get("EHR_001495") != null) {
                patientMap.put("menarcheAge", result.get("EHR_001495"));
            } else {
                patientMap.put("menarcheAge", "");
            }
            //月经周期
            if (result.get("EHR_001497") != null) {
                int week = (int)result.get("EHR_001497") / 7;
                patientMap.put("menstrualCycle", week);
            } else {
                patientMap.put("menstrualCycle", "");
            }
            //月经持续时间
            if (result.get("EHR_001496") != null) {
                patientMap.put("menstrualDuration", result.get("EHR_001496"));
            } else if (result.get("EHR_001760") != null) {
                patientMap.put("menstrualDuration", result.get("EHR_001760"));
            } else {
                patientMap.put("menstrualDuration", "");
            }
            //痛经标志
            if (result.get("EHR_001501") != null) {
                patientMap.put("dysmenorrheaSign", result.get("EHR_001501"));
            } else {
                patientMap.put("dysmenorrheaSign", "");
            }
            //孕次
            if (result.get("EHR_001491") != null) {
                patientMap.put("pregnancy", result.get("EHR_001491"));
            } else {
                patientMap.put("pregnancy", "");
            }
            //产次
            if (result.get("EHR_001492") != null) {
                patientMap.put("productionTimes", result.get("EHR_001492"));
            } else {
                patientMap.put("productionTimes", "");
            }
            //阴道分娩次数
            if (result.get("EHR_001493") != null) {
                patientMap.put("vaginalDelivery", result.get("EHR_001493"));
            } else {
                patientMap.put("vaginalDelivery", "");
            }
            //剖宫产次数
            if (result.get("EHR_001494") != null) {
                patientMap.put("caesareanSection", result.get("EHR_001494"));
            } else {
                patientMap.put("caesareanSection", "");
            }
            //流产总次数
            if (result.get("EHR_001513") != null) {
                patientMap.put("totalNumberOfAbortions", result.get("EHR_001513"));
            } else {
                patientMap.put("totalNumberOfAbortions", "");
            }
            //人工流产次数
            if (result.get("EHR_001514") != null) {
                patientMap.put("artificialAbortionTimes", result.get("EHR_001514"));
            } else {
                patientMap.put("artificialAbortionTimes", "");
            }
            //死产例数
            if (result.get("EHR_001515") != null) {
                patientMap.put("numberOfStillbirths", result.get("EHR_001515"));
            } else {
                patientMap.put("numberOfStillbirths", "");
            }
            //死胎例数
            if (result.get("EHR_001516") != null) {
                patientMap.put("_numberOfStillbirths", result.get("EHR_001516"));
            } else {
                patientMap.put("_numberOfStillbirths", "");
            }
            //妊娠并发症史
            if (result.get("EHR_001512") != null) {
                patientMap.put("complication", result.get("EHR_001512"));
            } else {
                patientMap.put("complication", "");
            }
            //妇科手术史
            if (result.get("EHR_001511") != null) {
                patientMap.put("gynecologicalSurgeryHistory", result.get("EHR_001511"));
            } else {
                patientMap.put("gynecologicalSurgeryHistory", "");
            }
        }
        return patientMap;
    }

}
