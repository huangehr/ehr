package com.yihu.ehr.report.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.util.rest.Envelop;
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

import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/6/22
 */
@FeignClient(name=MicroServices.Patient)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface QcQuotaResultClient {

    @RequestMapping(value = ServiceApi.Report.GetQcQuotaResultList, method = RequestMethod.GET)
    @ApiOperation(value = "数据统计指标结果列表")
    ListResult search(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);


    @RequestMapping(value = ServiceApi.Report.QcQuotaResult, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增数据统计指标结果")
    ObjectResult add(@RequestBody String model) ;

    @RequestMapping(value = ServiceApi.Report.QcQuotaResult, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据统计指标结果")
    Result delete(@RequestParam(value = "id") String id);

    @ApiOperation("质控-单项指标统计结果，按机构列表查询,初始化查询")
    @RequestMapping(value = ServiceApi.Report.GetQcQuotaOrgIntegrity, method = RequestMethod.GET)
    ListResult queryQcQuotaOrgIntegrity(
            @ApiParam(name = "location", value = "地域", defaultValue = "")
            @RequestParam(value = "location", required = false ) String location,
            @ApiParam(name = "orgCode", value = "机构编码", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "quotaId", value = "指标ID", defaultValue = "")
            @RequestParam(value = "quotaId", required = false) String quotaId,
            @ApiParam(name = "startTime", value = "开始日期", defaultValue = "")
            @RequestParam(value = "startTime") String startTime,
            @ApiParam(name = "endTime", value = "结束日期", defaultValue = "")
            @RequestParam(value = "endTime") String endTime);



    @ApiOperation("趋势分析 - 单项指标统计结果列表查询,初始化查询")
    @RequestMapping(value = ServiceApi.Report.GetQcQuotaIntegrity, method = RequestMethod.GET)
    ListResult queryQcQuotaIntegrity(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

}
