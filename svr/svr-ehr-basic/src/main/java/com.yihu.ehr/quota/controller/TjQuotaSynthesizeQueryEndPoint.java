package com.yihu.ehr.quota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.quota.*;
import com.yihu.ehr.model.tj.DictModel;
import com.yihu.ehr.model.tj.SaveModel;
import com.yihu.ehr.quota.service.TjDimensionMainService;
import com.yihu.ehr.quota.service.TjDimensionSlaveService;
import com.yihu.ehr.quota.service.TjQuotaDimensionMainService;
import com.yihu.ehr.quota.service.TjQuotaDimensionSlaveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;
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
    @Autowired
    private JdbcTemplate jdbcTemplate;


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
        for(String quotaCodeKey:dimensionMap.keySet() ){
            Map<String,String> codeMap = dimensionMap.get(quotaCodeKey);
            for(String dimenCode: codeMap.keySet()){
                tempMap.put(dimenCode, codeMap.get(dimenCode));
            }
            break;
        }

        //用于保存共同交集的指标 key 保存交集的维度code
        //value 保存 此维度在每个指标统计的结果集中对应的字段名称
        Map<String,Map<String,String>> synthesiseMap = new HashedMap();

        Map<String,String> saveModelMap = new HashedMap();
        //其他指标与第一个指标维度对比，如果在第一个指标中都存在 交集维度
        for(String tempDimenCode:tempMap.keySet() ){
            int num = 0;
            String quotaCodeStr = "";
            for(String keyCode:dimensionMap.keySet() ){
                quotaCodeStr = keyCode;
                Map<String,String> codeMap = dimensionMap.get(keyCode);
                for(String code: codeMap.keySet()){
                    if( code.equals(tempDimenCode) &&  tempMap.get(tempDimenCode).equals(codeMap.get(code))){
                        saveModelMap.put(quotaCodeStr + "-"+ tempDimenCode ,  tempMap.get(tempDimenCode) );
                        //指标code + 维度编码 ->  科室-slaveKey2
                        num ++;
                    }
                }
            }
            if(num == dimensionMap.size()){
                Map<String,String> modelCloumnMap = new HashedMap();
                modelCloumnMap.put("name",tempMap.get(tempDimenCode).split("-")[0]);
                for(String keyCode:dimensionMap.keySet() ){
                    if(saveModelMap.containsKey(keyCode+"-"+ tempDimenCode)) {
                        String str = keyCode+"-"+ tempDimenCode;
                        if(saveModelMap.get(str).contains("mainKey")){
                            modelCloumnMap.put(keyCode,tempDimenCode);
                        }
                        if(saveModelMap.get(str).contains("slaveKey")){
                            modelCloumnMap.put(keyCode,saveModelMap.get(str).split("-")[1]);
                        }
                    }
                }
                synthesiseMap.put(tempDimenCode,modelCloumnMap);
            }
        }

        List<Map<String,String>> resultList = new ArrayList<>();
        for(String key : synthesiseMap.keySet()){
            resultList.add(synthesiseMap.get(key));
        }
        return  resultList;
    }


    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaSynthesiseDimensionKeyVal, method = RequestMethod.GET)
    @ApiOperation(value = "查询多个指标交集维度的字典项")
    public  Map<String,Map<String,String>>  getTjQuotaSynthesiseDimensionKeyVal(
            @ApiParam(name = "quotaCode", value = "指标code多个指标其中一个")
            @RequestParam(value = "quotaCode") String quotaCode,
            @ApiParam(name = "dimensions", value = "维度编码，多个维度用英文,分开")
            @RequestParam(value = "dimensions") String dimensions) {

        List<TjQuotaDimensionMain> tjQuotaDimensionMains = null;
        List<TjQuotaDimensionSlave> tjQuotaDimensionSlaves = null;
        String [] dimensionArr = dimensions.split(",");
        Map<String, Map<String,String>> resultMap = new HashedMap();
        tjQuotaDimensionMains = tjQuotaDimensionMainService.getTjQuotaDimensionMainByCode(quotaCode);
        if(tjQuotaDimensionMains != null){
            for(TjQuotaDimensionMain tjQuotaDimensionMain : tjQuotaDimensionMains){
                for(int i=0 ;i < dimensionArr.length ; i++){
                    if(dimensionArr[i].equals(tjQuotaDimensionMain.getMainCode())){
                        Map<String, String> map = new HashedMap();
                        //查询字典数据
                        List<SaveModel> dictData = jdbcTemplate.query(tjQuotaDimensionMain.getDictSql(), new BeanPropertyRowMapper(SaveModel.class));
                        if(dictData != null ){
                            for(SaveModel saveModel :dictData){
                                String name = getFieldValueByName(tjQuotaDimensionMain.getMainCode()+"Name",saveModel).toString();
                                String val = getFieldValueByName(tjQuotaDimensionMain.getMainCode(),saveModel).toString();
                                map.put(name,val);
                            }
                            resultMap.put(tjQuotaDimensionMain.getMainCode(),map);
                        }
                    }
                }
            }
        }

        tjQuotaDimensionSlaves = tjQuotaDimensionSlaveService.getTjQuotaDimensionSlaveByCode(quotaCode);
        if(tjQuotaDimensionSlaves != null){
            for(TjQuotaDimensionSlave tjQuotaDimensionSlave : tjQuotaDimensionSlaves){
                for(int i=0 ;i < dimensionArr.length ; i++){
                    String slave = "slaveKey"+(i+1);
                    if( dimensionArr[i].equals(slave) ){
                        Map<String, String> map = new HashedMap();
                        //查询字典数据
                        List<DictModel> dictDataList = jdbcTemplate.query(tjQuotaDimensionSlave.getDictSql(), new BeanPropertyRowMapper(DictModel.class));
                        if(dictDataList != null ){
                            for(DictModel dictModel :dictDataList){
                                String name = getFieldValueByName(slave+"Name",dictModel).toString();
                                String val = getFieldValueByName(slave,dictModel).toString();
                                map.put(name,val);
                            }
                            resultMap.put(tjQuotaDimensionSlave.getSlaveCode(),map);
                        }
                    }
                }
            }
        }
        return resultMap;
    }


    /**
     * 根据属性名获取属性值
     * */
    private Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[] {});
            Object value = method.invoke(o, new Object[] {});
            return value;
        } catch (Exception e) {
            return null;
        }
    }


}
