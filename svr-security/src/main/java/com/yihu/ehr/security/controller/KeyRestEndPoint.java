package com.yihu.ehr.security.controller;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.security.feign.AppClient;
import com.yihu.ehr.security.feign.UserClient;
import com.yihu.ehr.security.service.Key;
import com.yihu.ehr.security.service.KeyManager;
import com.yihu.ehr.security.service.TokenManager;
import com.yihu.ehr.security.service.Token;
import com.yihu.ehr.util.DateUtil;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.util.encrypt.RSA;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(protocols = "https", value = "securities", description = "Key接口")
public class KeyRestEndPoint extends BaseRestController {

    @Autowired
    private KeyManager keyManager;

    @RequestMapping(value = RestApi.Securities.UserKey, method = RequestMethod.POST)
    @ApiOperation(value = "为用户创建Key")
    public MKey createUserKey(
            @ApiParam(name = "user_id", value = "用户代码")
            @PathVariable(value = "user_id") String userId) throws Exception {
        Key key = keyManager.createKeyByUserId(userId);
        return convertToModel(key, MKey.class);
    }

    @RequestMapping(value = RestApi.Securities.UserKey, method = RequestMethod.GET)
    @ApiOperation(value = "获取用户Key", notes = "公-私钥用于与健康档案平台加密传输数据使用")
    public MKey getUserKey(
            @ApiParam(name = "user_id", value = "用户名")
            @PathVariable(value = "user_id") String userName) throws Exception {
        Key key = keyManager.getKeyByUserId(userName);

        return convertToModel(key, MKey.class);
    }

    @RequestMapping(value = RestApi.Securities.UserKeyId, method = RequestMethod.GET)
    @ApiOperation(value = "获取用户key的id")
    public String getKeyIdByUserId(
            @ApiParam(name = "user_id", value = "用户id")
            @PathVariable(value = "user_id") String userId) throws Exception {
        return keyManager.getKeyByUserId(userId).getId();
    }

    @RequestMapping(value = RestApi.Securities.OrganizationKey, method = RequestMethod.POST)
    @ApiOperation(value = "为机构创建Key")
    public MKey createOrgKey(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code") String orgCode) throws Exception {
        Key key = keyManager.createKeyByOrgCode(orgCode);
        return convertToModel(key, MKey.class);
    }

    @RequestMapping(value = RestApi.Securities.OrganizationKey, method = RequestMethod.GET)
    @ApiOperation(value = "获取机构Key", produces = "application/json", notes = "公-私钥用于与健康档案平台加密传输数据使用")
    public MKey getOrgKey(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code") String orgCode) {
        Key key = keyManager.getKeyByOrgCode(orgCode);
        return convertToModel(key, MKey.class);
    }

    @RequestMapping(value = RestApi.Securities.OrganizationPublicKey, method = RequestMethod.GET)
    @ApiOperation(value = "获取机构公钥")
    public String getOrgPublicKey(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code") String orgCode) throws Exception {
        return keyManager.getOrgKey(orgCode);
    }

    @RequestMapping(value = RestApi.Securities.Keys, method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除Key")
    public boolean deleteKey(
            @ApiParam(name = "id", value = "security代码")
            @PathVariable(value = "id") String id) {
        keyManager.deleteKey(id);
        keyManager.deleteKeyMap(id);

        return true;
    }
}