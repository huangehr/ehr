package com.yihu.ehr.quota.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.quota.TjQuotaDimensionSlave;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/6/9
 */
@FeignClient(name=MicroServices.Patient)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface TjQuotaDimensionSlaveClient {

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaDimensionSlaveList, method = RequestMethod.GET)
    @ApiOperation(value = "数据从维度列表关联信息")
    ListResult search(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaDimensionSlaveAll, method = RequestMethod.GET)
    @ApiOperation(value = "获取子维度子表中的所有mainCode")
    ListResult getTjQuotaDimensionSlaveAll(
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts);

    @RequestMapping(value = ServiceApi.TJ.TjQuotaDimensionSlave, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增/修改从维度关联信息")
    ObjectResult add(@RequestBody String model) ;

    @RequestMapping(value = ServiceApi.TJ.AddTjQuotaDimensionSlave, method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增从维度关联信息")
    ObjectResult addTjQuotaDimensionSlave(@RequestBody String model);

    @RequestMapping(value = ServiceApi.TJ.TjQuotaDimensionSlave, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除从维度关联信息")
    Result delete(@RequestParam(value = "id") String id);

    @RequestMapping(value = ServiceApi.TJ.DeleteSlaveByQuotaCode, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除统计指标从维度关联信息")
    Result deleteSlaveByQuotaCode(@RequestParam(value = "quotaCode") String quotaCode);

    @RequestMapping(value = ServiceApi.TJ.GetDimensionSlaveByQuotaCode, method = RequestMethod.GET)
    @ApiOperation(value = "根据指标ID获取从维度列表信息")
    List<TjQuotaDimensionSlave> getDimensionSlaveByQuotaCode(@RequestParam(value = "quotaCode") String quotaCode);
}
