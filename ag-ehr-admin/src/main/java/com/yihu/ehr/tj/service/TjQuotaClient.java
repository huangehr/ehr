package com.yihu.ehr.tj.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.tj.TjQuota;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by Administrator on 2017/6/9.
 */
@FeignClient(name= MicroServices.Patient)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface TjQuotaClient {
    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaList , method = RequestMethod.GET)
    @ApiOperation(value = "统计列表")
    ListResult search(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.TJ.AddTjQuota, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增统计")
    ObjectResult add(@RequestBody String model) ;

    @RequestMapping(value = ServiceApi.TJ.DeleteTjQuota, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除统计")
    Result delete(@RequestParam(value = "id") Long id);

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaById, method = RequestMethod.GET)
    @ApiOperation(value = "根据ID查询指标")
    TjQuota getById(@PathVariable(value = "id") Long id);

    @RequestMapping(value = ServiceApi.TJ.TjQuotaExistsName, method = RequestMethod.GET)
    @ApiOperation(value = "校验name是否存在")
    boolean hasExistsName(@PathVariable("name") String name);

    @RequestMapping(value = ServiceApi.TJ.TjQuotaExistsCode, method = RequestMethod.GET)
    @ApiOperation(value = "校验code是否存在")
    boolean hasExistsCode(@PathVariable("code") String code);
}
