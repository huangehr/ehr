package com.yihu.quota.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
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
    @ApiOperation(value = "获取指标当天统计结果曲线性和柱状报表")
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
    @ApiOperation(value = "获取指标统计结果总量")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaTotalCount, method = RequestMethod.GET)
    public Envelop getQuotaTotalCount(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id" , required = true) int id,
            @ApiParam(name = "filters", value = "检索条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "")
            @RequestParam(value = "dimension", required = false) String dimension

            //如何根据不同维度 统计总量
    ) {
        Envelop envelop = new Envelop();
        try {
            int  count = 0;
            QuotaReport quotaReport = quotaService.getQuotaReport(id, filters,dimension);
            if(quotaReport.getReultModelList() != null){
                for(ReultModel reultModel:quotaReport.getReultModelList()){
                    count = Integer.valueOf(reultModel.getValue().toString()) + count;
                }
            }
            envelop.setTotalCount(count);
            envelop.setObj(quotaReport.getTjQuota());
            envelop.setSuccessFlg(true);
            envelop.setObj(count);
            return envelop;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
        }
        envelop.setSuccessFlg(false);
        return envelop;
    }

    @ApiOperation(value = "获取指标当天统计结果曲线性或柱状报表")
    @RequestMapping(value = ServiceApi.TJ.GetQuotaGraphicReport, method = RequestMethod.GET)
    public ChartInfoModel getQuotaGraphicReport(
            @ApiParam(name = "id", value = "指标任务ID", required = true)
            @RequestParam(value = "id" , required = true) int id,
            @ApiParam(name = "type", value = "图表类型", defaultValue = "1")
            @RequestParam(value = "type" , required = true) int type,
            @ApiParam(name = "filters", value = "图表类型", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "dimension", value = "维度字段", defaultValue = "")
            @RequestParam(value = "dimension", required = false) String dimension
    ) {
        Envelop envelop = new Envelop();
        ChartInfoModel chartInfoModel = new ChartInfoModel();
        try {
           /* ReportOption reportOption = new ReportOption();
            QuotaReport  quotaReport = quotaService.getQuotaReport(id, filters,dimension);
            envelop.setSuccessFlg(true);
            envelop.setObj(quotaReport);
            if (type == 1) {
                chartInfoModel.setTitle("柱状图");
                String barEchart = reportOption.getBarEchartOption("柱状图", "", "", envelop.getDetailModelList(), "", null);
                chartInfoModel.setChartJson(barEchart);
            } else if (type == 2) {
                chartInfoModel.setTitle("折线图");
                String lineEchart = reportOption.getLineEchartOption("折线图", "", "", envelop.getDetailModelList(), 1, "", null);
                chartInfoModel.setChartJson(lineEchart);
            } else if (type == 3) {
                chartInfoModel.setTitle("曲线图");

            } else {
                chartInfoModel.setTitle("饼状图");
                String pieEchart = reportOption.getPieEchartOption("饼状图", "", "", envelop.getDetailModelList(), "", null);
                chartInfoModel.setChartJson(pieEchart);
            }*/

            return chartInfoModel;
        } catch (Exception e) {
            error(e);
            invalidUserException(e, -1, "查询失败:" + e.getMessage());
            envelop.setSuccessFlg(false);
            return chartInfoModel;
        }

    }
}
