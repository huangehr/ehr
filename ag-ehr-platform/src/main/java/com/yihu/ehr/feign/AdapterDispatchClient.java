package com.yihu.ehr.feign;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.util.RestEcho;
import com.yihu.ehr.util.encrypt.RSA;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AndyCai on 2016/3/3.
 */
@FeignClient(MicroServices.AdaptionMgr)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface AdapterDispatchClient {

    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000")
    @RequestMapping(value = "/adapter_dispatcher/org_standard_data/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案映射信息", response = RestEcho.class, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "获取采集标准适配方案信息，文件以Base64编码，压缩格式为zip")
    Object getSchemeMappingInfo(
            @RequestParam(value = "private_key", required = true) String privateKey,
            @RequestParam(value = "version_code", required = true) String versionCode,
            @RequestParam(value = "org_code", required = true) String orgcode);


    @RequestMapping(value = "/adapter_dispatcher/org_plan/version", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构编码获取最新映射版本号", response = RestEcho.class, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "指定版本的信息")
    Object getCDAVersionInfoByOrgCode(
            @ApiParam(name = "org_code", value = "机构编码")
            @RequestParam(value = "org_code") String orgCode);


    @RequestMapping(value = "/adapter_dispatcher/{org_code}", method = RequestMethod.GET)
    @ApiOperation(value = "获取采集标准及适配方案信息", response = RestEcho.class, produces = "application/json", notes = "获取采集标准及适配方案信息，文件以Base64编码，压缩格式为zip")
    public Object downAdaptions(
            @RequestParam(value = "private_key", required = true) String privateKey,
            @RequestParam(value = "version_code", required = true) String versionCode,
            @PathVariable(value = "org_code") String orgcode);


    @RequestMapping(value = "/adapter_dispatcher/{org_code}/source", method = RequestMethod.GET)
    @ApiOperation(value = "下载适配数据包，此包内容包含：平台标准，机构标准与二者适配", response = RestEcho.class, produces = "application/json", notes = "获取采集标准及适配方案信息，文件以Base64编码，压缩格式为zip")
    Map getAdaptionUrl(
            @ApiParam(required = true, name = "private_key", value = "用户名")
            @RequestParam(value = "private_key", required = true) String privateKey,
            @ApiParam(required = true, name = "version_code", value = "适配标准版本")
            @RequestParam(value = "version_code", required = true) String versionCode,
            @ApiParam(required = true, name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code") String orgcode) ;
}
