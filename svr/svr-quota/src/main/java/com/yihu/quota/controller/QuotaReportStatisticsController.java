package com.yihu.quota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.dict.MapDict;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.quota.model.jpa.TjQuota;
import com.yihu.quota.service.quota.QuotaService;
import com.yihu.quota.util.BasesicUtil;
import com.yihu.quota.vo.SaveModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by janseny on 2017/12/14.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(description = "报表统计 - 统计指标 接口")
public class QuotaReportStatisticsController extends BaseController {


    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private QuotaService quotaService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping(value = ServiceApi.Resources.StatisticsGetDoctorsGroupByTown, method = RequestMethod.GET)
    @ApiOperation(value = "获取各行政区划总卫生人员", notes = "获取各行政区划总卫生人员")
    Envelop statisticsGetDoctorsGroupByTown(){
        Envelop envelop = new Envelop();
        List<MapDict> dataList = new ArrayList<>();
        String quotaCode = "HC_02_0015";
        String dimension = "town";
        try {
            TjQuota tjQuota = quotaService.findByCode(quotaCode);
            String dictSql = "SELECT id as town,name as townName  from address_dict where pid = '361100'";
            List<SaveModel> dictDatas = jdbcTemplate.query(dictSql, new BeanPropertyRowMapper(SaveModel.class));
            Map<String,String> dimensionDicMap = new HashMap<>();
            if(dictDatas != null ) {
                BasesicUtil baseUtil = new BasesicUtil();
                for (SaveModel saveModel : dictDatas) {
                    String name = baseUtil.getFieldValueByName(dimension + "Name", saveModel);
                    String val = baseUtil.getFieldValueByName(dimension, saveModel).toLowerCase();
                    dimensionDicMap.put(val,name);
                }
            }

            Map<String, Integer> resultMap = new HashMap<>();
            String filter = "year = ";
            Calendar currCal = Calendar.getInstance();
            int currentYear = currCal.get(Calendar.YEAR);
            boolean flag = true;
            while (flag){
                filter = filter + currentYear;
                resultMap = quotaService.searcherSumByGroupBySql(tjQuota, dimension, filter,"result","year","asc");
                if(resultMap != null && resultMap.size() > 0){
                    for(String key:resultMap.keySet()){
                        MapDict mapDict = new MapDict();
                        mapDict.setName(dimensionDicMap.containsKey(key)?dimensionDicMap.get(key):key);
                        mapDict.setValue(resultMap.get(key));
                        dataList.add(mapDict);
                    }
                    flag = false;
                }
                currentYear --;
                if(currentYear <=2016){
                    flag = false;
                }
            }
            envelop.setObj(objectMapper.writeValueAsString(dataList));
        } catch (Exception e) {
            e.printStackTrace();
        };
        envelop.setSuccessFlg(true);
        return envelop;
    }




















}
