package com.yihu.ehr.feign;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.security.MKey;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * Created by AndyCai on 2016/2/1.
 */
@FeignClient(MicroServices.Security)
@ApiIgnore
public interface SecurityClient {

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Securities.OrganizationPublicKey, method = RequestMethod.GET)
    String getOrgPublicKey(@PathVariable(value = "org_code") String orgCode) ;

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Securities.UserKey, method = RequestMethod.GET)
    MKey getUserKey(@PathVariable(value = "user_id") String user_id) ;

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Securities.OrganizationKey, method = RequestMethod.GET)
    MKey getOrgKey(@PathVariable(value = "org_code") String orgCode) ;

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Securities.UserToken, method = RequestMethod.PUT)
    Map<String,Object> getUserTempToken(
            @PathVariable(value = "user_id") String userId,
            @PathVariable(value = "token_id") String tokenId,
            @RequestParam(value = "rsa_pw", required = true) String rsaPWD,
            @RequestParam(value = "app_id", required = true) String appId,
            @RequestParam(value = "app_secret", required = true) String appSecret);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Securities.Keys, method = RequestMethod.GET)
    MKey getKey(
            @ApiParam(name = "id", value = "security代码")
            @PathVariable(value = "id") String id);
}
