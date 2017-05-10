package com.yihu.ehr.report.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.report.MQcQuotaResult;
import io.swagger.annotations.ApiOperation;
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
    ResponseEntity<List<MQcQuotaResult>> search(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);


    @RequestMapping(value = ServiceApi.Report.QcQuotaResult, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增数据统计指标结果")
    MQcQuotaResult add(@RequestBody String model) ;

    @RequestMapping(value = ServiceApi.Report.QcQuotaResult, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改数据统计指标结果")
    MQcQuotaResult update(@RequestBody MQcQuotaResult model);

    @RequestMapping(value = ServiceApi.Report.QcQuotaResult, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据统计指标结果")
    boolean delete(@RequestParam(value = "id") String id);

}
