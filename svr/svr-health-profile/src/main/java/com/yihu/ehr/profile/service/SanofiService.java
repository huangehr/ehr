package com.yihu.ehr.profile.service;


import com.yihu.ehr.profile.feign.XDictClient;
import com.yihu.ehr.profile.feign.XGeographyClient;
import com.yihu.ehr.profile.feign.XOrganizationClient;
import com.yihu.ehr.profile.feign.XResourceClient;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.rest.Envelop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author hzp 2016-05-26
 */
@Service
public class SanofiService {
    @Autowired
    XResourceClient resourceClient; //资源服务

    @Autowired
    XOrganizationClient organization; //机构信息服务

    @Autowired
    XDictClient dictClient;

    @Autowired
    XGeographyClient addressClient;

    @Value("${spring.application.id}")
    String appId;


    /**
     * @获取患者档案基本信息
     */
    public List<Map<String, Object>> findByDemographic(String demographicId,
                                                       String name,
                                                       String telephone,
                                                       String gender,
                                                       Date birthday) throws Exception {
        //时间排序
        String query = "{";
        if (!StringUtils.isEmpty(demographicId)) {
            query += "\"q\":\"demographic_id:" + demographicId;
        }

        if (!StringUtils.isEmpty(name)) {
            query += "+AND+EHR_000004:" + name;
            if (!StringUtils.isEmpty(telephone)) {
                query += "+AND+EHR_000003:" + telephone;
            } else if (!StringUtils.isEmpty(gender) && birthday != null) {
                query += "+AND+EHR_000019:" + gender +"+AND+EHR_000007:"+DateTimeUtil.simpleDateTimeFormat(birthday);
            }
        }
        query += "\"}";
        Envelop result = resourceClient.getResources(BasisConstant.patientInfo, appId,null, query, null, null);

        return result.getDetailModelList();
    }


    // 获取数据集
    public List<Map<String, Object>> getDataSet(String core, String query) throws Exception {
        Envelop result = resourceClient.getResources(core, appId, null,query, null, null);
        return result.getDetailModelList();
    }


}
