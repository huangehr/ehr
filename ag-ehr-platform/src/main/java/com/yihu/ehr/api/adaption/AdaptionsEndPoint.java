package com.yihu.ehr.api.adaption;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.feign.AdapterDispatchClient;
import com.yihu.ehr.feign.SecurityClient;
import com.yihu.ehr.feign.StandardDispatchClient;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.util.RestEcho;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * 标准适配。标准适配分为两个方面：机构数据标准与适配方案。前者用于定义医疗机构信息化系统的数据视图，
 * 后者根据此视图与平台的健康档案数据标准进行匹配与映射。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.03 14:15
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/adaptions")
@Api(protocols = "https", value = "adaptions", description = "标准适配")
public class AdaptionsEndPoint {
    @Autowired
    private SecurityClient securityClient;

    @Autowired
    private StandardDispatchClient standardDispatchClient;

    @Autowired
    private AdapterDispatchClient adapterDispatchClient;

    @RequestMapping(value = "/organization/standard", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案摘要", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "获取两个指定版本的标准化数据差异与适配方案，文件以Base64编码，压缩格式为zip")
    public Object getOrgSchema(
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name", required = true) String userName,
            @ApiParam(required = true, name = "update_version", value = "要更新的目标版本")
            @RequestParam(value = "update_version", required = true) String updateVersion,
            @ApiParam(required = true, name = "current_version", value = "用户当前使用的版本")
            @RequestParam(value = "current_version", required = false) String currentVersion) {

        MKey mKey = securityClient.getUserKey(userName);
        if (mKey == null) {
            return new RestEcho().failed(ErrorCode.GenerateUserKeyFailed, "获取用户密钥失败");
        }

        Object restEcho = standardDispatchClient.getSchemeInfo(mKey.getPrivateKey(), updateVersion, currentVersion);
        return restEcho;
    }


    @RequestMapping(value = "/organization/adaption", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案映射信息", response = RestEcho.class, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "获取采集标准适配方案信息，文件以Base64编码，压缩格式为zip")
    public Object getOrgAdaption(
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name", required = true) String userName,
            @ApiParam(required = true, name = "version_code", value = "适配标准版本")
            @RequestParam(value = "version_code", required = true) String versionCode,
            @ApiParam(required = true, name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code", required = true) String orgCode) throws Exception {
        String errorMsg = null;
        if (StringUtils.isEmpty(userName)) {
            errorMsg += "缺失参数:user_name!";
        }
        if (StringUtils.isEmpty(versionCode)) {
            errorMsg += "缺失参数:version_code!";
        }
        if (StringUtils.isEmpty(orgCode)) {
            errorMsg += "缺失参数:org_code!";
        }
        if (StringUtils.isNotEmpty(errorMsg))
            return new RestEcho().failed(ErrorCode.MissParameter, errorMsg);
        MKey mKey = securityClient.getUserKey(userName);
        if (mKey == null) {
            return new RestEcho().failed(ErrorCode.GenerateUserKeyFailed, "获取用户密钥失败");
        }
        Object object = adapterDispatchClient.getSchemeMappingInfo(mKey.getPrivateKey(), versionCode, orgCode);
        return object;
    }


    @RequestMapping(value = "/organization", method = RequestMethod.GET)
    @ApiOperation(value = "获取采集标准及适配方案信息", response = RestEcho.class, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "获取采集标准及适配方案信息，文件以Base64编码，压缩格式为zip")
    public Object getOrgAdaptions(
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name", required = true) String userName,
            @ApiParam(required = true, name = "version_code", value = "适配标准版本")
            @RequestParam(value = "version_code", required = true) String versionCode,
            @ApiParam(required = true, name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code", required = true) String orgCode) {

        String errorMsg = null;
        if (StringUtils.isEmpty(userName)) {
            errorMsg += "缺失参数:user_name!";
        }
        if (StringUtils.isEmpty(versionCode)) {
            errorMsg += "缺失参数:version_code!";
        }
        if (StringUtils.isEmpty(orgCode)) {
            errorMsg += "缺失参数:org_code!";
        }
        if (StringUtils.isNotEmpty(errorMsg))
            return new RestEcho().failed(ErrorCode.MissParameter, errorMsg);
        MKey mKey = securityClient.getUserKey(userName);
        if (mKey == null) {
            return new RestEcho().failed(ErrorCode.GenerateUserKeyFailed, "获取用户密钥失败");
        }
        Object object = adapterDispatchClient.getALLSchemeMappingInfo(mKey.getPrivateKey(), versionCode, orgCode);
        return object;
    }


    @RequestMapping(value = "/version_plan", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构编码获取最新映射版本号 ", response = RestEcho.class, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "指定版本的信息")
    @HystrixProperty(name = "hystrix.command.default.execution.timeout.enabled", value = "false")
    public Object getCDAVersionInfoByOrgCode(
            @ApiParam(name = "org_code", value = "机构编码")
            @RequestParam(value = "org_code") String orgCode) throws Exception {
        if (StringUtils.isEmpty(orgCode)) {
            return new RestEcho().failed(ErrorCode.MissParameter, "缺失参数:org_code!");
        }
        Object object = adapterDispatchClient.getCDAVersionInfoByOrgCode(orgCode);
        return object;
    }

//
//    @ApiOperation(value = "获取机构数据标准", produces = "application/gzip")
//    @RequestMapping(value = "/organization/{org_code}/standard", method = {RequestMethod.GET})
//    public Object getOrgSchema(@ApiParam(name = "org_code", value = "机构代码")
//                               @PathVariable(value = "org_code") String orgCode) {
//
//
//        return null;
//    }
//
//    @RequestMapping(value = "/organization/{org_code}/adaption", method = {RequestMethod.GET})
//    @ApiOperation(value = "获取机构适配方案", produces = "application/gzip")
//    public Object getOrgAdaption(@ApiParam(name = "org_code", value = "机构代码")
//                                 @PathVariable(value = "org_code") String orgCode) {
//        return null;
//    }
//
//    @RequestMapping(value = "/organization/{org_code}", method = {RequestMethod.GET})
//    @ApiOperation(value = "获取机构所有适配方案", produces = "application/gzip")
//    public Object getOrgAdaptions(@ApiParam(name = "org_code", value = "机构代码")
//                                  @PathVariable(value = "org_code") String orgCode) {
//        return "{message: Hello}";
//    }
}
