package com.yihu.ehr.tj.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author janseny
 * @version 1.0
 * @created 2017/6/9
 */
@FeignClient(name=MicroServices.Patient)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface TjQuotaDimensionMainClient {

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaDimensionMainList, method = RequestMethod.GET)
    @ApiOperation(value = "数据统计主维度关联信息列表")
    ListResult search(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaDimensionMainAll, method = RequestMethod.GET)
    @ApiOperation(value = "获取主维度子表中的所有mainCode")
    ListResult getTjQuotaDimensionMainAll(
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts);

    @RequestMapping(value = ServiceApi.TJ.TjQuotaDimensionMain, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增/修改主维度关联信息")
    ObjectResult add(@RequestBody String model) ;

    @RequestMapping(value = ServiceApi.TJ.AddTjQuotaDimensionMain, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增主维度关联信息")
    ObjectResult addTjQuotaDimensionMain(@RequestBody String model) ;

    @RequestMapping(value = ServiceApi.TJ.TjQuotaDimensionMain, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除主维度关联信息")
    Result delete(@RequestParam(value = "id") String id);

    @RequestMapping(value = "tj/deleteMainByQuotaCode", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据指标ID删除统计指标主维度关联信息")
    Result deleteMainByQuotaCode(@RequestParam(value = "quotaCode") String quotaCode);
}
