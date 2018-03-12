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
import com.yihu.ehr.oauth2.oauth2.EhrTokenGranter;
import com.yihu.ehr.oauth2.oauth2.EhrAuthorizationServerTokenServices;
import com.yihu.ehr.oauth2.oauth2.jdbc.EhrJdbcClientDetailsService;
import com.yihu.ehr.oauth2.oauth2.redis.EhrRedisApiAccessValidator;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.endpoint.AbstractEndpoint;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Ehr Oauth2 Server
 * AccessToken Endpoint
 * Created by Progr1mmer on 2018/01/09.
 */
@RestController
public class EhrAccessTokenEndpoint extends AbstractEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(EhrAccessTokenEndpoint.class);

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private EhrJdbcClientDetailsService ehrJdbcClientDetailsService;
    @Autowired
    private EhrAuthorizationServerTokenServices ehrAuthorizationServerTokenServices;
    @Autowired
    private EhrTokenGranter ehrTokenGranter;
    @Autowired
    private EhrRedisApiAccessValidator ehrRedisApiAccessValidator;

    private OAuth2RequestFactory oAuth2RequestFactory;
    private OAuth2RequestValidator oAuth2RequestValidator = new DefaultOAuth2RequestValidator();
    private WebResponseExceptionTranslator providerExceptionHandler = new DefaultWebResponseExceptionTranslator();

    @PostConstruct
    private void init() {
        super.setTokenGranter(ehrTokenGranter);
    }

    @RequestMapping(value = ServiceApi.Authentication.AccessToken, method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> accessToken(@RequestParam Map<String, String> parameters) {
        String grant_type = parameters.get("grant_type");
        String client_id = parameters.get("client_id");
        String scope = parameters.get("scope");
        //检查基本参数
        if (StringUtils.isEmpty(client_id)) {
            throw new InvalidClientException("client id can not be null!");
        }
        if(StringUtils.isEmpty(grant_type)) {
            throw new InvalidRequestException("Missing grant type");
        }
        Map<String, String> param = new HashMap<>();
        param.put("grant_type", grant_type);
        param.put("client_id", client_id);
        param.put("scope", scope);
        if(grant_type.equals("authorization_code")) {
            param.put("code", parameters.get("code"));
            param.put("redirect_uri", parameters.get("redirect_uri"));
        }else if(grant_type.equals("password")) {
            param.put("username", parameters.get("username"));
            param.put("password", parameters.get("password"));
        }else if(grant_type.equals("refresh_token")){
            param.put("refresh_token", parameters.get("refresh_token"));
        }else {
            throw new UnsupportedGrantTypeException("unsupported grant type: " + grant_type);
        }
        ClientDetails authenticatedClient = ehrJdbcClientDetailsService.loadClientByClientId(client_id);
        TokenRequest tokenRequest = oAuth2RequestFactory.createTokenRequest(param, authenticatedClient);
        if (authenticatedClient != null) {
            oAuth2RequestValidator.validateScope(tokenRequest, authenticatedClient);
        }
        if (!StringUtils.hasText(tokenRequest.getGrantType())) {
            throw new InvalidRequestException("Missing grant type");
        }
        if (tokenRequest.getGrantType().equals("implicit")) {
            throw new InvalidGrantException("Implicit grant type not supported from token endpoint");
        }
        //校验 client_id 是否一致
        if (!client_id.equals(tokenRequest.getClientId())) {
            throw new InvalidClientException("Given client ID does not match authenticated client");
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

        Map<String, Object> tokenMap = new HashMap<>();
        OAuth2AccessToken token = getTokenGranter().grant(grant_type, tokenRequest);
        if (token == null) {
            throw new UnsupportedGrantTypeException("Unsupported grant type: " + grant_type);
        } else {
            tokenMap.put("accessToken", token.getValue());
            tokenMap.put("tokenType", token.getTokenType());
            tokenMap.put("expiresIn", token.getExpiresIn());
            tokenMap.put("refreshToken", token.getRefreshToken().getValue());
            if(!StringUtils.isEmpty(parameters.get("state"))) {
                tokenMap.put("state", parameters.get("state"));
            }
            if(grant_type.equals("password")) {
                tokenMap.put("user", parameters.get("username"));
            }
            putVerificationApi(tokenRequest, token);
        }
        return getResponse(tokenMap);
    }

    @RequestMapping(value = ServiceApi.Authentication.ValidToken, method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> validToken(
        @ApiParam(name = "clientId", value = "应用ID", required = true)
        @RequestParam(value = "clientId") String clientId,
        @ApiParam(name = "accessToken", value = "accessToken", required = true)
        @RequestParam(value = "accessToken") String accessToken,
        @ApiParam(name = "api", value = "访问api")
        @RequestParam(value = "api", required = false)String api) throws IllegalAccessException {
        //根据accessToken查询相应的访问授权数据行
        OAuth2AccessToken auth2AccessToken = ehrAuthorizationServerTokenServices.readAccessToken(accessToken);
        if (auth2AccessToken == null) {
            throw  new InvalidTokenException("Invalid accessToken");
        }
        else {
            if (!auth2AccessToken.getValue().equals(accessToken) || auth2AccessToken.isExpired()) {
                throw  new InvalidTokenException("Expired accessToken");
            } else {
                //判断ClientId
                OAuth2Authentication authentication = ehrAuthorizationServerTokenServices.loadAuthentication(accessToken);
                String authenticationClientId = authentication.getOAuth2Request().getClientId();
                if(authenticationClientId != null && authenticationClientId.equals(clientId)) {
                    Map<String, Object> successMap = new HashMap<>();
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) authentication.getUserAuthentication();
                    if(api != null) {
                        if(ehrRedisApiAccessValidator.verificationApi(clientId, usernamePasswordAuthenticationToken.getName(), api)){
                            successMap.put("successFlg", true);
                            successMap.put("accessToken", auth2AccessToken.getValue());
                            successMap.put("tokenType", auth2AccessToken.getTokenType());
                            successMap.put("expiresIn", auth2AccessToken.getExpiresIn());
                            successMap.put("refreshToken", auth2AccessToken.getRefreshToken().getValue());
                            return getResponse(successMap);
                        }else {
                            throw new InvalidRequestException("Illegal api request");
                        }
                    }else {
                        successMap.put("successFlg", true);
                        successMap.put("accessToken", auth2AccessToken.getValue());
                        successMap.put("tokenType", auth2AccessToken.getTokenType());
                        successMap.put("expiresIn", auth2AccessToken.getExpiresIn());
                        successMap.put("refreshToken", auth2AccessToken.getRefreshToken().getValue());
                        successMap.put("user", usernamePasswordAuthenticationToken.getName());
                        return getResponse(successMap);
                    }
                }
                else{
                    throw new InvalidClientException("Illegal client id");
                }
            }
        }
    }

    private void putVerificationApi(TokenRequest tokenRequest, OAuth2AccessToken token) {
        OAuth2Authentication authentication = ehrAuthorizationServerTokenServices.loadAuthentication(token.getValue());
        String clientId = tokenRequest.getClientId();
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken)authentication.getUserAuthentication();
        String userName = usernamePasswordAuthenticationToken.getName();
        ehrRedisApiAccessValidator.putVerificationApi(clientId, userName);
    }

    /**
    protected TokenGranter getTokenGranter(String grantType) {
        if ("authorization_code".equals(grantType)) {
            return new EhrAuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices, clientDetailsService, this.oAuth2RequestFactory, ehrJDBCUserSecurityService, ehrUserDetailsService);
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
    */

    @Override
    protected TokenGranter getTokenGranter() {
        return this.ehrTokenGranter;
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

    private ResponseEntity<Map<String, Object>> getResponse(Map<String, Object> accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        return new ResponseEntity<Map<String, Object>>(accessToken, headers, HttpStatus.OK);
    }

    private boolean isRefreshTokenRequest(Map<String, String> parameters) {
        return "refresh_token".equals(parameters.get("grant_type")) && parameters.get("refresh_token") != null;
    }

    private boolean isAuthCodeRequest(Map<String, String> parameters) {
        return "authorization_code".equals(parameters.get("grant_type")) && parameters.get("code") != null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        Assert.state(ehrJdbcClientDetailsService != null, "ClientDetailsService must be provided");
        Assert.state(authenticationManager != null, "AuthenticationManager must be provided");

        oAuth2RequestFactory = new DefaultOAuth2RequestFactory(ehrJdbcClientDetailsService);
    }

    private AuthenticationManager getAuthenticationManager() {
        return this.authenticationManager;
    }

    @Override
    protected WebResponseExceptionTranslator getExceptionTranslator() {
        return providerExceptionHandler;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (this.authenticationManager == null) {
            this.authenticationManager = (AuthenticationManager) applicationContext.getBean("authenticationManager");
        }
    }
}
