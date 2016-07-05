package com.yihu.ehr.profile.service;


import com.yihu.ehr.profile.feign.XDictClient;
import com.yihu.ehr.profile.feign.XGeographyClient;
import com.yihu.ehr.profile.feign.XOrganizationClient;
import com.yihu.ehr.profile.feign.XResourceClient;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author hzp 2016-05-26
 */
@Service
public class SanofiService {
    @Autowired
    XResourceClient resource; //资源服务

    @Autowired
    XOrganizationClient organization; //机构信息服务

    @Autowired
    XDictClient dictClient;

    @Autowired
    XGeographyClient addressClient;

    String appId = "svr-health-profile";


    /**
     * @获取患者档案基本信息
     */
    public List<Map<String,Object>> findByDemographic(String profileid,
                                                 String orgCode,
                                                 String name,
                                                 String telephone,
                                                 String gender,
                                                 Date birthday) throws Exception {
        //时间排序
        Envelop result = resource.getResources(BasisConstant.patientInfo, appId, "{\"q\":\"rowkey:" + profileid + "*\"}", null, null);
        return result.getDetailModelList();
    }


    // 获取数据集
    public List<Map<String,Object>> getProfileSub(String core,String profileId) throws Exception {
        Envelop result = resource.getResources(core, appId, "{\"q\":\"rowkey:" + profileId + "*\"}", null, null);
        return result.getDetailModelList();
    }


}
