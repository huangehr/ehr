package com.yihu.ehr.oauth2.oauth2;

import com.yihu.ehr.oauth2.oauth2.jdbc.EhrJdbcUserSecurityService;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.implicit.ImplicitGrantService;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Token授权器，根据请求创建Token给客户端。
 *
 * @author Progr1mmer
 * @created 2018.01.09
 */
public class EhrTokenGranter implements TokenGranter {
    private final Map<String, TokenGranter> tokenGranters = new HashMap<>();

    public EhrTokenGranter(AuthenticationManager authenticationManager,
                           AuthorizationServerTokenServices tokenServices,
                           AuthorizationCodeServices authorizationCodeServices,
                           ClientDetailsService clientDetailsService,
                           OAuth2RequestFactory requestFactory,
                           EhrJdbcUserSecurityService ehrJDBCUserSecurityService,
                           EhrUserDetailsService ehrUserDetailsService) {

        tokenGranters.put(EhrAuthorizationCodeGranter.GRANT_TYPE,
                new EhrAuthorizationCodeGranter(
                        tokenServices,
                        authorizationCodeServices,
                        clientDetailsService,
                        requestFactory,
                        ehrJDBCUserSecurityService,
                        ehrUserDetailsService
                ));

        tokenGranters.put(EhrResourceOwnerPasswordTokenGranter.GRANT_TYPE,
                new EhrResourceOwnerPasswordTokenGranter(
                        authenticationManager,
                        tokenServices,
                        clientDetailsService,
                        requestFactory
                ));

        tokenGranters.put(EhrRefreshTokenGranter.GRANT_TYPE,
                new EhrRefreshTokenGranter(
                        tokenServices,
                        clientDetailsService,
                        requestFactory
                ));

        tokenGranters.put(EhrImplicitTokenGranter.GRANT_TYPE,
                new EhrImplicitTokenGranter(
                        tokenServices,
                        clientDetailsService,
                        requestFactory
                ));
    }

    public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
        if (tokenGranters.get(grantType) != null) {
            OAuth2AccessToken grant = tokenGranters.get(grantType).grant(grantType, tokenRequest);
            return grant;
        }
        return null;
    }

    /**
     * authorization_code模式Token授权器。
     */
    public static class EhrAuthorizationCodeGranter extends AbstractTokenGranter {
        public static final String GRANT_TYPE = "authorization_code";

        private final AuthorizationCodeServices authorizationCodeServices;
        // Ehr Properties
        private EhrJdbcUserSecurityService ehrJDBCUserSecurityService;
        private EhrUserDetailsService ehrUserDetailsService;

        public EhrAuthorizationCodeGranter(AuthorizationServerTokenServices tokenServices,
                                           AuthorizationCodeServices authorizationCodeServices,
                                           ClientDetailsService clientDetailsService,
                                           OAuth2RequestFactory requestFactory,
                                           EhrJdbcUserSecurityService ehrJDBCUserSecurityService,
                                           EhrUserDetailsService ehrUserDetailsService) {
            this(tokenServices, authorizationCodeServices, clientDetailsService, requestFactory, GRANT_TYPE);
            this.ehrUserDetailsService = ehrUserDetailsService;
            this.ehrJDBCUserSecurityService = ehrJDBCUserSecurityService;
        }

        protected EhrAuthorizationCodeGranter(AuthorizationServerTokenServices tokenServices,
                                              AuthorizationCodeServices authorizationCodeServices,
                                              ClientDetailsService clientDetailsService,
                                              OAuth2RequestFactory requestFactory,
                                              String grantType) {
            super(tokenServices, clientDetailsService, requestFactory, grantType);
            this.authorizationCodeServices = authorizationCodeServices;
        }

        @Override
        protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

            Map<String, String> parameters = tokenRequest.getRequestParameters();
            String authorizationCode = parameters.get("code");
            String redirectUri = parameters.get(OAuth2Utils.REDIRECT_URI);

            if (authorizationCode == null) {
                throw new InvalidRequestException("An authorization code must be supplied.");
            }

            OAuth2Authentication storedAuth = authorizationCodeServices.consumeAuthorizationCode(authorizationCode);
            if (storedAuth == null) {
                throw new InvalidGrantException("Invalid authorization code: " + authorizationCode);
            }

            OAuth2Request pendingOAuth2Request = storedAuth.getOAuth2Request();
            String oauthUri;
            if(pendingOAuth2Request.getRedirectUri().indexOf("?") != -1) {
                oauthUri = pendingOAuth2Request.getRedirectUri().substring(0, pendingOAuth2Request.getRedirectUri().indexOf("?"));
            }else {
                oauthUri = pendingOAuth2Request.getRedirectUri();
            }
            // https://jira.springsource.org/browse/SECOAUTH-333
            // This might be null, if the authorization was done without the redirect_uri parameter
            String redirectUriApprovalParameter = pendingOAuth2Request.getRequestParameters().get(
                    OAuth2Utils.REDIRECT_URI);
            if ((redirectUri != null || redirectUriApprovalParameter != null)
                    && !oauthUri.equals(redirectUri)) {
                throw new RedirectMismatchException("Redirect URI mismatch.");
            }

            String pendingClientId = pendingOAuth2Request.getClientId();
            String clientId = tokenRequest.getClientId();
            if (clientId != null && !clientId.equals(pendingClientId)) {
                // just a sanity check.
                throw new InvalidClientException("Client ID mismatch");
            }

            // Secret is not required in the authorization request, so it won't be available
            // in the pendingAuthorizationRequest. We do want to check that a secret is provided
            // in the token request, but that happens elsewhere.

            Map<String, String> combinedParameters = new HashMap<String, String>(pendingOAuth2Request
                    .getRequestParameters());
            // Combine the parameters adding the new ones last so they override if there are any clashes
            combinedParameters.putAll(parameters);

            // Make a new stored request with the combined parameters
            OAuth2Request finalStoredOAuth2Request = pendingOAuth2Request.createOAuth2Request(combinedParameters);

            //弃用当前方法，再次通过pk获取当前请求的用户信息
            //Authentication userAuth = storedAuth.getUserAuthentication();

            String pk = combinedParameters.get("pk");
            String keyId = ehrJDBCUserSecurityService.getDefaultKeyIdSelectStatement(pk);
            String userId = ehrJDBCUserSecurityService.getDefaultUserIdByKeyIdSelectStatement(keyId);
            String userName = ehrJDBCUserSecurityService.getDefaultUserNameByUserId(userId);
            UserDetails userDetails = ehrUserDetailsService.loadUserByUsername(userName);
            if (StringUtils.isEmpty(keyId) || StringUtils.isEmpty(userId) || StringUtils.isEmpty(userName)) {
                throw new InsufficientAuthenticationException("Illegal pk");
            }
            UsernamePasswordAuthenticationToken userToken = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), userDetails.getPassword(), userDetails.getAuthorities());
            return new OAuth2Authentication(finalStoredOAuth2Request, userToken);
        }
    }

    public static class EhrResourceOwnerPasswordTokenGranter extends AbstractTokenGranter {
        private static final String GRANT_TYPE = "password";

        private final AuthenticationManager authenticationManager;

        public EhrResourceOwnerPasswordTokenGranter(AuthenticationManager authenticationManager,
                                                 AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
            this(authenticationManager, tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        }

        protected EhrResourceOwnerPasswordTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices,
                                                    ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
            super(tokenServices, clientDetailsService, requestFactory, grantType);
            this.authenticationManager = authenticationManager;
        }

        @Override
        protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

            Map<String, String> parameters = new LinkedHashMap<String, String>(tokenRequest.getRequestParameters());
            String username = parameters.get("username");
            String password = parameters.get("password");
            // Protect from downstream leaks of password
            parameters.remove("password");

            Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password);
            ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
            try {
                userAuth = authenticationManager.authenticate(userAuth);
            }
            catch (AccountStatusException ase) {
                //covers expired, locked, disabled cases (mentioned in section 5.2, draft 31)
                throw new InvalidGrantException(ase.getMessage());
            }
            catch (BadCredentialsException e) {
                // If the username/password are wrong the spec says we should send 400/invalid grant
                throw new InvalidGrantException(e.getMessage());
            }
            if (userAuth == null || !userAuth.isAuthenticated()) {
                throw new InvalidGrantException("Could not authenticate user: " + username);
            }

            OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
            return new OAuth2Authentication(storedOAuth2Request, userAuth);
        }
    }

    /**
     * refresh模式Token授权器。
     */
    public static class EhrRefreshTokenGranter extends AbstractTokenGranter {
        static final String GRANT_TYPE = "refresh_token";

        public EhrRefreshTokenGranter(AuthorizationServerTokenServices tokenServices,
                                      ClientDetailsService clientDetailsService,
                                      OAuth2RequestFactory requestFactory) {
            super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        }

        @Override
        protected OAuth2AccessToken getAccessToken(ClientDetails client, TokenRequest tokenRequest) {
            String refreshToken = tokenRequest.getRequestParameters().get(GRANT_TYPE);

            return getTokenServices().refreshAccessToken(refreshToken, tokenRequest);
        }
    }

    /**
     * Implicit模式Token授权器。
     */
    public static class EhrImplicitTokenGranter extends AbstractTokenGranter {
        private static final String GRANT_TYPE = "implicit";

        public EhrImplicitTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
            this(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
        }

        protected EhrImplicitTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
                                       OAuth2RequestFactory requestFactory, String grantType) {
            super(tokenServices, clientDetailsService, requestFactory, grantType);
        }

        @Override
        protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest clientToken) {

            Authentication userAuth = SecurityContextHolder.getContext().getAuthentication();
            if (userAuth == null || !userAuth.isAuthenticated()) {
                throw new InsufficientAuthenticationException("There is no currently logged in user");
            }
            Assert.state(clientToken instanceof ImplicitTokenRequest, "An ImplicitTokenRequest is required here. Caller needs to wrap the TokenRequest.");

            OAuth2Request requestForStorage = ((ImplicitTokenRequest)clientToken).getOAuth2Request();

            return new OAuth2Authentication(requestForStorage, userAuth);

        }

        @SuppressWarnings("deprecation")
        public void setImplicitGrantService(ImplicitGrantService service) {
        }
    }
}

