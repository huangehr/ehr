package com.yihu.ehr.report.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.agModel.report.QcDailyEventDetailModel;
import com.yihu.ehr.agModel.report.QcDailyEventsModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.report.QcDailyReport;
import com.yihu.ehr.entity.report.QcDailyReportDetail;
import com.yihu.ehr.model.report.MQcDailyReport;
import com.yihu.ehr.model.report.MQcDailyReportDetail;
import com.yihu.ehr.report.service.QcDailyReportClient;
import com.yihu.ehr.util.FeignExceptionUtils;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author janseny
 * @version 1.0
 * @created 2017.5.9
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "QcDailyReport", description = "质控包数据完整性日报", tags = {"报表管理-质控包数据完整性日报"})
public class QcDailyReportController extends ExtendController<QcDailyReport> {

    @Autowired
    QcDailyReportClient qcDailyReportClient;


    @RequestMapping(value = ServiceApi.Report.GetQcDailyReportList, method = RequestMethod.GET)
    @ApiOperation(value = "质控包数据完整性日报列表")
    public Envelop search(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page){

        ResponseEntity<List<MQcDailyReport>> responseEntity = qcDailyReportClient.search(fields, filters, sorts, size, page);
        return getResult(responseEntity.getBody(), getTotalCount(responseEntity), page, size);
    }


    @RequestMapping(value = ServiceApi.Report.QcDailyReport, method = RequestMethod.POST)
    @ApiOperation(value = "新增质控包数据完整性日报")
    public Envelop add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            return success(qcDailyReportClient.add(model) );
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }


    @RequestMapping(value = ServiceApi.Report.QcDailyReport, method = RequestMethod.PUT)
    @ApiOperation(value = "修改质控包数据完整性日报")
    public Envelop update(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            MQcDailyReport qcDailyReportDatasets = convertToMModel(model, MQcDailyReport.class);

            if( StringUtils.isNotEmpty(qcDailyReportDatasets.getId()) )
                return failed("编号不能为空");
            return success(qcDailyReportClient.update(qcDailyReportDatasets) );
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.Report.QcDailyReport, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除质控包数据完整性日报")
    public Envelop delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") String id) {
        try {
            qcDailyReportClient.delete(id);
            return success("");
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.Report.QcDailyReport, method = RequestMethod.GET)
    @ApiOperation(value = "获取质控包数据完整性日报信息")
    public Envelop getInfo(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") String id) {
        try {
            MQcDailyReport qcDailyReportDatasets = qcDailyReportClient.getInfo(id);
            if(qcDailyReportDatasets == null)
                return failed("没有找到该质控包数据完整性日报信息！");
            return success(qcDailyReportDatasets);
        }catch (Exception e){
            e.printStackTrace();
            return failed("获取信息出错！");
        }
    }

    @RequestMapping(value = ServiceApi.Report.GetQcDailyReportPageList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件数据完整性详细分页列表")
    public Envelop getQcDailyReportPageList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page){

        ResponseEntity<List<MQcDailyReportDetail>> responseEntity = qcDailyReportClient.getQcDailyReportPageList(fields, filters, sorts, size, page);
        return getResult(responseEntity.getBody(), getTotalCount(responseEntity), page, size);
    }

}
