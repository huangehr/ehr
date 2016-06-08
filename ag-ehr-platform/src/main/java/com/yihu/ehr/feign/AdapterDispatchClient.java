package com.yihu.ehr.feign;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * Created by AndyCai on 2016/3/3.
 */
@FeignClient(name = "svr-adaption")
@ApiIgnore
public interface AdapterDispatchClient {

    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000")
    @RequestMapping(value = ApiVersion.Version1_0 + "/adapter_dispatcher/org_standard_data/{org_code}", method = RequestMethod.GET)
    Object getSchemeMappingInfo(
            @RequestParam(value = "private_key", required = true) String privateKey,
            @RequestParam(value = "version_code", required = true) String versionCode,
            @RequestParam(value = "org_code", required = true) String orgcode);

    @RequestMapping(value = ApiVersion.Version1_0 + "/adapter_dispatcher/org_plan/version", method = RequestMethod.GET)
    Object getCDAVersionInfoByOrgCode(
            @ApiParam(name = "org_code", value = "机构编码")
            @RequestParam(value = "org_code") String orgCode);


    @RequestMapping(value = ApiVersion.Version1_0 + "/adapter_dispatcher/{org_code}", method = RequestMethod.GET)
    public Object downAdaptions(
            @RequestParam(value = "private_key", required = true) String privateKey,
            @RequestParam(value = "version_code", required = true) String versionCode,
            @PathVariable(value = "org_code") String orgcode);

    @RequestMapping(value = ApiVersion.Version1_0 + "/adapter_dispatcher/{org_code}/source", method = RequestMethod.GET)
    Map getAdaptionUrl(
            @ApiParam(required = true, name = "private_key", value = "用户名")
            @RequestParam(value = "private_key", required = true) String privateKey,
            @ApiParam(required = true, name = "version_code", value = "适配标准版本")
            @RequestParam(value = "version_code", required = true) String versionCode,
            @ApiParam(required = true, name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code") String orgcode);
}
