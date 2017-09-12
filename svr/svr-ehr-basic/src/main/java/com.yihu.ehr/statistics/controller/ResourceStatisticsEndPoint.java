package com.yihu.ehr.statistics.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.tj.EchartReportModel;
import com.yihu.ehr.patient.service.arapply.UserCardsService;
import com.yihu.ehr.patient.service.demographic.DemographicService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/9.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "ResourceStatistics", description = "资源中心首页报表", tags = {"资源中心首页报表-入口"})
public class ResourceStatisticsEndPoint extends EnvelopRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private DemographicService demographicService;
    @Autowired
    UserCardsService userCardsService;

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaList, method = RequestMethod.GET)
    @ApiOperation(value = "全员人口个案-健康卡绑定量")
    public ListResult getTjDataSaveList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters
    ) throws Exception {

        ListResult listResult = new ListResult();

        return listResult;
    }

    @RequestMapping(value = "/getStatisticsUserCards", method = RequestMethod.GET)
    @ApiOperation(value = "获取健康卡绑定量")
    public List<EchartReportModel> getStatisticsUserCards() {
        //获取居民总数
        Map<String, Object> map=new HashedMap();
        map.put("search",null);
        map.put("province",null);
        map.put("city",null);
        map.put("district",null);
        map.put("gender",null);
        map.put("startDate",null);
        map.put("endDate",null);
       int totalDemographicsNum=demographicService.searchPatientByParamsTotalCount(map);
        //获取绑卡量 userCardsNum
        int userCardsNum= 0;
        try {
            String filters="";
             userCardsNum=(int)userCardsService.getCount(filters);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 计算未绑卡量 nonBindingCardNum、
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        int nonBindingCardNum=totalDemographicsNum - userCardsNum;
        ////获取绑卡率
        String userCardsScale = df.format((float)userCardsNum/totalDemographicsNum);//返回的是String类型
        String userCardsN=String.valueOf(userCardsNum)+"("+userCardsScale+"%)";

        // 计算未绑卡率
        String nonBindingCardScale = df.format((float)nonBindingCardNum/totalDemographicsNum);//返回的是String类型
        String nonBindingCardN=String.valueOf(nonBindingCardNum)+"("+nonBindingCardScale+"%)";
        EchartReportModel echartReportModel=new EchartReportModel();
        Map<String, String> mapData=new HashedMap();
        mapData.put("totalDemographicsNum",String.valueOf(totalDemographicsNum));
        mapData.put("userCardsNum",userCardsN);
        mapData.put("nonBindingCardNum",nonBindingCardN);
        echartReportModel.setMapData(mapData);
        List<EchartReportModel> quotaCategories = new ArrayList<>();
        quotaCategories.add(echartReportModel);
        return (List<EchartReportModel>) convertToModels(quotaCategories, new ArrayList<EchartReportModel>(quotaCategories.size()), EchartReportModel.class, null);
    }
}
