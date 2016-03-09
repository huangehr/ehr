package com.yihu.ehr.api.authorization;

import com.yihu.ehr.api.model.MToken;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.service.oauth2.EhrTokenStoreService;
import com.yihu.ehr.service.oauth2.EhrUserDetailsService;
import com.yihu.ehr.util.encode.HashUtil;
import com.yihu.ehr.utils.BasicAuthorizationExtractor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    EhrTokenStoreService tokenStoreService;

    @Autowired
    EhrUserDetailsService userDetailsService;

    @ApiOperation(value = "获取用户所有应用授权", notes = "提供用户名/密码作为Basic验证")
    @RequestMapping(value = "", produces = "application/json", method = RequestMethod.GET)
    public List<String> getAuthorizations() {
        return null;
    }

    @ApiOperation(value = "获取单个授权", notes = "提供用户名/密码作为Basic验证")
    @RequestMapping(value = "/{token_id}", produces = "application/json", method = RequestMethod.GET)
    public List<String> getAuthorization(@ApiParam(value = "token_id")
                                         @PathVariable("token_id") long tokenId) {
        return null;
    }

    @ApiOperation(value = "更新授权", notes = "提供用户名/密码作为Basic验证")
    @RequestMapping(value = "/{token_id}", produces = "application/json", method = RequestMethod.PUT)
    public List<String> updateAuthorization(@ApiParam(value = "token_id")
                                            @PathVariable(value = "token_id") long id,
                                            @ApiParam(value = "json")
                                            @RequestParam("json") String json) {
        return null;
    }

    @ApiOperation(value = "删除授权", notes = "提供用户名/密码作为Basic验证")
    @RequestMapping(value = "/{token_id}", produces = "application/json", method = RequestMethod.DELETE)
    public void deleteAuthorization(@ApiParam(value = "token_id")
                                    @PathVariable(value = "token_id") long id) {

    }

    @ApiOperation(value = "为用户的多个应用创建授权", notes = "提供用户名/密码作为Basic验证")
    @RequestMapping(value = "", produces = "application/json", method = RequestMethod.POST)
    public List<String> createAuthorizations(@ApiParam(value = "json")
                                             @RequestParam(value = "json") String json) {
        return null;
    }

    @ApiOperation(value = "为指定应用创建授权，若存在返回已有授权", notes = "提供用户名/密码作为Basic验证")
    @RequestMapping(value = "/clients/{client_id}", produces = "application/json", method = RequestMethod.PUT)
    public List<String> createClientAuthorization(@ApiParam("client_id")
                                                  @PathVariable("client_id")
                                                  String clientId,
                                                  @ApiParam(value = "info")
                                                  @RequestParam("info") String info) {
        return null;
    }

    //------ 用户Token ------

    @ApiOperation(value = "获取用户Token列表", notes = "提供用户名/密码作为Basic验证")
    @RequestMapping(value = "/users/{user_name}/tokens", method = RequestMethod.GET)
    public List<String> getTokens(@ApiParam("user_name")
                                  @PathVariable("user_name")
                                  String userName) {
        return null;
    }

    @ApiOperation(value = "获取用户单个Token", notes = "提供用户名/密码作为Basic验证")
    @RequestMapping(value = "/users/{user_name}/tokens/{id}", method = RequestMethod.GET)
    public String getToken(@ApiParam("id")
                           @PathVariable("id")
                           String id) {
        return "";
    }

    // 临时固化一个Token
    private Map<String, String> userTokens = new HashMap<>();

    @ApiOperation(value = "为指定用户创建授权，若存在返回已有授权", notes = "提供用户名/密码作为Basic验证")
    @RequestMapping(value = "/users/{user_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.PUT)
    public MToken createUserAuthorization(@ApiParam(name = "user_name", value = "与Basic认证用户名一致")
                                          @PathVariable("user_name")
                                          String userName,
                                          @ApiParam(value = "info")
                                          @RequestParam(value = "info", required = false) String info,
                                          HttpServletRequest request) throws NoSuchAlgorithmException {

        Pair<String, String> basic = BasicAuthorizationExtractor.extract(request.getHeader("Authorization"));
        if (!basic.getKey().equals(userName)) throw new InvalidParameterException("用户名不一致");

        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        if (userDetails == null || !HashUtil.hashStr(basic.getValue()).equals(userDetails.getPassword())) {
            throw new InvalidParameterException("用户名或密码错误");
        }

        return createTempToken(userDetails);
    }

    public MToken createTempToken(UserDetails userDetails) {

        String token = userTokens.get(userDetails.getUsername());
        if (token == null) {
            token = UUID.randomUUID().toString();
            userTokens.put(userDetails.getUsername(), token);
        }

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
                    "client-with-registered-redirect",
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

        MToken mToken = new MToken();
        mToken.setId(1);
        mToken.setToken(token);
        mToken.setToken_last_eight(token.substring(token.length() - 8));
        mToken.setUpdated_at(new Date());

        return mToken;
    }
}
