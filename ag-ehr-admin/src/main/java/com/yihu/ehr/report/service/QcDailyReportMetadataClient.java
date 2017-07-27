package com.yihu.ehr.report.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.report.MQcDailyReportMetadata;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
public interface QcDailyReportMetadataClient {

    @RequestMapping(value = ServiceApi.Report.GetQcDailyReportMetadataList, method = RequestMethod.GET)
    @ApiOperation(value = "质控包数据元日报列表")
    ResponseEntity<List<MQcDailyReportMetadata>> search(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);


    @RequestMapping(value = ServiceApi.Report.QcDailyReportMetadata, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增质控包数据元日报")
    MQcDailyReportMetadata add(@RequestBody String model) ;

    @RequestMapping(value = ServiceApi.Report.QcDailyReportMetadata, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改质控包数据元日报")
    MQcDailyReportMetadata update(@RequestBody MQcDailyReportMetadata model);

    @RequestMapping(value = ServiceApi.Report.QcDailyReportMetadata, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除质控包数据元日报")
    boolean delete(@RequestParam(value = "id") String id);

    @RequestMapping(value = ServiceApi.Report.AddQcDailyMetadataDetailList, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增质控包数据元日报列表")
    boolean addDailyDatasetMetadataList(@RequestBody String models ) ;
}
