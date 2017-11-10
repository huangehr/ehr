package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.resource.MRsMonitorTypeReport;
import com.yihu.ehr.resource.model.RsMonitorTypeReport;
import com.yihu.ehr.resource.service.RsMonitorTypeReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by jansny 2017年11月8日15:17:24
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "资源监测分类配置报表服务接口", description = "资源报表-资源监测分类配置报表服务接口")
public class RsMonitorTypeReportEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private RsMonitorTypeReportService rsMonitorTypeReportService;

    @RequestMapping(value = ServiceApi.Resources.RsMonitorTypeReport,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为资源监测类型配置报表，单个")
    MRsMonitorTypeReport createMonitorTypeReport(
            @ApiParam(name = "data_json",value = "角色组-机构关系Json串")
            @RequestBody String dataJson){
        RsMonitorTypeReport rsMonitorTypeReport = toEntity(dataJson,RsMonitorTypeReport.class);
        rsMonitorTypeReport = rsMonitorTypeReportService.save(rsMonitorTypeReport);
        return convertToModel(rsMonitorTypeReport,MRsMonitorTypeReport.class);
    }

    @RequestMapping(value = ServiceApi.Resources.RsMonitorTypeReport,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据监测类型id,删除资源监测类型报表")
    boolean deleteMonitorTypeReport(
            @ApiParam(name = "reportId",value = "报表ID")
            @RequestParam(value = "reportId") String reportId,
            @ApiParam(name = "monitorTypeId",value = "监测类型ID")
            @RequestParam(value = "monitorTypeId") String monitorTypeId){
        RsMonitorTypeReport rsMonitorTypeReport = rsMonitorTypeReportService.findRelation(reportId,monitorTypeId);
        if(null != rsMonitorTypeReport){
            rsMonitorTypeReportService.deleteRsMonitorTypeReport(rsMonitorTypeReport);
        }
        return true;
    }


    @RequestMapping(value = ServiceApi.Resources.RsMonitorTypeReports,method = RequestMethod.GET)
    @ApiOperation(value = "查询资源监测类型报表列表---分页")
    Collection<MRsMonitorTypeReport> searchRsMonitorTypeReports(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request, HttpServletResponse response) throws Exception{
        List<RsMonitorTypeReport> rsMonitorTypeReportList = rsMonitorTypeReportService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, rsMonitorTypeReportService.getCount(filters), page, size);
        return convertToModels(rsMonitorTypeReportList, new ArrayList<MRsMonitorTypeReport>(rsMonitorTypeReportList.size()), MRsMonitorTypeReport.class,fields);
    }

    @RequestMapping(value = ServiceApi.Resources.RsMonitorTypeReportsNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询资源监测类型报表列表---不分页")
    Collection<MRsMonitorTypeReport> searchRsMonitorTypeReportsNoPage(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        List<RsMonitorTypeReport> rsMonitorTypeReportList = rsMonitorTypeReportService.search(filters);
        return convertToModels(rsMonitorTypeReportList,new ArrayList<MRsMonitorTypeReport>(rsMonitorTypeReportList.size()),MRsMonitorTypeReport.class,"");
    }

}
