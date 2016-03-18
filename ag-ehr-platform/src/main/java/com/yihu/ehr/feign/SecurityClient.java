package com.yihu.ehr.feign;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.*;
import com.yihu.ehr.model.security.MKey;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * Created by AndyCai on 2016/2/1.
 */
@FeignClient(name = MicroServices.SecurityMgr,url = MicroServiceIpAddressStr.Security+ MicroServicePort.Security)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface SecurityClient {

    @RequestMapping(value = RestApi.Securities.UserPublicKey, method = RequestMethod.GET)
    @ApiOperation(value = "获取用户公钥" , notes = "用户在平台注册时，会分配一个公钥，此公钥用于与健康档案平台加密传输数据使用")
    MKey getUserKey(@PathVariable(value = "user_id") String user_id) ;

    @RequestMapping(value = RestApi.Securities.OrganizationPublicKey, method = RequestMethod.GET)
    @ApiOperation(value = "获取企业公钥", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, notes = "企业公钥，用于与健康档案平台之间传输数据的加密。")
    String getOrgKey(@PathVariable(value = "org_code") String orgCode) ;

    @RequestMapping(value = RestApi.Securities.UserToken, method = RequestMethod.PUT)
    Map<String,Object> getUserTempToken(
            @PathVariable(value = "user_id") String userId,
            @PathVariable(value = "token_id") String tokenId,
            @RequestParam(value = "rsa_pw", required = true) String rsaPWD,
            @RequestParam(value = "app_id", required = true) String appId,
            @RequestParam(value = "app_secret", required = true) String appSecret);

    @RequestMapping(value = RestApi.Securities.Keys, method = RequestMethod.GET)
    MKey getKey(
            @ApiParam(name = "id", value = "security代码")
            @PathVariable(value = "id") String id);
}
