package com.yihu.ehr.ha.std.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.ha.security.service.SecurityClient;
import com.yihu.ehr.ha.std.service.AdapterDispatchClient;
import com.yihu.ehr.ha.std.service.StandardDispatchClient;
import com.yihu.ehr.model.security.MUserSecurity;
import com.yihu.ehr.util.RestEcho;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by AndyCai on 2016/1/26.
 */

@RequestMapping(ApiVersion.Version1_0 + "/admin/dispatch")
@RestController
public class StandardDispatchController  {

    @Autowired
    private SecurityClient securityClient;

    @Autowired
    private StandardDispatchClient standardDispatchClient;

    @Autowired
    private AdapterDispatchClient adapterDispatchClient;

    @RequestMapping(value = "/schema", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案摘要", produces = "application/json", notes = "获取两个指定版本的标准化数据差异与适配方案，文件以Base64编码，压缩格式为zip")
    public RestEcho getSchemeInfo(
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name", required = true) String userName,
            @ApiParam(required = true, name = "update_version", value = "要更新的目标版本")
            @RequestParam(value = "update_version", required = true) String updateVersion,
            @ApiParam(required = true, name = "current_version", value = "用户当前使用的版本")
            @RequestParam(value = "current_version", required = true) String currentVersion) {

        MUserSecurity mUserSecurity = securityClient.getUserSecurityByLoginCode(userName);

        if(mUserSecurity==null)
        {
            return new RestEcho().failed(ErrorCode.GenerateUserKeyFailed,"获取用户密钥失败");
        }

        RestEcho restEcho = standardDispatchClient.getSchemeInfo(mUserSecurity.getPrivateKey(),updateVersion,currentVersion);

        return restEcho;
    }

    @RequestMapping(value = "/schema_mapping_plan", method = RequestMethod.GET)
    @ApiOperation(value = "获取适配方案映射信息", response = RestEcho.class, produces = "application/json", notes = "获取采集标准适配方案信息，文件以Base64编码，压缩格式为zip")
    public Object getSchemeMappingInfo(
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name", required = true) String userName,
            @ApiParam(required = true, name = "version_code", value = "适配标准版本")
            @RequestParam(value = "version_code", required = true) String versionCode,
            @ApiParam(required = true, name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code", required = true) String orgCode) throws Exception {

        String errorMsg = null;
        if(StringUtils.isEmpty(userName))
        {
            errorMsg +="缺失参数:user_name!";
        }
        if (StringUtils.isEmpty(versionCode))
        {
            errorMsg +="缺失参数:version_code!";
        }
        if(StringUtils.isEmpty(orgCode))
        {
            errorMsg +="缺失参数:org_code!";
        }
        if(StringUtils.isNotEmpty(errorMsg))
            return new RestEcho().failed(ErrorCode.MissParameter,errorMsg);

        MUserSecurity mUserSecurity = securityClient.getUserSecurityByLoginCode(userName);

        if(mUserSecurity==null)
        {
            return new RestEcho().failed(ErrorCode.GenerateUserKeyFailed,"获取用户密钥失败");
        }

        Object object = adapterDispatchClient.getSchemeMappingInfo(mUserSecurity.getPrivateKey(),versionCode,orgCode);

        return object;
    }


    @RequestMapping(value = "/all_schema_mapping_plan", method = RequestMethod.GET)
    @ApiOperation(value = "获取采集标准及适配方案信息", response = RestEcho.class, produces = "application/json", notes = "获取采集标准及适配方案信息，文件以Base64编码，压缩格式为zip")
    public Object getALLSchemeMappingInfo(
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name", required = true) String userName,
            @ApiParam(required = true, name = "version_code", value = "适配标准版本")
            @RequestParam(value = "version_code", required = true) String versionCode,
            @ApiParam(required = true, name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code", required = true) String orgCode) {

        String errorMsg = null;
        if(StringUtils.isEmpty(userName))
        {
            errorMsg +="缺失参数:user_name!";
        }
        if (StringUtils.isEmpty(versionCode))
        {
            errorMsg +="缺失参数:version_code!";
        }
        if(StringUtils.isEmpty(orgCode))
        {
            errorMsg +="缺失参数:org_code!";
        }
        if(StringUtils.isNotEmpty(errorMsg))
            return new RestEcho().failed(ErrorCode.MissParameter,errorMsg);

        MUserSecurity mUserSecurity = securityClient.getUserSecurityByLoginCode(userName);

        if(mUserSecurity==null)
        {
            return new RestEcho().failed(ErrorCode.GenerateUserKeyFailed,"获取用户密钥失败");
        }

        Object object = adapterDispatchClient.getALLSchemeMappingInfo(mUserSecurity.getPrivateKey(), versionCode, orgCode);

        return object;
    }


    @RequestMapping(value = "/version_plan", method = RequestMethod.GET)
    @ApiOperation(value = "根据机构编码获取最新映射版本号 ", response = RestEcho.class, produces = "application/json", notes = "指定版本的信息")
    public Object getCDAVersionInfoByOrgCode(
            @ApiParam(name = "org_code", value = "机构编码")
            @RequestParam(value = "org_code") String orgCode) throws Exception {

        if(StringUtils.isEmpty(orgCode))
        {
            return new RestEcho().failed(ErrorCode.MissParameter,"缺失参数:org_code!");
        }
        Object object = adapterDispatchClient.getCDAVersionInfoByOrgCode(orgCode);
       return object;
    }
}
