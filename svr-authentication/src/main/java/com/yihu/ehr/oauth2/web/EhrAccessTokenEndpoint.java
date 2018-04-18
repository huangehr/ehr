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
import com.yihu.ehr.oauth2.oauth2.EhrOAuth2ExceptionTranslator;
import com.yihu.ehr.oauth2.oauth2.EhrTokenGranter;
import com.yihu.ehr.oauth2.oauth2.EhrTokenServices;
import com.yihu.ehr.oauth2.oauth2.jdbc.EhrJdbcClientDetailsService;
import com.yihu.ehr.oauth2.oauth2.redis.EhrRedisApiAccessValidator;
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
import javax.servlet.http.HttpServletRequest;
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
    private EhrTokenServices ehrTokenServices;
    @Autowired
    private EhrTokenGranter ehrTokenGranter;
    @Autowired
    private EhrRedisApiAccessValidator ehrRedisApiAccessValidator;
    @Autowired
    private EhrOAuth2ExceptionTranslator ehrOAuth2ExceptionTranslator;

    private OAuth2RequestFactory oAuth2RequestFactory;
    private OAuth2RequestValidator oAuth2RequestValidator = new DefaultOAuth2RequestValidator();

    @PostConstruct
    private void init() {
        super.setTokenGranter(ehrTokenGranter);
    }

    @RequestMapping(value = ServiceApi.Authentication.AccessToken, method = RequestMethod.POST)
    public ResponseEntity<AccessToken> accessToken(@RequestParam Map<String, String> parameters) {
        String grant_type = parameters.get("grant_type");
        String client_id = parameters.get("client_id");
        String scope = parameters.get("scope");
        //检查基本参数
        if (StringUtils.isEmpty(client_id)) {
            throw new InvalidRequestException("Missing client id");
        }
        if (StringUtils.isEmpty(grant_type)) {
            throw new InvalidRequestException("Missing grant type");
        }
        Map<String, String> param = new HashMap<>();
        param.put("grant_type", grant_type);
        param.put("client_id", client_id);
        param.put("scope", scope);
        if (grant_type.equals("authorization_code")) {
            param.put("code", parameters.get("code"));
            param.put("redirect_uri", parameters.get("redirect_uri"));
        } else if (grant_type.equals("password")) {
            param.put("username", parameters.get("username"));
            param.put("password", parameters.get("password"));
        } else if (grant_type.equals("refresh_token")){
            param.put("refresh_token", parameters.get("refresh_token"));
        } else {
            throw new UnsupportedGrantTypeException("unsupported grant type: " + grant_type);
        }
        ClientDetails authenticatedClient = ehrJdbcClientDetailsService.loadClientByClientId(client_id);
        TokenRequest tokenRequest = oAuth2RequestFactory.createTokenRequest(param, authenticatedClient);
        //校验 client_id 是否一致
        if (!client_id.equals(tokenRequest.getClientId())) {
            throw new InvalidClientException("Given client ID does not match authenticated client");
        }
        if (authenticatedClient != null) {
            oAuth2RequestValidator.validateScope(tokenRequest, authenticatedClient);
        }
        if (!StringUtils.hasText(tokenRequest.getGrantType())) {
            throw new InvalidRequestException("Missing grant type");
        }
        if (tokenRequest.getGrantType().equals("implicit")) {
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
        OAuth2AccessToken token = getTokenGranter().grant(grant_type, tokenRequest);
        if (token == null) {
            throw new UnsupportedGrantTypeException("Unsupported grant type: " + grant_type);
        } else {
            accessToken.setAccessToken(token.getValue());
            accessToken.setTokenType(token.getTokenType());
            accessToken.setExpiresIn(token.getExpiresIn());
            accessToken.setRefreshToken(token.getRefreshToken().getValue());
            accessToken.setState(parameters.get("state"));
            accessToken.setUser(parameters.get("username"));
            putVerificationApi(tokenRequest, token);
        }
        return getResponse(accessToken);
    }

    @RequestMapping(value = ServiceApi.Authentication.ValidToken, method = RequestMethod.POST)
    public ResponseEntity<AccessToken> validToken(@RequestParam Map<String, String> parameters) throws IllegalAccessException {
        //根据accessToken查询相应的访问授权数据行
        String clientId = parameters.get("clientId");
        String accessToken = parameters.get("accessToken");
        String api = parameters.get("api");
        if (StringUtils.isEmpty(clientId)) {
            throw new InvalidRequestException("Missing clientId");
        }
        if (StringUtils.isEmpty(accessToken)) {
            throw new InvalidRequestException("Missing accessToken");
        }
        OAuth2AccessToken auth2AccessToken = ehrTokenServices.readAccessToken(accessToken);
        if (auth2AccessToken == null) {
            throw new InvalidTokenException("Invalid accessToken");
        } else {
            if (!auth2AccessToken.getValue().equals(accessToken) || auth2AccessToken.isExpired()) {
                throw new InvalidTokenException("Expired accessToken");
            } else {
                //判断ClientId
                OAuth2Authentication authentication = ehrTokenServices.loadAuthentication(accessToken);
                String authenticationClientId = authentication.getOAuth2Request().getClientId();
                if (authenticationClientId != null && authenticationClientId.equals(clientId)) {
                    AccessToken accessToken1 = new AccessToken();
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = (UsernamePasswordAuthenticationToken) authentication.getUserAuthentication();
                    if (StringUtils.isEmpty(api)) {
                        accessToken1.setAccessToken(auth2AccessToken.getValue());
                        accessToken1.setTokenType(auth2AccessToken.getTokenType());
                        accessToken1.setExpiresIn(auth2AccessToken.getExpiresIn());
                        accessToken1.setRefreshToken(auth2AccessToken.getRefreshToken().getValue());
                        accessToken1.setUser(usernamePasswordAuthenticationToken.getName());
                        accessToken1.setState(parameters.get("state"));
                        return getResponse(accessToken1);
                    } else {
                        if (ehrRedisApiAccessValidator.verificationApi(clientId, usernamePasswordAuthenticationToken.getName(), api)){
                            accessToken1.setAccessToken(auth2AccessToken.getValue());
                            accessToken1.setTokenType(auth2AccessToken.getTokenType());
                            accessToken1.setExpiresIn(auth2AccessToken.getExpiresIn());
                            accessToken1.setRefreshToken(auth2AccessToken.getRefreshToken().getValue());
                            accessToken1.setUser(usernamePasswordAuthenticationToken.getName());
                            accessToken1.setState(parameters.get("state"));
                            return getResponse(accessToken1);
                        } else {
                            throw new InvalidRequestException("Illegal api request");
                        }
                    }
                } else {
                    throw new InvalidClientException("Illegal client id");
                }
            }
        }
    }

    private void putVerificationApi(TokenRequest tokenRequest, OAuth2AccessToken token) {
        OAuth2Authentication authentication = ehrTokenServices.loadAuthentication(token.getValue());
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
        LOG.warn("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
        return getExceptionTranslator().translate(e);
    }

    @ExceptionHandler(ClientRegistrationException.class)
    public ResponseEntity<OAuth2Exception> handleClientRegistrationException(Exception e) throws Exception {
        LOG.warn("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
        return getExceptionTranslator().translate(new BadClientCredentialsException());
    }

    @ExceptionHandler(OAuth2Exception.class)
    public ResponseEntity<OAuth2Exception> handleException(OAuth2Exception e) throws Exception {
        LOG.warn("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
        return getExceptionTranslator().translate(e);
    }

    private ResponseEntity<AccessToken> getResponse(AccessToken accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        return new ResponseEntity<>(accessToken, headers, HttpStatus.OK);
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
        return ehrOAuth2ExceptionTranslator;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (this.authenticationManager == null) {
            this.authenticationManager = (AuthenticationManager) applicationContext.getBean("authenticationManager");
        }
    }
}
