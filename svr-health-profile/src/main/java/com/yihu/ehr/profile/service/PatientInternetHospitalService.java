package com.yihu.ehr.profile.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.model.resource.MStdTransformDto;
import com.yihu.ehr.profile.feign.XResourceClient;
import com.yihu.ehr.profile.feign.XTransformClient;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.util.string.StringBuilderEx;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lyr on 2016/6/13.
 */
@Service
public class PatientInternetHospitalService {

    @Autowired
    XResourceClient resource;

    @Autowired
    XTransformClient transform;

    @Autowired
    ObjectMapper mapper;

    private static String APP_ID = "svr-health-profile";

    /**
     * 获取EHR资源并按版本转换标准代码
     * @param resource_code 资源代码
     * @param query 查询参数
     * @param isSub 是否细表资源
     * @param version 标准版本
     * @return List<Map<String,Object>>
     * @throws Exception
     */
    public List<Map<String,Object>> getEhrResourceVersion(String resource_code,String query,boolean isSub,String version) throws Exception
    {
        //获取EHR资源
        List<Map<String,Object>> result = getEhrResource(resource_code,query,isSub);

        if(result != null && result.size() > 0)
        {
            if(!StringUtils.isBlank(version))
            {   //按版本转换标准代码
                MStdTransformDto stdTransformDto = new MStdTransformDto();
                stdTransformDto.setSource(mapper.writeValueAsString(result));
                stdTransformDto.setVersion(version);

                return transform.stdTransformList(mapper.writeValueAsString(stdTransformDto));
            }
            else
            {   //version为空时，直接返回
                return result;
            }
        }
        else
        {
           return new ArrayList<Map<String, Object>>();
        }
    }


    /**
     * 获取EHR资源
     * @param resource_code 资源代码
     * @param query 查询参数
     * @param isSub 是否细表资源
     * @return List<Map<String,Object>>
     * @throws Exception
     */
    public List<Map<String,Object>> getEhrResource(String resource_code,String query,boolean isSub) throws Exception
    {
        if(isSub)
        {
            //根据查询参数查询主表
            Envelop result = resource.getResources(BasisConstant.patientEvent,APP_ID,query,1,1000);
            //主表rowkey条件
            StringBuilder rowkeys = new StringBuilder();

            if(result.getDetailModelList() != null && result.getDetailModelList().size() > 0)
            {
                //获取主表rowkey组合条件
                for(Map<String,Object> map : (List<Map<String,Object>>)result.getDetailModelList())
                {
                    if(rowkeys.length() > 0)
                    {
                        rowkeys.append(" OR ");
                    }
                    rowkeys.append("profile_id:" + (String)map.get("rowkey"));
                }

                //根据主表rowkey查询细表资源
                Envelop resultSub = resource.getResources(resource_code,APP_ID ,URLEncoder.encode("{\"q\":\"(" + rowkeys.toString() + ")\"}"),1,1000);

                if(resultSub.getDetailModelList() != null && resultSub.getDetailModelList().size() > 0)
                {
                    return resultSub.getDetailModelList();
                }
            }
        }
        else
        {
            //查询主表资源
            Envelop result = resource.getResources(resource_code,APP_ID,query,1,1000);

            if(result.getDetailModelList() != null && result.getDetailModelList().size() > 0)
            {
                return result.getDetailModelList();
            }
        }

        return new ArrayList<Map<String,Object>>();
    }

}
