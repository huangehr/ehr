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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
     * 获取指标当天统计结果曲线性和柱状报表
     * @param id
     * @return
     */
    @ApiOperation(value = "获取指标统计结果曲线性和柱状报表")
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
            QuotaReport  quotaReport = quotaService.getQuotaReport(id, filters,dimension,10);
            envelop.setSuccessFlg(true);
            envelop.setObj(quotaReport);
            return envelop;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
        }
        envelop.setSuccessFlg(false);
        return envelop;
    }

    /**
     * 获取指标统计饼状结果报表
     * @param id
     * @return
     */
    @ApiOperation(value = "获取指标统计饼状结果报表")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaBreadReport, method = RequestMethod.GET)
    public Envelop getQuotaBreadReport(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id" , required = true) int id,
            @ApiParam(name = "filters", value = "检索条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "quotaDate")
            @RequestParam(value = "dimension", required = false) String dimension
    ) {
        Envelop envelop = new Envelop();
        try {
            QuotaReport  quotaReport = quotaService.getQuotaReport(id, filters,dimension,10);
            envelop.setSuccessFlg(true);
            envelop.setObj(quotaReport);
            return envelop;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
        }
        envelop.setSuccessFlg(false);
        return envelop;
    }

    @ApiOperation(value = "获取指标统计结果echart图表")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaGraphicReportPreview, method = RequestMethod.GET)
    public MChartInfoModel getQuotaGraphicReport(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id" , required = true) int id,
            @ApiParam(name = "type", value = "图表类型", defaultValue = "1")
            @RequestParam(value = "type" , required = true) int type,
            @ApiParam(name = "filter", value = "过滤", defaultValue = "")
            @RequestParam(value = "filter", required = false) String filter,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "quotaDate")
            @RequestParam(value = "dimension", required = false) String dimension
    ) {
        Envelop envelop = new Envelop();
        MChartInfoModel chartInfoModel = new MChartInfoModel();
        try {
            ReportOption reportOption = new ReportOption();
            TjQuota tjQuota = quotaService.findOne(id);

            //查询维度
            List<TjDimensionMain> mains = tjDimensionMainService.getDimensionMainByQuotaCode(tjQuota.getCode());
            List<TjDimensionSlave> slaves = tjDimensionSlaveService.getDimensionSlaveByQuotaCode(tjQuota.getCode());
            List<MReportDimension> dimesionList = new ArrayList<>();
            for(int i=0 ;i < mains.size();i++){
                String choose = "false";
                if(StringUtils.isEmpty(dimension) || dimension.equals("null") || dimension.equals("")){
                    dimension = mains.get(i).getCode();
                    if(i==0){
                        choose = "true";
                    }
                }else {
                   if( dimension.equals(mains.get(i).getCode())){
                       choose = "true";
                    }
                }
                MReportDimension mReportDimension = new MReportDimension();
                mReportDimension.setIsCheck(choose);
                mReportDimension.setName(mains.get(i).getName());
                mReportDimension.setCode(mains.get(i).getCode());
                mReportDimension.setIsMain("true");
                dimesionList.add(mReportDimension);
            }
            for(int i=0 ;i < slaves.size();i++){
                String choose = "false";
                String key = "slaveKey" + (i+1);
                if( dimension.equals(key)){
                    choose = "true";
                }
                MReportDimension mReportDimension = new MReportDimension();
                mReportDimension.setIsCheck(choose);
                mReportDimension.setName(slaves.get(i).getName());
                mReportDimension.setCode(key);
                mReportDimension.setIsMain("false");
                dimesionList.add(mReportDimension);
            }
            chartInfoModel.setListMap(dimesionList);

            List<Map<String, Object>> data2list = null;
            List<Map<String, Object>> datalist = new ArrayList<>();
            String dimensions = dimension + ";" + dimension + "Name";
            QuotaReport quotaReport = quotaService.getQuotaReportGeneral(tjQuota.getId(), filter, dimensions, 10000);
            Map<String,Object> dimensionMap = new HashMap<>();
            for(ResultModel resultModel :quotaReport.getReultModelList()){
                dimensionMap.put(resultModel.getCloumns().get(1),resultModel.getCloumns().get(0));
                Map<String, Object> map = new HashMap<>();
                map.put("NAME",resultModel.getCloumns().get(1));
                map.put("TOTAL",resultModel.getValue());
                datalist.add(map);
            }
            String title = "指标标题";
            String legend = "";
            title = tjQuota.getName();
            legend = tjQuota.getName();
            String xName = "";
            String yName = "数量";
            String barName = "";
            String bar2Name = "";
            Option option = null;
            if (type == ReportOption.bar) {
                option = reportOption.getBarEchartOption(title, legend ,xName,yName, barName, datalist, bar2Name, data2list);
            } else if (type == ReportOption.line) {
                option = reportOption.getLineEchartOption(title, legend,xName,yName, barName, datalist, bar2Name, data2list);
            } else if (type == ReportOption.pie) {
                option = reportOption.getPieEchartOption(title, legend, barName, datalist, bar2Name, data2list);
            }
            chartInfoModel.setOption(option.toString());
            chartInfoModel.setTitle(title);
            chartInfoModel.setDimensionMap(dimensionMap);
            return chartInfoModel;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
            envelop.setSuccessFlg(false);
            return null;
        }
    }


}
