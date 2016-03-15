package com.yihu.ehr.feign;

import com.yihu.ehr.constants.*;
import com.yihu.ehr.util.RestEcho;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by AndyCai on 2016/3/3.
 */
@FeignClient(name = MicroServiceName.Adaption,url = MicroServiceIpAddressStr.Adaption+ MicroServicePort.Adaption)
@RequestMapping(ApiVersion.Version1_0 )
@ApiIgnore
public interface AdapterDispatchClient {

    @RequestMapping(value = "/adapter-dispatcher/schemaMappingPlan", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案映射信息", response = RestEcho.class, produces = "application/json", notes = "获取采集标准适配方案信息，文件以Base64编码，压缩格式为zip")
    Object getSchemeMappingInfo(
            @ApiParam(required = true, name = "userkey", value = "用户私钥")
            @RequestParam(value = "userkey", required = true) String userkey,
            @ApiParam(required = true, name = "versionCode", value = "适配标准版本")
            @RequestParam(value = "versionCode", required = true) String versionCode,
            @ApiParam(required = true, name = "orgcode", value = "机构代码")
            @RequestParam(value = "orgcode", required = true) String orgcode) ;


    @RequestMapping(value = "/adapter-dispatcher/allSchemaMappingPlan", method = RequestMethod.GET)
    @ApiOperation(value = "获取采集标准及适配方案信息", response = RestEcho.class, produces = "application/json", notes = "获取采集标准及适配方案信息，文件以Base64编码，压缩格式为zip")
    Object getALLSchemeMappingInfo(
            @ApiParam(required = true, name = "userkey", value = "用户名")
            @RequestParam(value = "userkey", required = true) String userkey,
            @ApiParam(required = true, name = "versionCode", value = "适配标准版本")
            @RequestParam(value = "versionCode", required = true) String versionCode,
            @ApiParam(required = true, name = "orgcode", value = "机构代码")
            @RequestParam(value = "orgcode", required = true) String orgcode) ;


    @RequestMapping(value = "/adapter-dispatcher/versionplan", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构编码获取最新映射版本号 ", response = RestEcho.class, produces = "application/json", notes = "指定版本的信息")
    Object getCDAVersionInfoByOrgCode(
            @ApiParam(name = "org_code", value = "机构编码")
            @RequestParam(value = "org_code") String orgCode) ;
}
