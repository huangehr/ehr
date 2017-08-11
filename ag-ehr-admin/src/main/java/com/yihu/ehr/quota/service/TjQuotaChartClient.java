package com.yihu.ehr.quota.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.quota.TjQuotaChart;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by janseny on 2017/8/3.
 */
@FeignClient(name= MicroServices.Patient)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface TjQuotaChartClient {


    @RequestMapping(value = ServiceApi.TJ.TjQuotaChart, method = RequestMethod.GET)
    @ApiOperation(value = "指标图表列表")
    ListResult search(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);


    @RequestMapping(value = ServiceApi.TJ.TjQuotaChart, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增/修改指标图表")
    ObjectResult add(@RequestBody String model) ;

    @RequestMapping(value = ServiceApi.TJ.BatchTjQuotaChart, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "批量新增指标图表")
    ObjectResult batchAddTjQuotaChart(@RequestBody String model) ;


    @RequestMapping(value = ServiceApi.TJ.TjQuotaChart, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除指标图表")
    Result delete(@RequestParam(value = "id") String id);

    @RequestMapping(value = ServiceApi.TJ.TjQuotaChartId, method = RequestMethod.GET)
    @ApiOperation(value = "获取指标图表信息", notes = "指标图表信息")
    TjQuotaChart getTjQuotaChart(@PathVariable(value = "id") Integer id);

    @RequestMapping(value = ServiceApi.TJ.GetAllTjQuotaChart, method = RequestMethod.GET)
    @ApiOperation(value = "获取所有选中的图表")
    ListResult getTjQuotaChartAll(
            @RequestParam(value = "filters", required = false) String filters);

}
