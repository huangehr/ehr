package com.yihu.ehr.report.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.report.QcQuotaResult;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.report.service.QcQuotaResultClient;
import com.yihu.ehr.util.FeignExceptionUtils;
import com.yihu.ehr.util.rest.Envelop;
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
 * @author janseny
 * @version 1.0
 * @created 2017.5.9
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api( value = "QcQuotaResult", description = "数据统计指标结果", tags = {"报表管理-数据统计指标结果"})
public class QcQuotaResultController extends ExtendController<QcQuotaResult> {

    @Autowired
    QcQuotaResultClient qcQuotaResultClient;


    @RequestMapping(value = ServiceApi.Report.GetQcQuotaResultList, method = RequestMethod.GET)
    @ApiOperation(value = "数据统计指标结果列表")
    public Envelop search(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "quota_id='1'")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page){

        ListResult listResult = qcQuotaResultClient.search(fields, filters, sorts, size, page);
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> list = listResult.getDetailModelList();
           /* list = convertArApplyModels(list);*/
            return getResult(list, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else{
            Envelop envelop = new Envelop();
            return envelop;
        }
    }

    @RequestMapping(value = ServiceApi.Report.QcQuotaResult, method = RequestMethod.POST)
    @ApiOperation(value = "新增数据统计指标结果")
    public Envelop add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam("model") String model) {
        try {
            ObjectResult objectResult = qcQuotaResultClient.add(model);
            if (objectResult.getCode() == 200) {
                return successObj(objectResult.getData());
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.Report.QcQuotaResult, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据统计指标结果")
    public Envelop delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") String id) {
        try {
            Result result = qcQuotaResultClient.delete(id);
            if(result.getCode() == 200){
                return successMsg(result.getMessage());
            }else{
                return failed("统计指标删除失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @ApiOperation("趋势分析 -按机构列表查询,初始化查询")
    @RequestMapping(value = ServiceApi.Report.GetQcQuotaOrgIntegrity, method = RequestMethod.GET)
    public Envelop queryQcQuotaOrgIntegrity(
            @ApiParam(name = "location", value = "地域", defaultValue = "")
            @RequestParam(value = "location", required = false ) String location,
            @ApiParam(name = "orgCode", value = "机构编码", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "quotaId", value = "指标ID", defaultValue = "")
            @RequestParam(value = "quotaId", required = false) String quotaId,
            @ApiParam(name = "startTime", value = "开始日期", defaultValue = "")
            @RequestParam(value = "startTime") String startTime,
            @ApiParam(name = "endTime", value = "结束日期", defaultValue = "")
            @RequestParam(value = "endTime") String endTime) throws Exception {
        ListResult  listResult =qcQuotaResultClient.queryQcQuotaOrgIntegrity(orgCode,quotaId,startTime,endTime);
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> list = listResult.getDetailModelList();
            return getResult(list, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else {
            Envelop envelop = new Envelop();
            return envelop;
        }
    }


    @ApiOperation("趋势分析 - 按区域列表查询,初始化查询")
    @RequestMapping(value = ServiceApi.Report.GetQcQuotaIntegrity, method = RequestMethod.GET)
    public Envelop queryQcQuotaIntegrity(
            @ApiParam(name = "location", value = "地域", defaultValue = "")
            @RequestParam(value = "location", required = false ) String location,
            @ApiParam(name = "quotaId", value = "指标ID", defaultValue = "")
            @RequestParam(value = "quotaId", required = false) String quotaId,
            @ApiParam(name = "startTime", value = "开始日期", defaultValue = "")
            @RequestParam(value = "startTime") String startTime,
            @ApiParam(name = "endTime", value = "结束日期", defaultValue = "")
            @RequestParam(value = "endTime") String endTime) throws Exception {
        ListResult  listResult =qcQuotaResultClient.queryQcQuotaIntegrity(location,quotaId,startTime,endTime);
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> list = listResult.getDetailModelList();
            return getResult(list, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else {
            Envelop envelop = new Envelop();
            return envelop;
        }
    }



    @ApiOperation("根据机构查询所有指标统计结果,初始化查询")
    @RequestMapping(value = ServiceApi.Report.GetQcOverAllIntegrity, method = RequestMethod.GET)
    public Envelop queryQcOverAllIntegrity(
            @ApiParam(name = "location", value = "地域", defaultValue = "")
            @RequestParam(value = "location", required = false ) String location,
            @ApiParam(name = "startTime", value = "开始日期", defaultValue = "")
            @RequestParam(value = "startTime") String startTime,
            @ApiParam(name = "endTime", value = "结束日期", defaultValue = "")
            @RequestParam(value = "endTime") String endTime) throws Exception {
        ListResult  listResult =qcQuotaResultClient.queryQcOverAllIntegrity(location,startTime,endTime);
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> list = listResult.getDetailModelList();
            return getResult(list, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else {
            Envelop envelop = new Envelop();
            return envelop;
        }
    }


    @ApiOperation("根据机构查询所有指标统计结果,初始化查询")
    @RequestMapping(value = ServiceApi.Report.GetQcOverAllOrgIntegrity, method = RequestMethod.GET)
    public Envelop queryQcOverAllOrgIntegrity(
            @ApiParam(name = "location", value = "地域", defaultValue = "")
            @RequestParam(value = "location", required = false ) String location,
            @ApiParam(name = "orgCode", value = "机构编码", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "startTime", value = "开始日期", defaultValue = "")
            @RequestParam(value = "startTime") String startTime,
            @ApiParam(name = "endTime", value = "结束日期", defaultValue = "")
            @RequestParam(value = "endTime") String endTime) throws Exception {
        ListResult  listResult =qcQuotaResultClient.queryQcOverAllOrgIntegrity(location,orgCode,startTime,endTime);
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> list = listResult.getDetailModelList();
            return getResult(list, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else {
            Envelop envelop = new Envelop();
            return envelop;
        }
    }

    @ApiOperation("分析明细列表")
    @RequestMapping(value = ServiceApi.Report.GetQcQuotaDailyIntegrity, method = RequestMethod.GET)
    public Envelop queryQcQuotaOrgIntegrity(
            @ApiParam(name = "location", value = "地域", defaultValue = "")
            @RequestParam(value = "location", required = false ) String location,
            @ApiParam(name = "quotaId", value = "指标ID", defaultValue = "")
            @RequestParam(value = "quotaId", required = false) String quotaId,
            @ApiParam(name = "startTime", value = "开始日期", defaultValue = "")
            @RequestParam(value = "startTime") String startTime,
            @ApiParam(name = "endTime", value = "结束日期", defaultValue = "")
            @RequestParam(value = "endTime") String endTime) throws Exception {
        ListResult  listResult =qcQuotaResultClient.queryQcQuotaDailyIntegrity(location,quotaId,startTime,endTime);
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> list = listResult.getDetailModelList();
            return getResult(list, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else {
            Envelop envelop = new Envelop();
            return envelop;
        }
    }

    @ApiOperation("根据地区、期间查询某项指标的值")
    @RequestMapping(value = ServiceApi.Report.GetQcQuotaByLocation, method = RequestMethod.GET)
    public Envelop queryQcQuotaByLocation(
            @ApiParam(name = "location", value = "地域", defaultValue = "")
            @RequestParam(value = "location", required = false ) String location,
            @ApiParam(name = "quotaId", value = "指标ID", defaultValue = "")
            @RequestParam(value = "quotaId", required = false) String quotaId,
            @ApiParam(name = "startTime", value = "开始日期", defaultValue = "")
            @RequestParam(value = "startTime") String startTime,
            @ApiParam(name = "endTime", value = "结束日期", defaultValue = "")
            @RequestParam(value = "endTime") String endTime) throws Exception {
        ListResult  listResult =qcQuotaResultClient.queryQcQuotaByLocation(location,quotaId,startTime,endTime);
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> list = listResult.getDetailModelList();
            return getResult(list, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else {
            Envelop envelop = new Envelop();
            return envelop;
        }
    }

}
