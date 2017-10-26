/*
 * Copyright (c) 2015 MONKEYK Information Technology Co. Ltd
 * www.monkeyk.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * MONKEYK Information Technology Co. Ltd ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement you
 * entered into with MONKEYK Information Technology Co. Ltd.
 */
package com.yihu.ehr.oauth2.web;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.oauth2.model.AccessToken;
import com.yihu.ehr.oauth2.model.ObjectResult;
import com.yihu.ehr.oauth2.model.Result;
import com.yihu.ehr.oauth2.oauth2.EhrTokenServices;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Restful OAuth API
 * <p>
 * 扩展默认的OAuth 功能,  提供 Restful API,
 * 可用于在获取access_token时调用
 */
@RestController
public class EhrAccessTokenEndpoint implements InitializingBean, ApplicationContextAware {


    private static final Logger LOG = LoggerFactory.getLogger(EhrAccessTokenEndpoint.class);

    @Autowired
    private ClientDetailsService clientDetailsService;
    @Autowired
    private EhrTokenServices tokenServices;
    @Autowired
    private AuthorizationCodeServices authorizationCodeServices;

    private AuthenticationManager authenticationManager;

    private OAuth2RequestFactory oAuth2RequestFactory;

    private OAuth2RequestValidator oAuth2RequestValidator = new DefaultOAuth2RequestValidator();

    private WebResponseExceptionTranslator providerExceptionHandler = new DefaultWebResponseExceptionTranslator();

    /**
     * 密码模式校验
     */
    @RequestMapping(value =ServiceApi.Authentication.AccessToken, method = RequestMethod.POST)
    @ApiOperation(value = "密码模式校验")
    public Result accessToken(@ApiParam(name = "userName", value = "登录账号", defaultValue = "")
                                       @RequestParam(value = "userName") String userName,
                                       @ApiParam(name = "password", value = "密码", defaultValue = "")
                                       @RequestParam(value = "password") String password,
                                       @ApiParam(name = "clientId", value = "应用ID", defaultValue = "")
                                       @RequestParam(value = "clientId") String clientId)
    {
        try {
            //校验应用是否存在
            ClientDetails authenticatedClient = clientDetailsService.loadClientByClientId(clientId);

            if (StringUtils.isEmpty(clientId)) {
                throw new InvalidClientException("client id can not be null!");
            }

            Map<String, String> parameters = new HashMap<>();
            parameters.put("grant_type", "password");
            parameters.put("client_id", clientId);
            parameters.put("username", userName);
            parameters.put("password", password);
            TokenRequest tokenRequest = oAuth2RequestFactory.createTokenRequest(parameters, authenticatedClient);

            //校验 client_id 是否一致
            if (!clientId.equals(tokenRequest.getClientId())) {
                throw new InvalidClientException("client ID does not match authenticated client.");
            }


            if (authenticatedClient != null) {
                oAuth2RequestValidator.validateScope(tokenRequest, authenticatedClient);
            }

            final String grantType = tokenRequest.getGrantType();
            if (!StringUtils.hasText(grantType)) {
                throw new InvalidRequestException("Missing grant type");
            }
            if ("implicit".equals(grantType)) {
                throw new InvalidGrantException("Implicit grant type not supported from token endpoint");
            }

            if (isAuthCodeRequest(parameters)) {
                // The scope was requested or determined during the authorization step
                if (!tokenRequest.getScope().isEmpty()) {
                    LOG.debug("Clearing scope of incoming token request");
                    tokenRequest.setScope(Collections.<String>emptySet());
                }
            }

            if (isRefreshTokenRequest(parameters)) {
                // A refresh token has its own default scopes, so we should ignore any added by the factory here.
                tokenRequest.setScope(OAuth2Utils.parseParameterList(parameters.get(OAuth2Utils.SCOPE)));
            }

            AccessToken accessToken = new AccessToken();
            OAuth2AccessToken token = getTokenGranter(grantType).grant(grantType, tokenRequest);
            if (token == null) {
                throw new UnsupportedGrantTypeException("Unsupported grant type: " + grantType);
            } else {
                System.out.print(token.getValue());
                accessToken.setAccessToken(token.getValue());
                accessToken.setRefreshToken(token.getRefreshToken().getValue());
                accessToken.setTokenType(token.getTokenType());
                accessToken.setExpiresIn(token.getExpiresIn());
                accessToken.setUser(parameters.get("username"));
            }

            return Result.success("get access token success!",accessToken);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return Result.error(ex.getMessage());
        }
    }

    /**
     * 判断token是否有效
     *
     * @return
     */
    @RequestMapping(value = ServiceApi.Authentication.ValidToken, method = RequestMethod.POST)
    @ApiOperation(value = "验证token", notes = "验证token")
    public Result validToken(@ApiParam(name = "clientId", value = "应用ID", defaultValue = "")
                              @RequestParam(value = "clientId") String clientId,
                              @ApiParam(name = "accessToken", value = "accessToken", defaultValue = "")
                              @RequestParam(value = "accessToken") String accessToken) throws Exception {

        try {
            //根据accessToken查询相应的访问授权数据行
            OAuth2AccessToken auth2AccessToken = tokenServices.readAccessToken(accessToken);
            if (auth2AccessToken == null)
            {
                return Result.error("无效accesstoken");
            }
            else {
                if (!auth2AccessToken.getValue().equals(accessToken) || auth2AccessToken.isExpired()) {
                    return Result.error("accesstoken 已经过期");
                } else {
                    //判断ClientId
                    OAuth2Authentication authentication = tokenServices.loadAuthentication(accessToken);
                    String authenticationClientId = authentication.getOAuth2Request().getClientId();
                    if(authenticationClientId!=null && authenticationClientId.equals(clientId)) {
                        String authenticationName = authentication.getName();
                        AccessToken token = new AccessToken();
                        token.setAccessToken(auth2AccessToken.getValue());
                        token.setRefreshToken(auth2AccessToken.getRefreshToken().getValue());
                        token.setTokenType(auth2AccessToken.getTokenType());
                        token.setExpiresIn(auth2AccessToken.getExpiresIn());
                        token.setUser(authenticationName);
                        if(null != authentication.getOAuth2Request().getRequestParameters().get("userId")) {
                            String authenticationUserId = authentication.getOAuth2Request().getRequestParameters().get("userId").toString();
                            token.setUserId(authenticationUserId);
                        }
                        ObjectResult re = new ObjectResult(true,"accesstoken 可以使用");
                        re.setData(token);
                        return re;
                    }
                    else{
                        return Result.error("非法ClientId");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }

    }


    @RequestMapping(value = ServiceApi.Authentication.RefreshToken, method = RequestMethod.POST)
    @ApiOperation(value = "刷新token", notes = "刷新token")
    Result refreshToken(
            @ApiParam(name = "clientId", value = "应用ID", defaultValue = "")
            @RequestParam(value = "clientId") String clientId,
            @ApiParam(name = "refreshToken", value = "刷新Token", defaultValue = "")
            @RequestParam(value = "refreshToken") String refreshToken) {
        try {
            //校验应用是否存在
            ClientDetails authenticatedClient = clientDetailsService.loadClientByClientId(clientId);

            if (clientId != null && !"".equals(clientId)) {
                throw new InvalidClientException("client ID does not exist.");
            }

            Map<String, String> parameters = new HashMap<>();
            parameters.put("client_id", clientId);
            parameters.put("grant_type", "refresh_token");
            parameters.put("client_secret", "admin");
            parameters.put("refresh_token", refreshToken);
            TokenRequest tokenRequest = oAuth2RequestFactory.createTokenRequest(parameters, authenticatedClient);

            //校验 client_id 是否一致
            if (!clientId.equals(tokenRequest.getClientId())) {
                throw new InvalidClientException("client ID does not match authenticated client.");
            }


            if (authenticatedClient != null) {
                oAuth2RequestValidator.validateScope(tokenRequest, authenticatedClient);
            }

            final String grantType = tokenRequest.getGrantType();
            if (!StringUtils.hasText(grantType)) {
                throw new InvalidRequestException("Missing grant type");
            }
            if ("implicit".equals(grantType)) {
                throw new InvalidGrantException("Implicit grant type not supported from token endpoint");
            }

            if (isAuthCodeRequest(parameters)) {
                // The scope was requested or determined during the authorization step
                if (!tokenRequest.getScope().isEmpty()) {
                    LOG.debug("Clearing scope of incoming token request");
                    tokenRequest.setScope(Collections.<String>emptySet());
                }
            }

            if (isRefreshTokenRequest(parameters)) {
                // A refresh token has its own default scopes, so we should ignore any added by the factory here.
                tokenRequest.setScope(OAuth2Utils.parseParameterList(parameters.get(OAuth2Utils.SCOPE)));
            }

            AccessToken accessToken = new AccessToken();
            OAuth2AccessToken token = getTokenGranter(grantType).grant(grantType, tokenRequest);
            if (token == null) {
                throw new UnsupportedGrantTypeException("Unsupported grant type: " + grantType);
            } else {
                System.out.print(token.getValue());
                accessToken.setAccessToken(token.getValue());
                accessToken.setRefreshToken(token.getRefreshToken().getValue());
                accessToken.setTokenType(token.getTokenType());
                accessToken.setExpiresIn(token.getExpiresIn());
                accessToken.setUser(parameters.get("username"));
            }

            return Result.success("get access token success!",accessToken);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return Result.error(ex.getMessage());
        }

    }







    protected TokenGranter getTokenGranter(String grantType) {

        if ("authorization_code".equals(grantType)) {
            return new AuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices, clientDetailsService, this.oAuth2RequestFactory);
        } else if ("password".equals(grantType)) {
            return new ResourceOwnerPasswordTokenGranter(getAuthenticationManager(), tokenServices, clientDetailsService, this.oAuth2RequestFactory);
        } else if ("refresh_token".equals(grantType)) {
            return new RefreshTokenGranter(tokenServices, clientDetailsService, this.oAuth2RequestFactory);
        } else if ("client_credentials".equals(grantType)) {
            return new ClientCredentialsTokenGranter(tokenServices, clientDetailsService, this.oAuth2RequestFactory);
        } else if ("implicit".equals(grantType)) {
            return new ImplicitTokenGranter(tokenServices, clientDetailsService, this.oAuth2RequestFactory);
        } else {
            throw new UnsupportedGrantTypeException("Unsupport grant_type: " + grantType);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<OAuth2Exception> handleException(Exception e) throws Exception {
        LOG.info("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
        return getExceptionTranslator().translate(e);
    }

    @ExceptionHandler(ClientRegistrationException.class)
    public ResponseEntity<OAuth2Exception> handleClientRegistrationException(Exception e) throws Exception {
        LOG.info("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
        return getExceptionTranslator().translate(new BadClientCredentialsException());
    }

    @ExceptionHandler(OAuth2Exception.class)
    public ResponseEntity<OAuth2Exception> handleException(OAuth2Exception e) throws Exception {
        LOG.info("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
        return getExceptionTranslator().translate(e);
    }

    private boolean isRefreshTokenRequest(Map<String, String> parameters) {
        return "refresh_token".equals(parameters.get("grant_type")) && parameters.get("refresh_token") != null;
    }

    private boolean isAuthCodeRequest(Map<String, String> parameters) {
        return "authorization_code".equals(parameters.get("grant_type")) && parameters.get("code") != null;
    }

    protected String getClientId(Map<String, String> parameters) {
        return parameters.get("client_id");
    }

    private AuthenticationManager getAuthenticationManager() {
        return this.authenticationManager;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        Assert.state(clientDetailsService != null, "ClientDetailsService must be provided");
        Assert.state(authenticationManager != null, "AuthenticationManager must be provided");

        oAuth2RequestFactory = new DefaultOAuth2RequestFactory(clientDetailsService);
    }

    protected WebResponseExceptionTranslator getExceptionTranslator() {
        return providerExceptionHandler;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (this.authenticationManager == null) {
            this.authenticationManager = (AuthenticationManager) applicationContext.getBean("authenticationManager");
        }
    }
}
