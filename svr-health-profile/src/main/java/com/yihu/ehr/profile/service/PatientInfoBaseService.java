package com.yihu.ehr.profile.service;


import com.yihu.ehr.profile.feign.XResourceClient;
import com.yihu.ehr.util.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hzp 2016-05-26
 */
@Service
public class PatientInfoBaseService {
    @Autowired
    XResourceClient resource;

    String appId = "svr-health-profile";

    /**
     * 获取患者档案基本信息
     * @return
     * @throws Exception
     */
    public Map<String,Object> getPatientInfo(String demographicId) throws Exception
    {
        //时间排序
        Envelop result = resource.getResources("RS_PATIENT_INFO",appId,"{\"q\":\"demographic_id:"+demographicId+"\"}");
        if(result.getDetailModelList()!=null && result.getDetailModelList().size()>0)
        {
            List<Map<String,Object>> list = result.getDetailModelList();
            if(list.size() == 1)
            {
                return list.get(0);
            }
            else{
                /***************** 需要合并数据 **************************/

                /*******************************************************/

                return list.get(0);
            }
        }
        else{
            throw new Exception("查无此人！");
        }
    }

    //根据患者住院门诊记录做疾病统计
    public List<Map<String,Object>> getHealthProblem(String demographicId) throws Exception
    {
        List<Map<String,Object>> re = new ArrayList<>();
        //门诊诊断
        Envelop outpatient = resource.getResources("RS_OUTPATIENT_DIAGNOSIS",appId,"{\"join\":\"demographic_id:"+demographicId+"\"}");
        if(outpatient.getDetailModelList()!=null && outpatient.getDetailModelList().size()>0)
        {

        }

        //住院诊断
        Envelop hospitalized = resource.getResources("RS_HOSPITALIZED_DIAGNOSIS",appId,"{\"fq\":\"demographic_id:"+demographicId+"\"}");
        if(hospitalized.getDetailModelList()!=null && hospitalized.getDetailModelList().size()>0)
        {

        }

        return re;
    }

    //（疾病模型）患者 -> 门诊住院记录
    public List<Map<String,Object>> getMedicalEvents(String demographicId,String medicalEventsType,String year,String area,String hpId,int page,int size) throws Exception
    {
        List<Map<String,Object>> re = new ArrayList<>();
        //患者事件列表
        Envelop result = resource.getResources("RS_PATIENT_EVENT",appId,"{\"q\":\"demographic_id:"+demographicId+"\"}");
        if(result.getDetailModelList()!=null && result.getDetailModelList().size()>0)
        {

        }

        return re;
    }


    //根据患者住院门诊记录做年份统计
    public String getMedicalYear(String demographicId) throws Exception
    {
        //患者事件列表
        Envelop result = resource.getResources("RS_PATIENT_EVENT",appId,"{\"q\":\"demographic_id:"+demographicId+"\"}");
        if(result.getDetailModelList()!=null && result.getDetailModelList().size()>0)
        {

        }

        return "";
    }


    //根据患者住院门诊记录做地区统计
    public String getMedicalArea(String demographicId) throws Exception
    {
        //患者事件列表
        Envelop result = resource.getResources("RS_PATIENT_EVENT",appId,"{\"q\":\"demographic_id:"+demographicId+"\"}");
        if(result.getDetailModelList()!=null && result.getDetailModelList().size()>0)
        {

        }

        return "";
    }


}
