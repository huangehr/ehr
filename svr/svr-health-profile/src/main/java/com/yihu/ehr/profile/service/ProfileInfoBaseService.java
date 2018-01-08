package com.yihu.ehr.profile.service;


import com.yihu.ehr.profile.feign.*;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author hzp 2016-05-26
 */
@Service
public class ProfileInfoBaseService {

    @Value("${spring.application.id}")
    private String appId;
    @Autowired
    private XResourceClient resource; //资源服务

    /**
     * @获取患者档案基本信息
     */
    public Map<String, Object> getPatientInfo(String demographicId, String version) {
        //时间排序
        Envelop envelop;
        if(version != null) {
            envelop = resource.getMasterData("{\"q\":\"demographic_id:" + demographicId + "\"}", null, null, version);
        }else {
            envelop = resource.getMasterData("{\"q\":\"demographic_id:" + demographicId + "\"}", null, null, null);
        }
        List<Map<String, Object>> list = envelop.getDetailModelList();
        if (list != null && list.size() > 0) {
            if (list.size() == 1) {
                return list.get(0);
            } else {
                //合并数据
                Map<String, Object> result = new HashMap<>();
                for (Map<String, Object> obj : list) {
                    for (String key : obj.keySet()) {
                        if (!result.containsKey(key)) {
                            result.put(key, obj.get(key));
                        }
                    }
                }
                return result;
            }
        } else {
            return null;
        }
    }

    private int CompareAgeOfDisease(String AgeOfDisease1,String AgeOfDisease2){
        int year1=0;
        int month1=0;
        int year2=0;
        int month2=0;
        if(AgeOfDisease1.split("年|个月").length>1) {
            year1 = Integer.parseInt(AgeOfDisease1.split("年|个月")[0]);
            month1 = Integer.parseInt(AgeOfDisease1.split("年|个月")[1]);
        }
        else
            month1 = Integer.parseInt(AgeOfDisease1.split("年|个月")[0]);
        if(AgeOfDisease2.split("年|个月").length>1) {
            year2 = Integer.parseInt(AgeOfDisease2.split("年|个月")[0]);
            month2 = Integer.parseInt(AgeOfDisease2.split("年|个月")[1]);
        }
        else
            month2 = Integer.parseInt(AgeOfDisease2.split("年|个月")[0]);
        if(year1 * 12 + month1 <= year2 * 12 + month2)
            return 1;
        else
            return 0;
    }

    /**
     * 全文检索
     */
    public Envelop getProfileLucene(String startTime,String endTime,List<String> lucene,Integer page,Integer size) throws Exception {
        String queryParams = "";
        if(startTime!=null && startTime.length()>0 && endTime!=null && endTime.length()>0) {
            queryParams = BasisConstant.eventDate+":["+startTime+" TO "+endTime+"]";
        }
        else {
            if(startTime!=null && startTime.length()>0) {
                queryParams = BasisConstant.eventDate+":["+startTime+" TO *]";
            }
            else if(endTime!=null && endTime.length()>0){
                queryParams = BasisConstant.eventDate+":[* TO "+endTime+"]";
            }
        }
        //全文检索
        Envelop re = resource.getResources(BasisConstant.patientEvent, "*", "*", "{\"q\":\""+queryParams.replace(' ','+')+"\"}", page, size);
        return re;
    }
}
