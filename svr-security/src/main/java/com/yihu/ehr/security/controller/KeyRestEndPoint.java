package com.yihu.ehr.security.controller;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.security.feign.AppClient;
import com.yihu.ehr.security.feign.UserClient;
import com.yihu.ehr.security.service.*;
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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "securities", description = "安全管理接口", tags = {"用户,企业,应用,安全"})
public class KeyRestEndPoint extends BaseRestController {

    @Autowired
    private KeyManager keyManager;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private AppClient appClient;

    @Autowired
    private UserClient userClient;

    @RequestMapping(value = RestApi.Securities.UserKey, method = RequestMethod.GET)
    @ApiOperation(value = "获取用户Key", notes = "公-私钥用于与健康档案平台加密传输数据使用")
    public MKey getUserKey(
            @ApiParam(name = "user_id", value = "用户名")
            @PathVariable(value = "user_id") String userId) throws Exception {
        Key key = keyManager.getKeyByUserId(userId);

        return convertToModel(key, MKey.class);
    }

    @RequestMapping(value = RestApi.Securities.UserKey, method = RequestMethod.POST)
    @ApiOperation(value = "为用户创建Key")
    public MKey createUserKey(
            @ApiParam(name = "user_id", value = "用户代码")
            @PathVariable(value = "user_id") String userId) throws Exception {
        Key key = keyManager.createKeyByUserId(userId);
        return convertToModel(key, MKey.class);
    }

    @RequestMapping(value = RestApi.Securities.UserKeyId, method = RequestMethod.GET)
    @ApiOperation(value = "获取用户key的id")
    public String getKeyIdByUserId(
            @ApiParam(name = "user_id", value = "用户id")
            @PathVariable(value = "user_id") String userId) throws Exception {
        return keyManager.getKeyByUserId(userId).getId();
    }

    /**
     * 1-2 根据传入的用户名及密码，进行用户验证，并返回生成的token。
     * 1-2-1 对传入的密码进行解密处理
     * 1-2-2 对用户的帐户名及密码进行验证，验证通过，则进入1-2-3，否则提示报错。
     * 1-2-3 根据用户ID及APP ID查询相应的授权信息
     * 1-2-3-1 存在用户授权，判断用户授权是否过期，是则提示过期
     * 1-2-3-2 不存在用户授权，新增授权信息，并返回
     * <p>
     * requestBody格式:
     * {
     * "user_name": "hill9868",
     * "rsa_pw": "f67b9646bcdaa60c647dfe7bc2623190...(略)",
     * "app_id": "AnG4G4zIz1",
     * "app_secret": "mPTZvuEFB6C32fko"
     * }
     */
    //现在tocken通过网关认证来获取
//    @RequestMapping(value = RestApi.Securities.UserToken, method = RequestMethod.PUT)
//    @ApiOperation(value = "获取用户登录用的临时会话Token", produces = "application/json", notes = "此Token用于客户与平台之间的会话，时效性时差为20分钟")
//    public Object createTempUserToken(
//            @ApiParam(required = true, name = "user_name", value = "用户名")
//            @RequestParam(value = "user_name", required = true) String userNmae,
//            @ApiParam(required = true, name = "rsa_pw", value = "用户密码，以RSA加密")
//            @RequestParam(value = "rsa_pw", required = true) String rsaPWD,
//            @ApiParam(required = true, name = "app_id", value = "APP ID")
//            @RequestParam(value = "app_id", required = true) String appId,
//            @ApiParam(required = true, name = "app_secret", value = "APP 密码")
//            @RequestParam(value = "app_secret", required = true) String appSecret) throws Exception {
//
//        boolean isAppExistence = appClient.isAppExistence(appId, appSecret);
//        if (!isAppExistence) return null;
//        MUser user1 = userClient.getUserByLoginCode(userNmae);
//        Key key = keyManager.getKeyByUserId(user1.getId());
//        String privateKey = key.getPrivateKey();
//
//        java.security.Key priKey = RSA.genPrivateKey(privateKey);
//        String psw = RSA.decrypt(rsaPWD, priKey);
//
//        MUser user = userClient.getUserByNameAndPassword(userNmae, psw);
//        if (user == null) return null;
//
//        Token token = tokenManager.getTokenByUserId(user.getId(), appId);
//        if (token == null) {
//            token = tokenManager.createUserToken(user.getId(), appId);
//
//            Map<String, Object> map = new HashMap<>();
//            map.put("access_token", token.getAccessToken());
//            map.put("refresh_token", token.getRefreshToken());
//            map.put("expires_in", token.getExpiresIn());
//            map.put("token_id", token.getTokenId());
//
//            return map;
//        }
//
//        Date currentDate = new Date();
//        boolean result = DateUtil.isExpire(token.getUpdateDate(), currentDate, token.getExpiresIn());
//        if (result == true) {
//            token = tokenManager.refreshAccessToken(user.getId(), token.getRefreshToken(), appId);
//
//            Map<String, Object> map = new HashMap<>();
//            map.put("access_token", token.getAccessToken());
//            map.put("refresh_token", token.getRefreshToken());
//            map.put("expires_in", token.getExpiresIn());
//
//            return map;
//        }
//
//        Map<String, Object> map = new HashMap<>();
//        map.put("access_token", token.getAccessToken());
//        map.put("refresh_token", token.getRefreshToken());
//        map.put("expires_in", token.getExpiresIn());
//
//        return map;
//    }

    @RequestMapping(value = RestApi.Securities.deleteUserKey, method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除Key")
    public boolean deleteKeyByUserId(
            @ApiParam(name = "user_id", value = "user_id")
            @PathVariable(value = "user_id") String userId) throws Exception{

        List<KeyMap> keyMaps =  keyManager.getKeyMapByUserId(userId);
        keyManager.deleteKey(keyMaps);
        return true;
    }


    @RequestMapping(value = RestApi.Securities.OrganizationKey, method = RequestMethod.GET)
    @ApiOperation(value = "获取机构Key", notes = "公-私钥用于与健康档案平台加密传输数据使用")
    public MKey getOrgKey(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code") String orgCode) {
        Key key = keyManager.getKeyByOrgCode(orgCode);
        if (key==null){
            return null;
        }
        return convertToModel(key, MKey.class);
    }

    @RequestMapping(value = RestApi.Securities.OrganizationKey, method = RequestMethod.POST)
    @ApiOperation(value = "为机构创建Key")
    public MKey createOrgKey(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code") String orgCode) throws Exception {
        Key key = keyManager.createKeyByOrgCode(orgCode);
        return convertToModel(key, MKey.class);
    }

    @RequestMapping(value = RestApi.Securities.OrganizationPublicKey, method = RequestMethod.GET)
    @ApiOperation(value = "获取机构公钥")
    public String getOrgPublicKey(
            @ApiParam(name = "org_code", value = "机构代码")
            @PathVariable(value = "org_code") String orgCode) throws Exception {
        return keyManager.getOrgKey(orgCode);
    }

    @RequestMapping(value = RestApi.Securities.deleteOrgKey, method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除Key")
    public boolean deleteKeyByOrgCode(
            @ApiParam(name = "org_code", value = "org_code")
            @PathVariable(value = "org_code") String orgCode) {

        List<KeyMap> keyMaps =  keyManager.getKeyMapByOrgCode(orgCode);
        keyManager.deleteKey(keyMaps);
        return true;
    }

    @RequestMapping(value = RestApi.Securities.Keys, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取Key")
    public MKey getKey(
            @ApiParam(name = "id", value = "security代码")
            @PathVariable(value = "id") String id) {
        Key key = keyManager.findKey(id);
        MKey mKey = convertToModel(key,MKey.class);
        return mKey;
    }
}