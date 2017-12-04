package com.yihu.quota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.abel533.echarts.Option;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.resource.MChartInfoModel;
import com.yihu.ehr.model.resource.MReportDimension;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.quota.model.jpa.TjQuota;
import com.yihu.quota.model.jpa.dimension.TjDimensionMain;
import com.yihu.quota.model.jpa.dimension.TjDimensionSlave;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionMain;
import com.yihu.quota.model.jpa.dimension.TjQuotaDimensionSlave;
import com.yihu.quota.model.rest.QuotaReport;
import com.yihu.quota.model.rest.ResultModel;
import com.yihu.quota.service.dimension.TjDimensionMainService;
import com.yihu.quota.service.dimension.TjDimensionSlaveService;
import com.yihu.quota.service.quota.QuotaService;
import com.yihu.quota.util.ReportOption;
import com.yihu.quota.vo.SaveModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;


/**
 *
 * @author janseny
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(description = "指标统计 -指标报表控制入口")
public class QuotaReportController extends BaseController {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TjDimensionMainService tjDimensionMainService;
    @Autowired
    private TjDimensionSlaveService tjDimensionSlaveService;
    @Autowired
    private QuotaService quotaService;


    /**
     * 获取指标统计结果
     * @param id
     * @return
     */
    @ApiOperation(value = "获取指标统计结果")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaReport, method = RequestMethod.GET)
    public Envelop getQuotaReport(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id" , required = true) int id,
            @ApiParam(name = "filters", value = "检索条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "quotaDate")
            @RequestParam(value = "dimension", required = false) String dimension
    ) {
        Envelop envelop = new Envelop();
        try {
            //To DO
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
        }
        envelop.setSuccessFlg(false);
        return envelop;
    }

    @ApiOperation(value = "获取指标统计结果echart图表，支持多条组合")
    @RequestMapping(value = ServiceApi.TJ.GetMoreQuotaGraphicReportPreviews, method = RequestMethod.GET)
    public MChartInfoModel getQuotaGraphicReports(
            @ApiParam(name = "quotaIdStr", value = "指标ID,多个用,拼接", required = true)
            @RequestParam(value = "quotaIdStr" , required = true) String quotaIdStr,
            @ApiParam(name = "charstr", value = "多图表类型用,拼接,混合类型只支持柱状和线性", defaultValue = "1")
            @RequestParam(value = "charstr" , required = true) String charstr,
            @ApiParam(name = "filter", value = "过滤", defaultValue = "")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "quotaDate")
            @RequestParam(value = "dimension", required = false) String dimension,
            @ApiParam(name = "title", value = "视图名称", defaultValue = "")
            @RequestParam(value = "title", required = false) String title
    ) {
        Envelop envelop = new Envelop();
        List<String> quotaIds = Arrays.asList(quotaIdStr.split(","));
        List<String> charTypes = Arrays.asList(charstr.split(","));
        MChartInfoModel chartInfoModel = new MChartInfoModel();
        try {
            Option option = null;
            List<List<Object>> optionData = new ArrayList<>();
            List<String> lineNames = new ArrayList<>();
            Map<String,Map<String, Object>> lineData = new HashMap<>();

            for(String quotaId:quotaIds){
                Map<String, Object> datamap = new HashMap<>();
                TjQuota tjQuota = quotaService.findOne(Integer.valueOf(quotaId));
                if(tjQuota != null){
                    QuotaReport quotaReport = quotaService.getQuotaReportGeneral(tjQuota.getId(), filter, dimension+"Name", 10000);
                    for(ResultModel resultModel :quotaReport.getReultModelList()){
                        datamap.put(resultModel.getCloumns().get(0),resultModel.getValue());
                    }
                    lineNames.add(tjQuota.getName());
                    lineData.put(tjQuota.getCode(), datamap);
                }
            }
            Map<String, Object> quotaMap = new HashMap<>();
            ReportOption reportOption = new ReportOption();

            int size = 0;
            String quota = "";
            if(lineData != null && lineData.size() > 0){
                for(String key : lineData.keySet()){
                    int tempSize = lineData.get(key).size();
                    if (tempSize > size){
                        size = tempSize;
                        quota = key;
                        quotaMap = lineData.get(key);
                    }
                }

                for(String key : lineData.keySet()){
                    List<Object> dataList = new ArrayList<>();
                    Map<String,Object> valMap = lineData.get(key);
                    if(key != quota){
                        for(String name :quotaMap .keySet()){
                            if(valMap.containsKey(name)){
                                dataList.add(valMap.get(name));
                            }else {
                                dataList.add(0);
                            }
                        }
                    }else{
                        for(String name :valMap .keySet()){
                            dataList.add(valMap.get(name));
                        }
                    }
                    optionData.add(dataList);
                }
            }
            Object[] yData = (Object[])quotaMap.keySet().toArray(new Object[quotaMap.size()]);
            option = reportOption.getLineEchartOptionMoreChart(title, "", "", yData, optionData, lineNames,charTypes);

            chartInfoModel.setOption(option.toString());
            chartInfoModel.setTitle(title);
            return chartInfoModel;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
            envelop.setSuccessFlg(false);
            return null;
        }
    }


    @ApiOperation(value = "指标统计分组查询")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaGroupBy, method = RequestMethod.GET)
    public Envelop getQuotaGroupBy(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id" , required = true) int id,
            @ApiParam(name = "filters", value = "检索条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "quotaDate")
            @RequestParam(value = "dimension", required = false) String dimension
    ) {
        Envelop envelop = new Envelop();
        try {
            Map<String, Integer>  resultMap = quotaService.searcherByGroupBySql(id,dimension, filters);
            envelop.setSuccessFlg(true);
            envelop.setObj(resultMap);
            return envelop;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
        }
        envelop.setSuccessFlg(false);
        return envelop;
    }

}
