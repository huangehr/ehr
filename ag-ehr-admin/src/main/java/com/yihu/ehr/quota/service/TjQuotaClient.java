package com.yihu.ehr.quota.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.tj.MTjQuotaModel;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * Created by Administrator on 2017/6/9.
 */
@FeignClient(name= MicroServices.Patient)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface TjQuotaClient {
    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaList , method = RequestMethod.GET)
    @ApiOperation(value = "统计指标列表")
    ListResult search(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.TJ.AddTjQuota, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增统计指标")
    ObjectResult add(@RequestBody String model) ;

    @RequestMapping(value = ServiceApi.TJ.UpdateTjQuota, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改统计指标")
    Result update(@RequestBody String model) ;

    @RequestMapping(value = ServiceApi.TJ.DeleteTjQuota, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除统计指标")
    Result delete(@RequestParam(value = "id") Long id);

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaById, method = RequestMethod.GET)
    @ApiOperation(value = "根据ID查询指标")
    MTjQuotaModel getById(@PathVariable(value = "id") Long id);

    @RequestMapping(value = ServiceApi.TJ.TjQuotaExistsName, method = RequestMethod.GET)
    @ApiOperation(value = "校验name是否存在")
    boolean hasExistsName(@PathVariable("name") String name);

    @RequestMapping(value = ServiceApi.TJ.TjQuotaExistsCode, method = RequestMethod.GET)
    @ApiOperation(value = "校验code是否存在")
    boolean hasExistsCode(@PathVariable("code") String code);

    @RequestMapping(value = ServiceApi.TJ.TjHasConfigDimension, method = RequestMethod.GET)
    @ApiOperation(value = "校验code是否存在")
    boolean hasConfigDimension(
            @ApiParam(name = "quotaCode", value = "指标编码")
            @RequestParam(value = "quotaCode") String quotaCode);

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaByCode, method = RequestMethod.GET)
    @ApiOperation(value = "根据Code获取指标")
    MTjQuotaModel getByCode(@RequestParam(value = "code") String code) ;

    @RequestMapping(value = ServiceApi.TJ.TjQuotaConfigInfo, method = RequestMethod.GET)
    @ApiOperation(value = "分页获取指标配置")
    ListResult quotaConfigInfo(
            @RequestParam(value = "quotaName", required = false) String quotaName,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "pageSize") Integer pageSize);

    @RequestMapping(value =  ServiceApi.TJ.TjQuotaTypeIsExist,method = RequestMethod.POST)
    List tjQuotaTypeIsExist(
            @RequestParam(value = "type")String type,
            @RequestBody String json);

    @RequestMapping(value = ServiceApi.TJ.TjQuotaBatch, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "批量导入指标、主维度、细维度", notes = "批量导入指标、主维度、细维度")
    boolean tjQuotaBatch(
            @RequestBody String lsMap) ;
}
