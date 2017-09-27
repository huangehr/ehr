package com.yihu.ehr.quota.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * Created by progr1mmer on 2017/9/13.
 */
@FeignClient(name= MicroServices.Patient)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface TjQuotaSynthesizeQueryClient {

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaSynthesiseDimension, method = RequestMethod.GET)
    @ApiOperation(value = "查询多个指标交集维度")
    List<Map<String, String>> getTjQuotaSynthesiseDimension(
            @ApiParam(name = "quotaCodes", value = "指标code，多个指标用英文,分开")
            @RequestParam(value = "quotaCodes") String quotaCodes);

    @RequestMapping(value = ServiceApi.TJ.GetTjQuotaSynthesiseDimensionKeyVal, method = RequestMethod.GET)
    @ApiOperation(value = "查询多个指标交集维度的字典项")
    Map<String, Map<String, Object>>  getTjQuotaSynthesiseDimensionKeyVal(
            @ApiParam(name = "quotaCode", value = "指标code多个指标其中一个")
            @RequestParam(value = "quotaCode") String quotaCode,
            @ApiParam(name = "dimensions", value = "维度编码，多个维度用英文,分开")
            @RequestParam(value = "dimensions") String dimensions);

}
