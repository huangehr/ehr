package com.yihu.ehr.resource.client;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.resource.MRsMonitorTypeReport;
import com.yihu.ehr.model.user.MRoleOrg;
import com.yihu.ehr.model.user.MRoleUser;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

/**
 * Created by yww on 2016/7/8.
 */
@FeignClient(name = MicroServices.Resource)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface RsMonitorTypeReportClient {

    @RequestMapping(value = ServiceApi.Resources.RsMonitorTypeReport,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为资源监测类型配置报表，单个")
    MRsMonitorTypeReport createMonitorTypeReport(
            @ApiParam(name = "data_json", value = "资源监测类型-报表Json串")
            @RequestBody String dataJson);

    @RequestMapping(value = ServiceApi.Resources.RsMonitorTypeReport,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据监测类型id,删除资源监测类型报表")
    boolean deleteMonitorTypeReport(
            @ApiParam(name = "reportId",value = "报表ID")
            @RequestParam(value = "reportId") String reportId,
            @ApiParam(name = "monitorTypeId",value = "监测类型ID")
            @RequestParam(value = "monitorTypeId") String monitorTypeId);


    @RequestMapping(value = ServiceApi.Resources.RsMonitorTypeReports,method = RequestMethod.GET)
    @ApiOperation(value = "查询资源监测类型报表列表---分页")
    ResponseEntity<Collection<MRsMonitorTypeReport>> searchRsMonitorTypeReports(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.Resources.RsMonitorTypeReportsNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询资源监测类型报表列表---不分页")
    Collection<MRsMonitorTypeReport> searchRsMonitorTypeReportsNoPage(
            @ApiParam(name = "filters", value = "过滤条件，为空检索全部", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters);
}
