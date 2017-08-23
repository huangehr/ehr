package com.yihu.ehr.quota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.quota.*;
import com.yihu.ehr.model.tj.MTjQuotaModel;
import com.yihu.ehr.model.tj.MTjQuotaWarn;
import com.yihu.ehr.quota.service.TjDimensionMainService;
import com.yihu.ehr.quota.service.TjDimensionSlaveService;
import com.yihu.ehr.quota.service.TjQuotaDimensionMainService;
import com.yihu.ehr.quota.service.TjQuotaDimensionSlaveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by janseny on 2017/8/11.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "TjQuotaSynthesizeQueryEndPoint", description = "指标统计综合表", tags = {"统计指标管理-指标统计综合表"})
public class TjQuotaSynthesizeQueryEndPoint extends EnvelopRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    TjQuotaDimensionMainService tjQuotaDimensionMainService;
    @Autowired
    TjQuotaDimensionSlaveService tjQuotaDimensionSlaveService;
    @Autowired
    TjDimensionMainService tjDimensionMainService;
    @Autowired
    TjDimensionSlaveService tjDimensionSlaveService;


    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaSynthesiseDimension, method = RequestMethod.GET)
    @ApiOperation(value = "查询多个指标交集维度")
    public  List<Map<String,String>>  getTjQuotaSynthesiseDimension(
            @ApiParam(name = "quotaCodes", value = "指标code，多个指标用英文,分开")
            @RequestParam(value = "quotaCodes") String quotaCodes) {

        List<TjQuotaDimensionMain> tjQuotaDimensionMains = null;
        List<TjQuotaDimensionSlave> tjQuotaDimensionSlaves = null;
        //保存指标的 ID 和 所有维度的集合
        Map<String,Map<String,String>> dimensionMap = new HashedMap();
        String [] quotaCode = quotaCodes.split(",");
        for(int i=0 ; i < quotaCode.length ;i++){
            Map<String,String> map = new HashedMap();
            tjQuotaDimensionMains = tjQuotaDimensionMainService.getTjQuotaDimensionMainByCode(quotaCode[i]);
            int main = 1;
            for(TjQuotaDimensionMain tjQuotaDimensionMain : tjQuotaDimensionMains){
                TjDimensionMain tjDimensionMain = tjDimensionMainService.getTjDimensionMainByCode(tjQuotaDimensionMain.getMainCode());
               if(tjDimensionMain !=null){
                   map.put(tjDimensionMain.getCode(),tjDimensionMain.getName()+"-mainKey" + main);
               }
                main ++;
            }
            tjQuotaDimensionSlaves = tjQuotaDimensionSlaveService.getTjQuotaDimensionSlaveByCode(quotaCode[i]);

            int slave = 1;
            for(TjQuotaDimensionSlave tjQuotaDimensionSlave : tjQuotaDimensionSlaves){
                TjDimensionSlave  tjDimensionSlave =  tjDimensionSlaveService.getTjDimensionMainByCode(tjQuotaDimensionSlave.getSlaveCode());
                if(tjDimensionSlave != null){
                    map.put(tjDimensionSlave.getCode(), tjDimensionSlave.getName()+"-slaveKey" + slave);//第几个维度
                }
                slave ++;
            }
            dimensionMap.put(quotaCode[i],map);
        }

        //取出第一个指标的所有维度
        Map<String,String> tempMap = new HashedMap();
        for(String idKey:dimensionMap.keySet() ){
            Map<String,String> codeMap = dimensionMap.get(idKey);
            for(String code: codeMap.keySet()){
                tempMap.put(code, codeMap.get(code));
            }
            break;
        }

        //用于保存共同交集的指标 key 保存交集的维度code
        //value 保存 此维度在每个指标统计的结果集中对应的字段名称
        Map<String,Map<String,String>> synthesiseMap = new HashedMap();

        Map<String,String> saveModelMap = new HashedMap();
        //其他指标与第一个指标维度对比，如果在第一个指标中都存在 交集维度
        for(String tempCode:tempMap.keySet() ){
            int num = 0;
            String quotaCodeStr = "";
            for(String keyCode:dimensionMap.keySet() ){
                quotaCodeStr = keyCode;
                Map<String,String> codeMap = dimensionMap.get(keyCode);
                for(String code: codeMap.keySet()){
                    if( code.equals(tempCode)){
                        saveModelMap.put(quotaCodeStr + "-"+ tempCode ,  tempMap.get(tempCode) );
                        //指标code + 维度编码 ->  科室-slaveKey2
                        num ++;
                    }
                }
            }
            if(num == dimensionMap.size()){
                Map<String,String> modelCloumnMap = new HashedMap();
                modelCloumnMap.put("name",tempMap.get(tempCode).split("-")[0]);
                for(String keyCode:dimensionMap.keySet() ){
                    if(saveModelMap.containsKey(keyCode+"-"+ tempCode)) {
                        String str = keyCode+"-"+ tempCode;
                        if(saveModelMap.get(str).contains("mainKey")){
                            modelCloumnMap.put(keyCode,tempCode);
                        }
                        if(saveModelMap.get(str).contains("slaveKey")){
                            modelCloumnMap.put(keyCode,saveModelMap.get(str).split("-")[1]);
                        }
                    }
                }
                synthesiseMap.put(tempCode,modelCloumnMap);
            }
        }

        List<Map<String,String>> resultList = new ArrayList<>();
        for(String key : synthesiseMap.keySet()){
            resultList.add(synthesiseMap.get(key));
        }
        return  resultList;
    }



}
