package com.yihu.ehr.profile.service;

import com.yihu.ehr.profile.feign.XResourceClient;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lyr on 2016/6/13.
 */
@Service
public class PatientInternetHospitalService {

    @Autowired
    XResourceClient resource;

    private static String APP_ID = "svr-health-profile";

    /**
     * 获取EHR资源
     * @param resource_code
     * @param query
     * @return
     * @throws Exception
     */
    public List<Map<String,Object>> getEhrResource(String resource_code,String query) throws Exception
    {
        Envelop envelop = resource.getResources(resource_code,APP_ID,query);

        if(envelop.getDetailModelList()!=null && envelop.getDetailModelList().size()>0)
        {
            return envelop.getDetailModelList();
        }

        return new ArrayList<Map<String,Object>>();
    }

}
