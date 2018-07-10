package com.yihu.ehr.oauth2.web;

import com.yihu.ehr.oauth2.oauth2.EhrOAuth2ExceptionTranslator;
import com.yihu.ehr.oauth2.oauth2.EhrTokenGranter;
import com.yihu.ehr.oauth2.oauth2.EhrUserDetailsService;
import com.yihu.ehr.oauth2.oauth2.jdbc.*;
import com.yihu.ehr.oauth2.oauth2.redis.EhrRedisApiAccessValidator;
import com.yihu.ehr.oauth2.oauth2.redis.EhrRedisTokenStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerEndpointsConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.approval.DefaultUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.endpoint.*;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenRequest;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.security.Principal;
import java.util.*;

/**
 * Ehr Oauth2 Server
 * Sso & authorization_code implicit Endpoint
 *
 * 自定义授权入口。由于Spring使用了自带授权入口Bean{@link AuthorizationEndpoint}，但其使用@FrameworkEndpoint标识，
 * 因此用户自定义的Controller不会与之冲突，放心地使用常规方式创建新的Controller即可。
 * <p>
 * http://stackoverflow.com/questions/29345508/spring-oauth2-custom-oauth-approval-page-at-oauth-authorize
 *
 * @author Sand
 * @version 1.0
 * @created 2016.03.04 10:56
 *
 * Modify by Progr1mmer on 2018/01/08.
 *
 */
@Controller
@SessionAttributes("authorizationRequest")
public class EhrAuthorizationEndpoint extends AbstractEndpoint {

    private static final Logger LOG = LoggerFactory.getLogger(EhrAuthorizationEndpoint.class);

    @Autowired
    private EhrOAuth2ExceptionTranslator ehrOAuth2ExceptionTranslator;
    @Autowired
    private AuthorizationCodeServices inMemoryAuthorizationCodeServices;
    /*@Autowired
    private EhrJdbcTokenStore ehrJdbcTokenStore;*/
    @Autowired
    private EhrRedisTokenStore ehrRedisTokenStore;
    @Autowired
    private EhrJdbcClientDetailsService ehrJdbcClientDetailsService;
    @Autowired
    private EhrJdbcAccessUrlService ehrJDBCAccessUrlService;
    @Autowired
    private EhrJdbcUserSecurityService ehrJDBCUserSecurityService;
    @Autowired
    private EhrUserDetailsService ehrUserDetailsService;
    @Autowired
    private EhrTokenGranter ehrTokenGranter;
    @Autowired
    private EhrRedisApiAccessValidator ehrRedisApiAccessValidator;

    private RedirectResolver redirectResolver = new DefaultRedirectResolver();
    private UserApprovalHandler userApprovalHandler = new DefaultUserApprovalHandler();
    private OAuth2RequestValidator oauth2RequestValidator = new DefaultOAuth2RequestValidator();
    private Object implicitLock = new Object();
    private String userApprovalPage = "forward:/oauth/confirm_access";
    private String errorPage = "forward:/oauth/error";

    @Autowired
    private AuthorizationServerEndpointsConfiguration configuration;

    @PostConstruct
    public void init() throws Exception {
        AuthorizationServerEndpointsConfigurer configurer = configuration.getEndpointsConfigurer();
        configurer.setClientDetailsService(ehrJdbcClientDetailsService);
        configurer.tokenStore(ehrRedisTokenStore);
        configurer.authorizationCodeServices(inMemoryAuthorizationCodeServices);
        FrameworkEndpointHandlerMapping mapping = configuration.getEndpointsConfigurer().getFrameworkEndpointHandlerMapping();
        this.setUserApprovalPage(extractPath(mapping, "/oauth/confirm_access"));
        this.setProviderExceptionHandler(configurer.getExceptionTranslator());
        this.setErrorPage(extractPath(mapping, "/oauth/error"));
        this.setTokenGranter(configurer.getTokenGranter());
        this.setClientDetailsService(configurer.getClientDetailsService());
        this.setAuthorizationCodeServices(inMemoryAuthorizationCodeServices);
        this.setOAuth2RequestFactory(configurer.getOAuth2RequestFactory());
        this.setOAuth2RequestValidator(configurer.getOAuth2RequestValidator());
        this.setUserApprovalHandler(configurer.getUserApprovalHandler());
    }

    /**
     * oauth2
     * @param model
     * @param sessionStatus
     * @param principal
     * @param parameters
     * @return
     */
    @RequestMapping(value = "/oauth/authorize")
    public ModelAndView authorize(Map<String, Object> model, SessionStatus sessionStatus, Principal principal, @RequestParam Map<String, String> parameters){
        // Pull out the authorization request first, using the OAuth2RequestFactory. All further logic should
        // query off of the authorization request instead of referring back to the parameters map. The contents of the
        // parameters map will be stored without change in the AuthorizationRequest object once it is created.
        AuthorizationRequest authorizationRequest = getOAuth2RequestFactory().createAuthorizationRequest(parameters);
        Set<String> responseTypes = authorizationRequest.getResponseTypes();
        if (!responseTypes.contains("token") && !responseTypes.contains("code")) {
            sessionStatus.setComplete();
            throw new UnsupportedResponseTypeException("Unsupported response types: " + responseTypes);
        }
        if (authorizationRequest.getClientId() == null) {
            sessionStatus.setComplete();
            throw new InvalidClientException("A client id must be provided");
        }
        try {
            String userName = "unAuth";
            if (!(principal instanceof Authentication) || !((Authentication) principal).isAuthenticated()) {
                if (parameters.containsKey("pk")) {
                    String pk = parameters.get("pk");
                    String keyId = ehrJDBCUserSecurityService.getDefaultKeyIdSelectStatement(pk);
                    String userId = ehrJDBCUserSecurityService.getDefaultUserIdByKeyIdSelectStatement(keyId);
                    userName = ehrJDBCUserSecurityService.getDefaultUserNameByUserId(userId);
                    UserDetails userDetails = ehrUserDetailsService.loadUserByUsername(userName);
                    if (StringUtils.isEmpty(keyId) || StringUtils.isEmpty(userId) || StringUtils.isEmpty(userName)) {
                        throw new InsufficientAuthenticationException("Illegal pk");
                    }
                    UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                    securityContext.setAuthentication(userAuth);
                    SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
                    securityContextHolderStrategy.setContext(securityContext);
                    principal = userAuth;
                } else {
                    throw new InsufficientAuthenticationException("A pk must be present.");
                }
            }
            ClientDetails client = getClientDetailsService().loadClientByClientId(authorizationRequest.getClientId());
            // The resolved redirect URI is either the redirect_uri from the parameters or the one from
            // clientDetails. Either way we need to store it on the AuthorizationRequest.
            //String redirectUriParameter = authorizationRequest.getRequestParameters().get(OAuth2Utils.REDIRECT_URI);
            String redirectUriParameter = authorizationRequest.getRequestParameters().get(OAuth2Utils.REDIRECT_URI);
            String resolvedRedirect = redirectResolver.resolveRedirect(redirectUriParameter, client);
            if (!StringUtils.hasText(resolvedRedirect)) {
                throw new RedirectMismatchException("A redirectUri must be either supplied or preconfigured in the ClientDetails");
            } else {
                authorizationRequest.setRedirectUri(resolvedRedirect);
                // We intentionally only validate the parameters requested by the client (ignoring any data that may have
                // been added to the request by the manager).
                oauth2RequestValidator.validateScope(authorizationRequest, client);
                // Some systems may allow for approval decisions to be remembered or approved by default. Check for
                // such logic here, and set the approved flag on the authorization request accordingly.
                authorizationRequest = userApprovalHandler.checkForPreApproval(authorizationRequest,
                        (Authentication) principal);
                // TODO: is this call necessary?
                boolean approved = userApprovalHandler.isApproved(authorizationRequest, (Authentication) principal);
                authorizationRequest.setApproved(approved);
                // Validation is all done, so we can check for auto approval...
                if (authorizationRequest.isApproved()) {
                    if (responseTypes.contains("token")) {
                        ModelAndView implicitModelAndView = getImplicitGrantResponse(authorizationRequest);
                        ehrRedisApiAccessValidator.putVerificationApi(authorizationRequest.getClientId(), userName);
                        return implicitModelAndView;
                    }
                    if (responseTypes.contains("code")) {
                        return new ModelAndView(getAuthorizationCodeResponse(authorizationRequest,
                                (Authentication) principal));
                    }
                }
                // Place auth request into the model so that it is stored in the session
                // for approveOrDeny to use. That way we make sure that auth request comes from the session,
                // so any auth request parameters passed to approveOrDeny will be ignored and retrieved from the session.
                model.put("authorizationRequest", authorizationRequest);
                return getUserApprovalPageResponse(model, authorizationRequest, (Authentication) principal);
            }
        } catch (RuntimeException e) {
            sessionStatus.setComplete();
            throw e;
        }
    }

    /**
     * 单点登陆 - 简化模式
     * @param model
     * @param sessionStatus
     * @param principal
     * @param parameters
     * @return
     */
    @RequestMapping(value = "/oauth/sso")
    public ModelAndView sso (Map<String, Object> model, SessionStatus sessionStatus, Principal principal, @RequestParam Map<String, String> parameters){
        // Pull out the authorization request first, using the OAuth2RequestFactory. All further logic should
        // query off of the authorization request instead of referring back to the parameters map. The contents of the
        // parameters map will be stored without change in the AuthorizationRequest object once it is created.
        AuthorizationRequest authorizationRequest = getOAuth2RequestFactory().createAuthorizationRequest(parameters);
        Set<String> responseTypes = authorizationRequest.getResponseTypes();
        if (!responseTypes.contains("token")) {
            sessionStatus.setComplete();
            throw new UnsupportedResponseTypeException("Unsupported response types: " + responseTypes);
        }
        if (authorizationRequest.getClientId() == null) {
            sessionStatus.setComplete();
            throw new InvalidClientException("A client id must be provided");
        }
        try {
            if (!(principal instanceof Authentication) || !((Authentication) principal).isAuthenticated()) {
                if (parameters.containsKey("token")) {
                    String token = parameters.get("token");
                    OAuth2Authentication authentication = ehrRedisTokenStore.readAuthentication(token);
                    if (authentication != null) {
                        String userName = authentication.getName();
                        UserDetails userDetails = ehrUserDetailsService.loadUserByUsername(userName);
                        UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
                        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                        securityContext.setAuthentication(userAuth);
                        SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
                        securityContextHolderStrategy.setContext(securityContext);
                        principal = userAuth;
                    } else {
                        throw new InsufficientAuthenticationException("User must be authenticated with Spring Security before authorization can be completed.");
                    }
                } else {
                    throw new InsufficientAuthenticationException("Token must be provided.");
                }
            }
            ClientDetails client = getClientDetailsService().loadClientByClientId(authorizationRequest.getClientId());
            // The resolved redirect URI is either the redirect_uri from the parameters or the one from
            // clientDetails. Either way we need to store it on the AuthorizationRequest.
            //String redirectUriParameter = authorizationRequest.getRequestParameters().get(OAuth2Utils.REDIRECT_URI);
            String redirectUriParameter = ehrJDBCAccessUrlService.getValidUrl(authorizationRequest.getClientId(), authorizationRequest.getRequestParameters().get(OAuth2Utils.REDIRECT_URI));
            String resolvedRedirect = redirectResolver.resolveRedirect(redirectUriParameter, client);
            if (!StringUtils.hasText(resolvedRedirect)) {
                throw new RedirectMismatchException(
                        "A redirectUri must be either supplied or preconfigured in the ClientDetails");
            }
            authorizationRequest.setRedirectUri(ehrJDBCAccessUrlService.getRealUrl(authorizationRequest.getClientId(), authorizationRequest.getRequestParameters().get(OAuth2Utils.REDIRECT_URI)));
            // We intentionally only validate the parameters requested by the client (ignoring any data that may have
            // been added to the request by the manager).
            oauth2RequestValidator.validateScope(authorizationRequest, client);
            // Some systems may allow for approval decisions to be remembered or approved by default. Check for
            // such logic here, and set the approved flag on the authorization request accordingly.
            authorizationRequest = userApprovalHandler.checkForPreApproval(authorizationRequest,
                    (Authentication) principal);
            // TODO: is this call necessary?
            boolean approved = userApprovalHandler.isApproved(authorizationRequest, (Authentication) principal);
            authorizationRequest.setApproved(approved);
            // Validation is all done, so we can check for auto approval...
            if (authorizationRequest.isApproved()) {
                if (responseTypes.contains("token")) {
                    return getImplicitGrantResponse(authorizationRequest);
                }
            }
            // Place auth request into the model so that it is stored in the session
            // for approveOrDeny to use. That way we make sure that auth request comes from the session,
            // so any auth request parameters passed to approveOrDeny will be ignored and retrieved from the session.
            model.put("authorizationRequest", authorizationRequest);
            return getUserApprovalPageResponse(model, authorizationRequest, (Authentication) principal);
        } catch (RuntimeException e) {
            sessionStatus.setComplete();
            throw e;
        }
    }

    @RequestMapping("/oauth/confirm_access")
    public ModelAndView getAccessConfirmation(Map<String, Object> model, HttpServletRequest request) throws Exception {
        if (request.getAttribute("_csrf") != null) {
            model.put("_csrf", request.getAttribute("_csrf"));
        } else {
            model.put("_csrf", "");
        }

        String scopes[] = request.getParameter("scope") == null ? new String[0] : request.getParameter("scope").split("\\+");
        model.put("scopes", scopes);

        return new ModelAndView("/oauth/confirm_access", model);
    }

    @RequestMapping("/oauth/error")
    public ModelAndView handleError(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<String, Object>();
        Object error = request.getAttribute("error");
        if (error == null) {
            error = Collections.singletonMap("summary", "Unknown error");
        }
        model.put("error", error);
        return new ModelAndView("/oauth/error", model);
    }


    public void setUserApprovalPage(String userApprovalPage) {
        this.userApprovalPage = userApprovalPage;
    }

    public void setAuthorizationCodeServices(AuthorizationCodeServices authorizationCodeServices) {
        this.inMemoryAuthorizationCodeServices = authorizationCodeServices;
    }

    public void setRedirectResolver(RedirectResolver redirectResolver) {
        this.redirectResolver = redirectResolver;
    }

    public void setOAuth2RequestValidator(OAuth2RequestValidator oauth2RequestValidator) {
        this.oauth2RequestValidator = oauth2RequestValidator;
    }

    public void setUserApprovalHandler(UserApprovalHandler userApprovalHandler) {
        this.userApprovalHandler = userApprovalHandler;
    }

    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }

    private String extractPath(FrameworkEndpointHandlerMapping mapping, String page) {
        String path = mapping.getPath(page);
        if (path.contains(":")) {
            return path;
        }
        return "forward:" + path;
    }

    // We need explicit approval from the user.
    private ModelAndView getUserApprovalPageResponse(Map<String, Object> model,
                                                     AuthorizationRequest authorizationRequest, Authentication principal) {
        logger.debug("Loading user approval page: " + userApprovalPage);
        model.putAll(userApprovalHandler.getUserApprovalRequest(authorizationRequest, principal));
        return new ModelAndView(userApprovalPage, model);
    }

    private View getAuthorizationCodeResponse(AuthorizationRequest authorizationRequest, Authentication authUser) {
        try {
            //url+code
            String url=getSuccessfulRedirect(authorizationRequest,
                    generateCode(authorizationRequest, authUser));
            //重定向到目标网站
            return new RedirectView(url,false, true, false);
        } catch (OAuth2Exception e) {
            return new RedirectView(getUnsuccessfulRedirect(authorizationRequest, e, false), false, true, false);
        }
    }

    private String appendAccessToken(AuthorizationRequest authorizationRequest, OAuth2AccessToken accessToken) {
        Map<String, Object> vars = new LinkedHashMap<String, Object>();
        Map<String, String> keys = new HashMap<String, String>();

        if (accessToken == null) {
            throw new InvalidRequestException("An implicit grant could not be made");
        }

        vars.put("access_token", accessToken.getValue());
        vars.put("token_type", accessToken.getTokenType());
        String state = authorizationRequest.getState();

        if (state != null) {
            vars.put("state", state);
        }
        Date expiration = accessToken.getExpiration();
        if (expiration != null) {
            long expires_in = (expiration.getTime() - System.currentTimeMillis()) / 1000;
            vars.put("expires_in", expires_in);
        }
        Map<String,String> map = authorizationRequest.getRequestParameters();
        vars.put("redirect_uri", map.get("redirect_uri"));


        String originalScope = authorizationRequest.getRequestParameters().get(OAuth2Utils.SCOPE);
        if (originalScope == null || !OAuth2Utils.parseParameterList(originalScope).equals(accessToken.getScope())) {
            vars.put("scope", OAuth2Utils.formatParameterList(accessToken.getScope()));
        }
        Map<String, Object> additionalInformation = accessToken.getAdditionalInformation();
        for (String key : additionalInformation.keySet()) {
            Object value = additionalInformation.get(key);
            if (value != null) {
                keys.put("extra_" + key, key);
                vars.put("extra_" + key, value);
            }
        }

        // Do not include the refresh token (even if there is one)
        return append(authorizationRequest.getRedirectUri(), vars, keys, true);
    }

    private String generateCode(AuthorizationRequest authorizationRequest, Authentication authentication)
            throws AuthenticationException {

        try {

            OAuth2Request storedOAuth2Request = getOAuth2RequestFactory().createOAuth2Request(authorizationRequest);

            OAuth2Authentication combinedAuth = new OAuth2Authentication(storedOAuth2Request, authentication);
            String code = inMemoryAuthorizationCodeServices.createAuthorizationCode(combinedAuth);
            return code;

        } catch (OAuth2Exception e) {

            if (authorizationRequest.getState() != null) {
                e.addAdditionalInformation("state", authorizationRequest.getState());
            }

            throw e;

        }
    }

    private String getSuccessfulRedirect(AuthorizationRequest authorizationRequest, String authorizationCode) {

        if (authorizationCode == null) {
            throw new IllegalStateException("No authorization code found in the current request scope.");
        }

        Map<String, String> query = new LinkedHashMap<String, String>();
        query.put("code", authorizationCode);

        String state = authorizationRequest.getState();
        if (state != null) {
            query.put("state", state);
        }

        return append(authorizationRequest.getRedirectUri(), query, false);
    }

    private String getUnsuccessfulRedirect(AuthorizationRequest authorizationRequest, OAuth2Exception failure,
                                           boolean fragment) {

        if (authorizationRequest == null || authorizationRequest.getRedirectUri() == null) {
            // we have no redirect for the user. very sad.
            throw new UnapprovedClientAuthenticationException("Authorization failure, and no redirect URI.", failure);
        }

        Map<String, String> query = new LinkedHashMap<String, String>();

        query.put("error", failure.getOAuth2ErrorCode());
        query.put("error_description", failure.getMessage());

        if (authorizationRequest.getState() != null) {
            query.put("state", authorizationRequest.getState());
        }

        if (failure.getAdditionalInformation() != null) {
            for (Map.Entry<String, String> additionalInfo : failure.getAdditionalInformation().entrySet()) {
                query.put(additionalInfo.getKey(), additionalInfo.getValue());
            }
        }

        return append(authorizationRequest.getRedirectUri(), query, fragment);
    }

    private String append(String base, Map<String, ?> query, boolean fragment) {
        return append(base, query, null, fragment);
    }

    private String append(String base, Map<String, ?> query, Map<String, String> keys, boolean fragment) {

        UriComponentsBuilder template = UriComponentsBuilder.newInstance();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(base);
        URI redirectUri;
        try {
            // assume it's encoded to start with (if it came in over the wire)
            redirectUri = builder.build(true).toUri();
        } catch (Exception e) {
            // ... but allow client registrations to contain hard-coded non-encoded values
            redirectUri = builder.build().toUri();
            builder = UriComponentsBuilder.fromUri(redirectUri);
        }
        template.scheme(redirectUri.getScheme()).port(redirectUri.getPort()).host(redirectUri.getHost())
                .userInfo(redirectUri.getUserInfo()).path(redirectUri.getPath());

        if (fragment) {
            StringBuilder values = new StringBuilder();
            if (redirectUri.getFragment() != null) {
                String append = redirectUri.getFragment();
                values.append(append);
            }
            for (String key : query.keySet()) {
                if (values.length() > 0) {
                    values.append("&");
                }
                String name = key;
                if (keys != null && keys.containsKey(key)) {
                    name = keys.get(key);
                }
                values.append(name + "={" + key + "}");
            }
            if (values.length() > 0) {
                template.fragment(values.toString());
            }
            UriComponents encoded = template.build().expand(query).encode();
            builder.fragment(encoded.getFragment());
        } else {
            for (String key : query.keySet()) {
                String name = key;
                if (keys != null && keys.containsKey(key)) {
                    name = keys.get(key);
                }
                template.queryParam(name, "{" + key + "}");
            }
            template.fragment(redirectUri.getFragment());
            UriComponents encoded = template.build().expand(query).encode();
            builder.query(encoded.getQuery());
        }

        return builder.build().toUriString();

    }

    // We can grant a token and return it with implicit approval.
    private ModelAndView getImplicitGrantResponse(AuthorizationRequest authorizationRequest) {
        try {
            TokenRequest tokenRequest = getOAuth2RequestFactory().createTokenRequest(authorizationRequest, "implicit");
            OAuth2Request storedOAuth2Request = getOAuth2RequestFactory().createOAuth2Request(authorizationRequest);
            OAuth2AccessToken accessToken = getAccessTokenForImplicitGrant(tokenRequest, storedOAuth2Request);
            if (accessToken == null) {
                throw new UnsupportedResponseTypeException("Unsupported response type: token");
            }

            return new ModelAndView(new RedirectView(appendAccessToken(authorizationRequest, accessToken), false, true,
                    false));
        } catch (OAuth2Exception e) {
            return new ModelAndView(new RedirectView(getUnsuccessfulRedirect(authorizationRequest, e, true), false,
                    true, false));
        }
    }

    private OAuth2AccessToken getAccessTokenForImplicitGrant(TokenRequest tokenRequest, OAuth2Request storedOAuth2Request) {
        OAuth2AccessToken accessToken = null;
        // These 1 method calls have to be atomic, otherwise the ImplicitGrantService can have a race condition where
        // one thread removes the token request before another has a chance to redeem it.
        synchronized (this.implicitLock) {
            accessToken = getTokenGranter().grant("implicit", new ImplicitTokenRequest(tokenRequest, storedOAuth2Request));
        }
        return accessToken;
    }

    @Override
    protected TokenGranter getTokenGranter() {
        return this.ehrTokenGranter;
    }

    @Override
    protected WebResponseExceptionTranslator getExceptionTranslator() {
        return ehrOAuth2ExceptionTranslator;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<OAuth2Exception> handleException(Exception e) throws Exception {
        LOG.warn("Handling error: " + e.getClass().getSimpleName() + ", " + e.getMessage());
        return getExceptionTranslator().translate(e);
    }
}
