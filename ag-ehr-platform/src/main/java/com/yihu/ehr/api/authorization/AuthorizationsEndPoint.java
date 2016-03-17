package com.yihu.ehr.api.authorization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.model.TokenModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.service.oauth2.EhrClientDetailsService;
import com.yihu.ehr.service.oauth2.EhrTokenStoreService;
import com.yihu.ehr.service.oauth2.EhrUserDetailsService;
import com.yihu.ehr.util.encode.HashUtil;
import com.yihu.ehr.util.token.TokenUtil;
import com.yihu.ehr.utils.BasicAuthorizationExtractor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * 认证控制器。此控制器基于用户名/密码认证，即Basic Authorization，需要在HTTPS中提供用户名与密码。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.23 11:24
 */
@RestController
@RequestMapping(ApiVersion.Version1_0 + "/authorizations")
@Api(protocols = "https", value = "authorizations", description = "认证与授权服务。注意此API使用Basic认证。")
public class AuthorizationsEndPoint {
    private final static int TOKEN_LENGTH = 32;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EhrTokenStoreService tokenStoreService;

    @Autowired
    EhrUserDetailsService userDetailsService;

    @Autowired
    EhrClientDetailsService clientDetailsService;

    @ApiOperation(value = "获取用户所有应用授权", notes = "提供用户名/密码作为Basic验证")
    @RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public List<String> getAuthorizations() {
        return null;
    }

    @ApiOperation(value = "获取单个授权", notes = "提供用户名/密码作为Basic验证")
    @RequestMapping(value = "/{token_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public List<String> getAuthorization(@ApiParam(value = "token_id")
                                         @PathVariable("token_id") long tokenId) {
        return null;
    }

    @ApiOperation(value = "更新授权", notes = "提供用户名/密码作为Basic验证")
    @RequestMapping(value = "/{token_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.PUT)
    public List<String> updateAuthorization(@ApiParam(value = "token_id")
                                            @PathVariable(value = "token_id") long id,
                                            @ApiParam(value = "json")
                                            @RequestParam("json") String json) {
        return null;
    }

    @ApiOperation(value = "删除授权", notes = "提供用户名/密码作为Basic验证")
    @RequestMapping(value = "/{token_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.DELETE)
    public void deleteAuthorization(@ApiParam(value = "token_id")
                                    @PathVariable(value = "token_id") long id) {

    }

    @ApiOperation(value = "为用户的多个应用创建授权", notes = "提供用户名/密码作为Basic验证")
    @RequestMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST)
    public List<String> createAuthorizations(@ApiParam(value = "json")
                                             @RequestParam(value = "json") String json) {
        return null;
    }

    @ApiOperation(value = "为指定应用创建授权，若存在返回已有授权", notes = "提供Client Id/Secret作为Basic验证")
    @RequestMapping(value = "/clients/{client_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.PUT)
    public ResponseEntity<Json> createClientAuthorization(@ApiParam(value = "client_id", defaultValue = "kHAbVppx44")
                                                          @PathVariable("client_id")
                                                          String clientId,
                                                          @ApiParam(value = "info")
                                                          @RequestParam(value = "info")
                                                          String info,
                                                          HttpServletRequest request) throws JsonProcessingException {
        Pair<String, String> basic = BasicAuthorizationExtractor.extract(request.getHeader("Authorization"));
        if (!basic.getKey().equals(clientId))
            throw new InvalidParameterException("Basic authorization client id MUST be same with url path.");

        Map<String, Object> values;
        try {
            values = objectMapper.readValue(info, Map.class);
        } catch (IOException e) {
            throw new InvalidParameterException("'info' parameter must be json format");
        }

        String fingerprint = values.get("fingerprint").toString();
        fingerprint = fingerprint == null ? "" : fingerprint;

        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        if (!clientDetails.getClientSecret().equals(basic.getValue())) {
            throw new InvalidParameterException("Client id or secret incorrect.");
        }

        TokenModel token = createClientToken(clientId, fingerprint);
        if (token == null) {
            throw new AuthenticationServiceException("Create client token failed.");
        }

        return new ResponseEntity<Json>(new Json(objectMapper.writeValueAsString(token)), HttpStatus.OK);
    }

    @ApiOperation(value = "获取用户Token列表", notes = "提供用户名/密码作为Basic验证")
    @RequestMapping(value = "/users/{user_name}/tokens", method = RequestMethod.GET)
    public List<String> getTokens(@ApiParam("user_name")
                                  @PathVariable("user_name")
                                  String userName) {
        return null;
    }

    @ApiOperation(value = "获取用户单个Token", notes = "提供用户名/密码作为Basic验证")
    @RequestMapping(value = "/users/{user_name}/tokens/{id}", method = RequestMethod.GET)
    public String getToken(@ApiParam("user_name")
                           @PathVariable("user_name")
                           String userName,
                           @ApiParam("id")
                           @PathVariable("id")
                           String id) {
        return "";
    }

    private Map<String, String> appTokens = new HashMap<>();

    @ApiOperation(value = "为指定用户创建授权，若存在返回已有授权", notes = "提供用户名/密码作为Basic验证")
    @RequestMapping(value = "/users/{user_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.PUT)
    public TokenModel createUserAuthorization(@ApiParam(name = "user_name", value = "与Basic认证用户名一致")
                                          @PathVariable("user_name")
                                          String userName,
                                              @ApiParam(value = "info")
                                          @RequestParam(value = "info", required = false) String info,
                                              HttpServletRequest request) throws NoSuchAlgorithmException {

        Pair<String, String> basic = BasicAuthorizationExtractor.extract(request.getHeader("Authorization"));
        if (!basic.getKey().equals(userName)) throw new InvalidParameterException("Basic authorization user name MUST be same with url path.");

        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        if (userDetails == null || !HashUtil.hashStr(basic.getValue()).equals(userDetails.getPassword())) {
            throw new InvalidParameterException("User name or password incorrect.");
        }

        TokenModel tokenModel = new TokenModel();
        tokenModel.setToken(TokenUtil.genToken(TOKEN_LENGTH));
        tokenModel.setTokenLastEight(tokenModel.getToken().substring(tokenModel.getToken().length() - 8));
        tokenModel.setUpdatedAt(new Date());

        return tokenModel;
    }

    public TokenModel createClientToken(String clientId, String fingerprint) {
        String key = clientId + "-" + fingerprint;
        String token = appTokens.get(key);
        if (token == null) {
            token = TokenUtil.genToken(TOKEN_LENGTH);
            appTokens.put(key, token);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername("su");
        DefaultOAuth2AccessToken accessToken = (DefaultOAuth2AccessToken) tokenStoreService.readAccessToken(token);
        if (null == accessToken) {
            Set<String> scopes = new HashSet<>();
            scopes.add("read");

            accessToken = new DefaultOAuth2AccessToken(token);
            accessToken.setExpiration(new GregorianCalendar(2099, 1, 1).getTime());
            accessToken.setScope(scopes);

            Set<String> resourceIds = new HashSet<>();
            resourceIds.add("ehr");

            OAuth2Request auth2Request = new OAuth2Request(null,
                    "simplified-esb-app",
                    userDetails.getAuthorities(),
                    true,
                    scopes,
                    resourceIds,
                    "http://www.yihu.com",
                    null,
                    null);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    new User(userDetails.getUsername(),
                            userDetails.getPassword(),
                            userDetails.isEnabled(),
                            userDetails.isAccountNonExpired(),
                            userDetails.isCredentialsNonExpired(),
                            userDetails.isAccountNonLocked(), userDetails.getAuthorities()),
                    null,
                    userDetails.getAuthorities());

            OAuth2Authentication auth2Authentication = new OAuth2Authentication(auth2Request, authentication);

            tokenStoreService.storeAccessToken(accessToken, auth2Authentication);
        }

        TokenModel tokenModel = new TokenModel();
        tokenModel.setId(1);
        tokenModel.setToken(token);
        tokenModel.setTokenLastEight(token.substring(token.length() - 8));
        tokenModel.setUpdatedAt(new Date());

        return tokenModel;
    }
}
