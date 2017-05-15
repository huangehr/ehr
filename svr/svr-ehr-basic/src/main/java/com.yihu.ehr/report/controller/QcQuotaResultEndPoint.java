package com.yihu.ehr.report.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.report.QcQuotaResult;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.report.service.QcQuotaResultService;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by janseny on 2017/5/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "QcQuotaResult", description = "数据统计指标结果", tags = {"数据统计指标结果"})
public class QcQuotaResultEndPoint extends EnvelopRestEndPoint {

    @Autowired
    ObjectMapper objectMapper;
    
    @Autowired
    QcQuotaResultService qcQuotaResultService;

    @RequestMapping(value = ServiceApi.Report.GetQcQuotaResultList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询数据统计指标结果")
    public ListResult getQcQuotaResultList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ListResult listResult = new ListResult();
        List<QcQuotaResult> qcQuotaResultList = qcQuotaResultService.search(fields, filters, sorts, page, size);
        if(qcQuotaResultList != null){
            listResult.setDetailModelList(qcQuotaResultList);
            listResult.setTotalCount((int)qcQuotaResultService.getCount(filters));
            listResult.setCode(200);
            listResult.setCurrPage(page);
            listResult.setPageSize(size);
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }

    @RequestMapping(value = ServiceApi.Report.QcQuotaResult, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增统计指标统计结果")
    public ObjectResult add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model) throws Exception{
        QcQuotaResult obj = objectMapper.readValue(model,QcQuotaResult.class);
        obj = qcQuotaResultService.save(obj);
        return Result.success("指标统计更新成功！", obj);
    }


    @RequestMapping(value = ServiceApi.Report.QcQuotaResult, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据统计指标结果")
    public Result delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @RequestParam(value = "id") String id) throws Exception{
        qcQuotaResultService.delete(id);
        return Result.success("统计指标删除成功！");
    }

    @RequestMapping(value = ServiceApi.Report.QcQuotaList, method = RequestMethod.GET)
    @ApiOperation(value = "根据区域获取统计指标列表")
    public ListResult getQuotaByOrg(
            @ApiParam(name = "location", value = "地域", defaultValue = "")
            @RequestParam(value = "location", required = false ) String location,
            @ApiParam(name = "orgCode", value = "机构编码", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "quotaId", value = "指标ID", defaultValue = "")
            @RequestParam(value = "quotaId", required = false) String quotaId,
            @ApiParam(name = "startTime", value = "开始日期", defaultValue = "")
            @RequestParam(value = "startTime") String startTime,
            @ApiParam(name = "endTime", value = "结束日期", defaultValue = "")
            @RequestParam(value = "endTime") String endTime) throws Exception{

        ListResult result = new ListResult();
        List quotaList = new ArrayList<>();
        Date startDate = DateTimeUtil.simpleDateTimeParse(startTime);
        Date endDate = DateTimeUtil.simpleDateTimeParse(endTime);
        if(!StringUtils.isEmpty(orgCode)){
            //按机构查询整体统计结果
            quotaList = qcQuotaResultService.getQuotaListByOrgCode(orgCode, startDate, endDate);
            if(quotaList.size() > 0){
                result.setDetailModelList(quotaList);
                result.setSuccessFlg(true);
                result.setCode(200);
                return result;
            }else {
                return null;
            }
        }
        if(!StringUtils.isEmpty(quotaId)){
            //区域整体统计结果 - 按机构及指标划分，
            quotaList = qcQuotaResultService.getQuotaListByLocationGBOrg(location, Long.parseLong(quotaId),startDate, endDate);
            if(quotaList.size() > 0){
                result.setDetailModelList(quotaList);
                result.setSuccessFlg(true);
                result.setCode(200);
                return result;
            }else {
                return null;
            }
        }
        if(StringUtils.isEmpty(orgCode) && StringUtils.isEmpty(quotaId)){
            //区域整体统计结果 - 按机构及指标划分，
            quotaList = qcQuotaResultService.getQuotaListByLocation(location, startDate, endDate);
            if(quotaList.size() > 0){
                result.setDetailModelList(quotaList);
                result.setSuccessFlg(true);
                result.setCode(200);
                return result;
            }else {
                return null;
            }
        }

        return null;
    }

    @ApiOperation("质控-单项指标统计结果，按机构列表查询,初始化查询")
    @RequestMapping(value = ServiceApi.Report.GetQcQuotaOrgIntegrity, method = RequestMethod.GET)
    public ListResult queryQcQuotaOrgIntegrity(
            @ApiParam(name = "location", value = "地域", defaultValue = "")
            @RequestParam(value = "location", required = false ) String location,
            @ApiParam(name = "orgCode", value = "机构编码", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "quotaId", value = "指标ID", defaultValue = "")
            @RequestParam(value = "quotaId", required = false) String quotaId,
            @ApiParam(name = "startTime", value = "开始日期", defaultValue = "")
            @RequestParam(value = "startTime") String startTime,
            @ApiParam(name = "endTime", value = "结束日期", defaultValue = "")
            @RequestParam(value = "endTime") String endTime,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ListResult result = new ListResult();
        List<QcQuotaResult> quotaList = new ArrayList<>();
        Date startDate = DateTimeUtil.simpleDateTimeParse(startTime.toString());
        Date endDate =DateTimeUtil.simpleDateTimeParse(endTime.toString());
        QcQuotaResult qc=null;
        if(!StringUtils.isEmpty(quotaId)){
            //区域整体统计结果 - 按机构及指标划分，
            quotaList = qcQuotaResultService.getQuotaListByLocationGBOrg(location, Long.parseLong(quotaId),startDate, endDate);
            if(quotaList.size() > 0){
//                for(QcQuotaResult q:quotaList){
//                    //json处理
//                  qc=new QcQuotaResult();
//
//
//                }
                result.setDetailModelList(quotaList);
                result.setSuccessFlg(true);
                result.setCode(200);
            }else {
            }
        }
        return result;
    }

    @ApiOperation("趋势分析 - 单项指标统计结果列表查询,初始化查询")
    @RequestMapping(value = ServiceApi.Report.GetQcQuotaIntegrity, method = RequestMethod.GET)
    public ListResult queryQcQuotaIntegrity(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        ListResult listResult = new ListResult();
        List<QcQuotaResult> qcQuotaResultList = qcQuotaResultService.search(fields, filters, sorts, page, size);
        if(null!=qcQuotaResultList){
            listResult.setDetailModelList(qcQuotaResultList);
            listResult.setTotalCount((int)qcQuotaResultService.getCount(filters));
            listResult.setCode(200);
            listResult.setCurrPage(page);
            listResult.setPageSize(size);
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }

}
