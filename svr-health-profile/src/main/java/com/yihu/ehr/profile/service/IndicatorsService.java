package com.yihu.ehr.profile.service;

import com.yihu.ehr.model.specialdict.MIndicatorsDict;
import com.yihu.ehr.profile.feign.XDictClient;
import com.yihu.ehr.profile.feign.XResourceClient;
import com.yihu.ehr.util.rest.Envelop;
import org.apache.hadoop.hbase.shaded.org.codehaus.jackson.map.ObjectMapper;
import org.apache.hadoop.hbase.shaded.org.codehaus.jackson.type.JavaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hzp
 * @created 2016.06.27 13:49
 * 指标服务
 */
@Service
public class IndicatorsService {

    @Autowired
    XDictClient dictService;

    @Autowired
    XResourceClient resource; //资源服务

    String appId = "svr-health-profile";

    /**
     * 获取某个健康问题指标
     */
    public List<Map<String,Object>> getIndicatorsClass(String demographicId,String hpCode,String indicatorType) throws  Exception{
        List<Map<String,Object>> re = new ArrayList<>();
        //某个健康问题所有指标
        List<MIndicatorsDict> indicatorsList = dictService.getIndicatorsByHpCode(hpCode);
        if(indicatorsList!=null &&indicatorsList.size()>0)
        {
            String sql = "select distinct ETL_INDICATORS_TYPE,ETL_INDICATORS_CODE,ETL_INDICATORS_NAME from ETL_INDICATORS where demographic_id='"+demographicId+"'";
            if(indicatorType!=null && indicatorType.length()>0)
            {
                sql+=" AND ETL_INDICATORS_TYPE = '"+indicatorType+"'";
            }
            sql +=" AND ETL_INDICATORS_CODE in (";
            for(MIndicatorsDict indicator :indicatorsList)
            {
                sql += "'"+indicator.getCode()+"',";
            }
            sql = sql.substring(0,sql.length()-1)+")";
            //获取某个健康问题指标
            Envelop result = resource.getResources(BasisConstant.healthIndicators,appId,sql,null,null);
            if(result.getDetailModelList()!=null&&result.getDetailModelList().size()>0)
            {
                for(int i=0;i<result.getDetailModelList().size();i++)
                {
                    Map<String,Object> resultItem = (Map<String,Object>)result.getDetailModelList().get(i);
                    Map<String,Object> item = new HashMap<>();
                    item.put("type",resultItem.get("ETL_INDICATORS_TYPE"));
                    item.put("code",resultItem.get("ETL_INDICATORS_CODE"));

                    MIndicatorsDict detailInformationFromCode = dictService.getIndicatorsDictByCode(resultItem.get("ETL_INDICATORS_CODE").toString());
                    item.put("unit",detailInformationFromCode.getUnit());
                    item.put("upper_limit",detailInformationFromCode.getUpperLimit());
                    item.put("lower_limit",detailInformationFromCode.getLowerLimit());
                    item.put("description",detailInformationFromCode.getDescription());
                    item.put("name",resultItem.get("ETL_INDICATORS_NAME"));
                    re.add(item);
                }
            }

        }
        return re;
    }

    /**
     * 获取指标数据
     */
    public Envelop getIndicatorsData(String demographicId,String indicatorCode,String dateFrom,String dateEnd,Integer page,Integer size) throws  Exception{
        Envelop re = new Envelop();
        String sql = "select * from ETL_INDICATORS where demographic_id='"+demographicId+"'";
        //指标代码
        if(indicatorCode!=null && indicatorCode.length()>0)
        {
            sql += " AND ETL_INDICATORS_CODE = '"+indicatorCode+"'";
        }
        //开始时间
        if(dateFrom!=null && dateFrom.length()>0)
        {
            sql += " AND ETL_INDICATORS_TIME > '"+dateFrom+"'";
        }
        //结束时间
        if(dateEnd!=null && dateEnd.length()>0)
        {
            sql += " AND ETL_INDICATORS_TIME < '"+dateEnd+"'";
        }

        //获取指标数据

        Envelop e= resource.getResources(BasisConstant.healthIndicators,appId,sql,page,size);
        for(int i=0;i<e.getDetailModelList().size();i++){
            Map<String, String> map=(Map<String, String>)(e.getDetailModelList().get(i));
            MIndicatorsDict detailInformationFromCode = dictService.getIndicatorsDictByCode(map.get("ETL_INDICATORS_CODE"));
            map.put("unit", detailInformationFromCode.getUnit());
            map.put("upper_limit", detailInformationFromCode.getUpperLimit());
            map.put("lower_limit", detailInformationFromCode.getLowerLimit());
            map.put("description", detailInformationFromCode.getDescription());
        }
        return e;
    }

}