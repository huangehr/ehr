package com.yihu.ehr.api.legacy;

import com.yihu.ehr.api.adaption.Adaptions;
import com.yihu.ehr.util.RestEcho;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 历史遗留接口，以rest开头。这些接口只被集成开放平台使用，目前先整合到这边来。待所有版本均升级好之后，可以去掉此接口。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.26 10:13
 */
@RestController
@RequestMapping("/rest/v1.0")
public class LegacyController {

    @Autowired
    Adaptions adaptions;

    @ApiOperation(value = "下载标准版本", response = RestEcho.class)
    @RequestMapping(value = "/adapter-dispatcher/versionplan", produces = "application/json; charset=UTF-8", method = RequestMethod.GET)
    public Object getVersion(
            @ApiParam(name = "org_code", value = "机构编码")
            @RequestParam(value = "org_code") String orgCode) throws Exception{

        return adaptions.getCDAVersionInfoByOrgCode(orgCode);
    }

    @ApiOperation(value = "下载标准版本列表", response = RestEcho.class)
    @RequestMapping(value = "/adapter-dispatcher/allSchemaMappingPlan", produces = "application/json; charset=UTF-8", method = RequestMethod.GET)
    public Object getAllVerions(
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name", required = true) String userName,
            @ApiParam(required = true, name = "versionCode", value = "适配标准版本")
            @RequestParam(value = "versionCode", required = true) String versionCode,
            @ApiParam(required = true, name = "orgcode", value = "机构代码")
            @RequestParam(value = "orgcode", required = true) String orgCode) {

        return adaptions.getOrgAdaptions(userName, versionCode, orgCode);
    }


    @ApiOperation(value = "机构数据元", response = RestEcho.class)
    @RequestMapping(value = "/adapter-dispatcher/schema", produces = "application/json; charset=UTF-8", method = RequestMethod.GET)
    public Object downloadOrgMeta(
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name", required = true) String userName,
            @ApiParam(required = true, name = "update_version", value = "要更新的目标版本")
            @RequestParam(value = "update_version", required = true) String updateVersion,
            @ApiParam(required = true, name = "current_version", value = "用户当前使用的版本")
            @RequestParam(value = "current_version", required = false) String currentVersion) throws Exception{

        return adaptions.getOrgSchema(userName, updateVersion, null);
    }

    @ApiOperation(value = "标准映射", response = RestEcho.class)
    @RequestMapping(value = "adapter-dispatcher/schemaMappingPlan", produces = "application/json; charset=UTF-8", method = RequestMethod.GET)
    public Object downloadMapper(
            @ApiParam(required = true, name = "user_name", value = "用户名")
            @RequestParam(value = "user_name", required = true) String userName,
            @ApiParam(required = true, name = "version_code", value = "适配标准版本")
            @RequestParam(value = "version_code", required = true) String versionCode,
            @ApiParam(required = true, name = "org_code", value = "机构代码")
            @RequestParam(value = "org_code", required = true) String orgCode) throws Exception{

        return adaptions.getOrgAdaption(userName, versionCode, orgCode);
    }

    @ApiOperation(value = "注册病人", response = String.class)
    @RequestMapping(value = "/patient/registration", produces = "application/json; charset=UTF-8", method = RequestMethod.GET)
    public String registerPatient() {
        return null;
    }

    @ApiOperation(value = "上传档案包", response = String.class)
    @RequestMapping(value = "/json_package", produces = "application/json; charset=UTF-8", method = RequestMethod.GET)
    public String uploadPackage() {
        return null;
    }

    @ApiOperation(value = "公钥", response = String.class)
    @RequestMapping(value = "/user_key", produces = "application/json; charset=UTF-8", method = RequestMethod.GET)
    public String getUserKey() {
        return null;
    }

    @ApiOperation(value = "临时Token", response = String.class)
    @RequestMapping(value = "/token", produces = "application/json; charset=UTF-8", method = RequestMethod.GET)
    public String getAppToken() {
        return null;
    }
}
