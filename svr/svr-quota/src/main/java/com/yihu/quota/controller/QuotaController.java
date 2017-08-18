package com.yihu.quota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.resource.MChartInfoModel;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.quota.model.ChartInfoModel;
import com.yihu.quota.model.rest.QuotaReport;
import com.yihu.quota.model.rest.ReultModel;
import com.yihu.quota.service.job.JobService;
import com.yihu.quota.service.quota.QuotaService;
import com.yihu.quota.util.ReportOption;
import com.yihu.quota.vo.SaveModel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.github.abel533.echarts.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 任务启动
 * @author janseny
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(description = "指标统计 -指标控制入口")
public class QuotaController extends BaseController {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private QuotaService quotaService;




    /**
     * 查询结果
     * @param id
     * @return
     */
    @ApiOperation(value = "获取指标执行结果分页")
    @RequestMapping(value = "/tj/tjGetQuotaResult", method = RequestMethod.GET)
    public Envelop getQuotaResult(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id" , required = true) int id,
            @ApiParam(name = "filters", value = "检索条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "pageNo", value = "页码", defaultValue = "0")
            @RequestParam(value = "pageNo" , required = false ,defaultValue = "0") int pageNo,
            @ApiParam(name = "pageSize", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "pageSize" , required = false ,defaultValue ="15") int pageSize
    ) {
        Envelop envelop = new Envelop();
        try {
            List<Map<String, Object>> resultList = quotaService.queryResultPage(id, filters, pageNo, pageSize);
            List<SaveModel> saveModelList = new ArrayList<SaveModel>();
            for(Map<String, Object> map : resultList){
                SaveModel saveModel =  objectMapper.convertValue(map, SaveModel.class);
                if(saveModel != null){
                    saveModelList.add(saveModel);
                }
            }
            long totalCount = quotaService.getQuotaTotalCount(id,filters);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(saveModelList);
            envelop.setCurrPage(pageNo);
            envelop.setPageSize(pageSize);
            envelop.setTotalCount((int)totalCount);
            return envelop;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
        }
        envelop.setSuccessFlg(false);
        return envelop;
    }


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
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "")
            @RequestParam(value = "dimension", required = false) String dimension
    ) {
        Envelop envelop = new Envelop();
        try {
            QuotaReport  quotaReport = quotaService.getQuotaReport(id, filters,dimension);
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
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "")
            @RequestParam(value = "dimension", required = false) String dimension
    ) {
        Envelop envelop = new Envelop();
        try {
            QuotaReport  quotaReport = quotaService.getQuotaReport(id, filters,dimension);
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
     * 获取指标统计结果总量
     * @param id
     * @return
     */
    @ApiOperation(value = "获取指标统计不同维度结果总量")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaTotalCount, method = RequestMethod.GET)
    public Envelop getQuotaTotalCount(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id" , required = true) int id,
            @ApiParam(name = "filters", value = "检索条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "dimension", value = "需要统计不同维度字段多个维度用;隔开", defaultValue = "quotaDate")
            @RequestParam(value = "dimension", required = false) String dimension
    ) {
        Envelop envelop = new Envelop();
        try {
            QuotaReport  quotaReport = quotaService.getQuotaReport(id, filters, dimension);
            envelop.setDetailModelList(quotaReport.getReultModelList());
            envelop.setSuccessFlg(true);
            return envelop;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
        }
        envelop.setSuccessFlg(false);
        return envelop;
    }


    @ApiOperation(value = "获取指标统计结果曲线性或柱状报表")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaGraphicReportPreview, method = RequestMethod.GET)
    public MChartInfoModel getQuotaGraphicReport(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id" , required = true) int id,
            @ApiParam(name = "type", value = "图表类型", defaultValue = "1")
            @RequestParam(value = "type" , required = true) int type,
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "")
            @RequestParam(value = "dimension", required = false) String dimension
    ) {
        Envelop envelop = new Envelop();
        MChartInfoModel chartInfoModel = new MChartInfoModel();
        try {
            ReportOption reportOption = new ReportOption();
            //查询指标数据
            QuotaReport  quotaReport = quotaService.getQuotaReport(id, filters,dimension);
            envelop.setSuccessFlg(true);
            envelop.setObj(quotaReport);

            String title = "指标测试标题";
            String legend = "指标测试副标题";
            if(quotaReport != null){
                title = quotaReport.getTjQuota().getName();
                legend = quotaReport.getTjQuota().getName();
                String barName = "";
                String bar2Name = "";
                List<Map<String, Object>> data2list = null;

                Option option = null;
                if (type == 1) {
                    barName = "天气 与 温度关系";
                    option = reportOption.getLineEchartOption(title, legend, barName, envelop.getDetailModelList(), 1, bar2Name, data2list);
//                    option = reportOption.getBarEchartOption(title, legend, barName, envelop.getDetailModelList(), bar2Name, data2list);
                } else if (type == 2) {
                    option = reportOption.getLineEchartOption(title, legend, barName, envelop.getDetailModelList(), 1, bar2Name, data2list);
                } else if (type == 3) {
                } else if (type == 4) {
                    option = reportOption.getPieEchartOption(title, legend, barName, envelop.getDetailModelList(), bar2Name, data2list);
                }
                chartInfoModel.setOption(objectMapper.writeValueAsString(option));
                return chartInfoModel;
            }else {
                return chartInfoModel;
            }
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
            envelop.setSuccessFlg(false);
            return chartInfoModel;
        }
    }


    @ApiOperation(value = "分组查询指标统计 支持单个字段")
    @RequestMapping(value = "/tj/tjGetQuotaResultGroup", method = RequestMethod.GET)
    public Envelop getQuotaResult(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id" , required = true) int id,
            @ApiParam(name = "aggsField", value = "分组字段", defaultValue = "")
            @RequestParam(value = "aggsField", required = false) String aggsField,
            @ApiParam(name = "filters", value = "检索条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters
    ) {
        Envelop envelop = new Envelop();
        try {
            List<Map<String, Object>> resultList = quotaService.searcherByGroup(id, filters,aggsField);
            List<SaveModel> saveModelList = new ArrayList<SaveModel>();
            for(Map<String, Object> map : resultList){
                SaveModel saveModel =  objectMapper.convertValue(map, SaveModel.class);
                if(saveModel != null){
                    saveModelList.add(saveModel);
                }
            }
            long totalCount = quotaService.getQuotaTotalCount(id,filters);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(saveModelList);
            envelop.setTotalCount((int)totalCount);
            return envelop;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
        }
        envelop.setSuccessFlg(false);
        return envelop;
    }

    @ApiOperation(value = "分组查询指标统计 支持单个及以上字段")
    @RequestMapping(value = "/tj/tjGetQuotaByGroupBySql", method = RequestMethod.GET)
    public Envelop getQuotaByGroupBySql(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id" , required = true) int id,
            @ApiParam(name = "aggsField", value = "分组字段", defaultValue = "")
            @RequestParam(value = "aggsField", required = true) String aggsField,
            @ApiParam(name = "filters", value = "检索条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters
    ) {
        Envelop envelop = new Envelop();
        List<SaveModel> saveModelList = new ArrayList<SaveModel>();
        try {
            Map<String, Integer> map = quotaService.searcherByGroupBySql(id, aggsField,filters);
            for(String str: map.keySet()){
                SaveModel saveModel =  objectMapper.convertValue(map, SaveModel.class);
                if(saveModel != null){
                    saveModelList.add(saveModel);
                }
            }
            long totalCount = quotaService.getQuotaTotalCount(id,filters);
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(saveModelList);
            envelop.setTotalCount((int)totalCount);
            return envelop;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
        }
        envelop.setSuccessFlg(false);
        return envelop;
    }





}
