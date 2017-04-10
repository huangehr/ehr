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

import com.yihu.ehr.oauth2.model.AccessToken;
import com.yihu.ehr.oauth2.model.ObjectResult;
import com.yihu.ehr.oauth2.model.Result;
import com.yihu.ehr.oauth2.oauth2.EhrTokenServices;
import org.apache.commons.collections.map.HashedMap;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
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
     * 新增用户访问授权
      * @param parameters
     * @return
     */
    @RequestMapping(value = "/oauth/accesstoken", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public AccessToken postAccessToken(@RequestBody Map<String, String> parameters) {

        String clientId = getClientId(parameters);
        ClientDetails authenticatedClient = clientDetailsService.loadClientByClientId(clientId);

        TokenRequest tokenRequest = oAuth2RequestFactory.createTokenRequest(parameters, authenticatedClient);

        if (clientId != null && !"".equals(clientId)) {
            // Only validate the client details if a client authenticated during this
            // request.
            if (!clientId.equals(tokenRequest.getClientId())) {
                // double check to make sure that the client ID in the token request is the same as that in the
                // authenticated client
                throw new InvalidClientException(" client ID does not match authenticated client");
            }
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
        }
        else{
            System.out.print(token.getValue());
            accessToken.setAccessToken(token.getValue());
            accessToken.setRefreshToken(token.getRefreshToken().getValue());
            accessToken.setTokenType(token.getTokenType());
            accessToken.setExpiresIn(token.getExpiresIn());
            accessToken.setUser(parameters.get("username"));
        }

        return accessToken;
    }

    /**
     * 判断token是否有效
     *
     * @return
     */
    @RequestMapping(value = "/oauth/validtoken", method = RequestMethod.GET)
    public Result validToken(@RequestParam(value = "client_id") String clientId,
                             @RequestParam(value = "access_token") String accessToken) throws Exception {

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
                    if(authenticationClientId!=null && authenticationClientId.equals(clientId))
                    {
                        String authenticationName = authentication.getName();
                        AccessToken token = new AccessToken();
                        token.setAccessToken(auth2AccessToken.getValue());
                        token.setRefreshToken(auth2AccessToken.getRefreshToken().getValue());
                        token.setTokenType(auth2AccessToken.getTokenType());
                        token.setExpiresIn(auth2AccessToken.getExpiresIn());
                        token.setUser(authenticationName);

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
