package com.yihu.ehr.api.app;

import com.yihu.ehr.api.model.TokenModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.service.oauth2.EhrTokenStoreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 应用授权接口。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.24 17:25
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/applications")
@Api(value = "applications", description = "应用管理服务")
public class AppsEndPoint {
    @Autowired
    EhrTokenStoreService tokenStoreService;

    @ApiOperation(value = "检查应用授权", notes = "提供用户名/密码作为Basic验证")
    @RequestMapping(value = "/{client_id}/tokens/{access_token}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public TokenModel checkClientAuthorization(@ApiParam(value = "client_id")
                                           @PathVariable("client_id") String clientId,
                                               @ApiParam(value = "access_token")
                                           @PathVariable("access_token") String accessToken) {
        OAuth2AccessToken token = tokenStoreService.readAccessToken(accessToken);

        TokenModel tokenModel = new TokenModel();
        tokenModel.setId(1);
        tokenModel.setToken(token.getValue());
        tokenModel.setTokenLastEight(token.getValue().substring(token.getValue().length() - 8));
        tokenModel.setUpdatedAt(new Date());

        return tokenModel;
    }

    @ApiOperation(value = "重置应用授权", notes = "提供Client Id/Secret作为Basic验证")
    @RequestMapping(value = "/{client_id}/tokens/{access_token}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST)
    public String restClientAuthorization(@ApiParam(value = "client_id")
                                          @PathVariable("client_id") String clientId,
                                          @ApiParam(value = "access_token")
                                          @PathVariable("access_token") String accessToken) {
        return null;
    }

    @ApiOperation(value = "删除应用授权", notes = "提供Client Id/Secret作为Basic验证")
    @RequestMapping(value = "/{client_id}/tokens/{access_token}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.DELETE)
    public void revokeClientAuthorization(@ApiParam(value = "client_id")
                                          @PathVariable("client_id") String clientId,
                                          @ApiParam(value = "access_token")
                                          @PathVariable("access_token") String accessToken) {
        OAuth2Authentication authentication = tokenStoreService.readAuthentication(accessToken);
        if (authentication.getOAuth2Request().getClientId().equals(clientId)){
            tokenStoreService.removeAccessToken(tokenStoreService.readAccessToken(accessToken));
        }
    }
}
