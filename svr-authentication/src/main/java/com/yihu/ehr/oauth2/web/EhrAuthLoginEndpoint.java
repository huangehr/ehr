package com.yihu.ehr.oauth2.web;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.user.EhrUserSimple;
import com.yihu.ehr.oauth2.model.VerifyCode;
import com.yihu.ehr.oauth2.oauth2.EhrOAuth2ExceptionTranslator;
import com.yihu.ehr.oauth2.oauth2.EhrTokenGranter;
import com.yihu.ehr.oauth2.oauth2.EhrUserDetailsService;
import com.yihu.ehr.oauth2.oauth2.jdbc.EhrJdbcClientDetailsService;
import com.yihu.ehr.oauth2.oauth2.redis.EhrRedisTokenStore;
import com.yihu.ehr.oauth2.oauth2.redis.EhrRedisVerifyCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.endpoint.AbstractEndpoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by progr1mmer on 2018/4/3.
 */
@RestController
public class EhrAuthLoginEndpoint extends AbstractEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(EhrAuthLoginEndpoint.class);
    private static final String DEFAULT_GRANT_TYPE = "password";

    private OAuth2RequestFactory oAuth2RequestFactory;
    private OAuth2RequestValidator oAuth2RequestValidator = new DefaultOAuth2RequestValidator();

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private EhrJdbcClientDetailsService ehrJdbcClientDetailsService;
    @Autowired
    private EhrTokenGranter ehrTokenGranter;
    @Autowired
    private EhrOAuth2ExceptionTranslator ehrOAuth2ExceptionTranslator;
    @Autowired
    private EhrRedisTokenStore ehrRedisTokenStore;
    @Autowired
    private EhrUserDetailsService ehrUserDetailsService;
    @Autowired
    private EhrRedisVerifyCodeService ehrRedisVerifyCodeService;

    @PostConstruct
    private void init() {
        super.setTokenGranter(ehrTokenGranter);
    }

    @RequestMapping(value = ServiceApi.Authentication.Login, method = RequestMethod.POST)
    public ResponseEntity<EhrUserSimple> login(@RequestParam Map<String, String> parameters, HttpServletRequest request) {
        String client_id = parameters.get("client_id");
        String scope = parameters.get("scope");
        //检查基本参数
        if (StringUtils.isEmpty(client_id)) {
            throw new InvalidRequestException("Missing clientId");
        }
        Map<String, String> param = new HashMap<>();
        if (StringUtils.isEmpty(parameters.get("verify_code"))) {
            param.put("grant_type", DEFAULT_GRANT_TYPE);
            param.put("password", parameters.get("password"));
        } else {
            param.put("grant_type", "verify_code");
            param.put("verify_code", parameters.get("verify_code"));
        }
        param.put("client_id", client_id);
        param.put("scope", scope);
        param.put("username", parameters.get("username"));
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
        OAuth2AccessToken token = getTokenGranter().grant(param.get("grant_type"), tokenRequest);
        /*如果是移动端登陆则移除之前的token，
        在网关处通过HTTP状态码告知前端是过期（401）还是账号在别处登陆（403），
        实现同一账号只能在一处登陆*/
        if (request.getHeader("login-device") != null && request.getHeader("login-device").equals("mobile")) {
            ehrRedisTokenStore.removeAccessToken(token.getValue());
            ehrRedisTokenStore.removeRefreshToken(token.getRefreshToken().getValue());
            token = getTokenGranter().grant(DEFAULT_GRANT_TYPE, tokenRequest);
        }
        EhrUserSimple ehrUserSimple = ehrUserDetailsService.loadUserSimpleByUsername(parameters.get("username"));
        if (token == null) {
            throw new UnsupportedGrantTypeException("Unsupported grant type: " + DEFAULT_GRANT_TYPE);
        } else {
            ehrUserSimple.setAccessToken(token.getValue());
            ehrUserSimple.setTokenType(token.getTokenType());
            ehrUserSimple.setExpiresIn(token.getExpiresIn());
            ehrUserSimple.setRefreshToken(token.getRefreshToken().getValue());
            ehrUserSimple.setUser(parameters.get("username"));
            ehrUserSimple.setState(parameters.get("state"));
        }
        return getResponse(ehrUserSimple);
    }

    @RequestMapping(value = ServiceApi.Authentication.VerifyCode, method = RequestMethod.POST)
    public ResponseEntity<VerifyCode> verifyCode(@RequestParam Map<String, String> parameters) {
        String client_id = parameters.get("client_id");
        String username = parameters.get("username");
        VerifyCode verifyCode = new VerifyCode();
        verifyCode.setCode("DS2X");
        verifyCode.setExpiresIn(600);
        ehrRedisVerifyCodeService.store(client_id, username, "DS2X", 600000);
        return new ResponseEntity<>(verifyCode, HttpStatus.OK);
    }

    @RequestMapping(value = ServiceApi.Authentication.VerifyCodeExpire, method = RequestMethod.POST)
    public ResponseEntity<Integer> verifyCodeExpire(@RequestParam Map<String, String> parameters) {
        String client_id = parameters.get("client_id");
        String username = parameters.get("username");
        int expiresIn = ehrRedisVerifyCodeService.getExpireTime(client_id, username);
        return new ResponseEntity<>(expiresIn, HttpStatus.OK);
    }

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

    @Override
    public void afterPropertiesSet() throws Exception {

        Assert.state(ehrJdbcClientDetailsService != null, "ClientDetailsService must be provided");
        Assert.state(authenticationManager != null, "AuthenticationManager must be provided");

        oAuth2RequestFactory = new DefaultOAuth2RequestFactory(ehrJdbcClientDetailsService);
    }

    private ResponseEntity<EhrUserSimple> getResponse(EhrUserSimple ehrUserSimple) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cache-Control", "no-store");
        headers.set("Pragma", "no-cache");
        return new ResponseEntity<>(ehrUserSimple, headers, HttpStatus.OK);
    }

    @Override
    protected WebResponseExceptionTranslator getExceptionTranslator() {
        return ehrOAuth2ExceptionTranslator;
    }

}
