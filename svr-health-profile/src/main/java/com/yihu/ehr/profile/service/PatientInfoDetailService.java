package com.yihu.ehr.profile.service;


import com.yihu.ehr.profile.feign.XResourceClient;
import com.yihu.ehr.util.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author hzp 2016-05-26
 */
@Service
public class PatientInfoDetailService {
    @Autowired
    XResourceClient resource;

    String appId = "svr-health-profile";

    //患者历史用药统计
    public Envelop getDrugListStat(String demographicId,String hpId,int page,int size) throws Exception
    {
        Envelop result = resource.getResources("RS_MEDICATION_STAT",appId,"{\"fq\":\"demographic_id:"+demographicId+"\"}");
        if(result.getDetailModelList()!=null && result.getDetailModelList().size()>0)
        {

        }

        return result;
    }

    //患者历史用药记录（可分页）
    public Envelop getDrugList(String demographicId,String hpId,int page,int size) throws Exception
    {
        Envelop result = resource.getResources("RS_MEDICATION",appId,"{\"q\":\"demographic_id:"+demographicId+"\"}");
        if(result.getDetailModelList()!=null && result.getDetailModelList().size()>0)
        {

        }

        return result;
    }

    //检验指标
    public Envelop getHealthIndicators(String demographicId,String hpId,String startTime,String endTime,int page,int size) throws Exception
    {
        Envelop result = resource.getResources("RS_INSPECTION_REPORT",appId,"{\"q\":\"demographic_id:"+demographicId+"\"}");
        if(result.getDetailModelList()!=null && result.getDetailModelList().size()>0)
        {

        }

        return result;
    }

    

}
