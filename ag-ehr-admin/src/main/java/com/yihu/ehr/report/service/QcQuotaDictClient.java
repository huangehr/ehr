package com.yihu.ehr.report.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.report.MQcQuotaDict;
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
public interface QcQuotaDictClient {

    @RequestMapping(value = ServiceApi.Report.GetQcQuotaDictList, method = RequestMethod.GET)
    @ApiOperation(value = "数据统计指标列表")
    ResponseEntity<List<MQcQuotaDict>> search(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);


    @RequestMapping(value = ServiceApi.Report.QcQuotaDict, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增数据统计指标")
    MQcQuotaDict add(@RequestBody String model) ;

    @RequestMapping(value = ServiceApi.Report.QcQuotaDict, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改数据统计指标")
    MQcQuotaDict update(@RequestBody MQcQuotaDict model);

    @RequestMapping(value = ServiceApi.Report.QcQuotaDict, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除数据统计指标")
    boolean delete(@RequestParam(value = "id") String id);

}
