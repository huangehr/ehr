package com.yihu.ehr.service.oauth2;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidClientException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.HashMap;
import java.util.Map;

/**
 * Token授权器，根据请求创建Token给客户端。由两个授权器组成：authorization_code与refresh_token模式。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.03.05 10:52
 */
public class EhrTokenGranter implements TokenGranter {
    private final Map<String, TokenGranter> tokenGranters = new HashMap<>();

    public EhrTokenGranter(AuthorizationServerTokenServices tokenServices,
                           AuthorizationCodeServices authorizationCodeServices,
                           ClientDetailsService clientDetailsService,
                           OAuth2RequestFactory requestFactory) {
        tokenGranters.put(EhrAuthorizationCodeGranter.GRANT_TYPE,
                new EhrAuthorizationCodeGranter(
                        tokenServices,
                        authorizationCodeServices,
                        clientDetailsService,
                        requestFactory));

        tokenGranters.put(EhrRefreshTokenGranter.GRANT_TYPE,
                new EhrRefreshTokenGranter(
                        tokenServices,
                        clientDetailsService,
                        requestFactory));
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
        static final String GRANT_TYPE = "authorization_code";

        private final AuthorizationCodeServices authorizationCodeServices;

        public EhrAuthorizationCodeGranter(AuthorizationServerTokenServices tokenServices,
                                           AuthorizationCodeServices authorizationCodeServices,
                                           ClientDetailsService clientDetailsService,
                                           OAuth2RequestFactory requestFactory) {
            super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);

            this.authorizationCodeServices = authorizationCodeServices;
        }

        @Override
        protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

            Map<String, String> parameters = tokenRequest.getRequestParameters();
            String authorizationCode = parameters.get("code");
            String redirectUri = parameters.get(OAuth2Utils.REDIRECT_URI);

            if (authorizationCode == null) {
                throw new InvalidRequestException("缺少授权码");
            }

            OAuth2Authentication storedAuth = authorizationCodeServices.consumeAuthorizationCode(authorizationCode);
            if (storedAuth == null) {
                throw new InvalidGrantException("无效授权码：" + authorizationCode);
            }

            OAuth2Request pendingOAuth2Request = storedAuth.getOAuth2Request();
            // https://jira.springsource.org/browse/SECOAUTH-333
            // This might be null, if the authorization was done without the redirect_uri parameter
            String redirectUriApprovalParameter = pendingOAuth2Request.getRequestParameters().get(
                    OAuth2Utils.REDIRECT_URI);

            if ((redirectUri != null || redirectUriApprovalParameter != null)
                    && !redirectUri.startsWith(pendingOAuth2Request.getRedirectUri())) {
                throw new RedirectMismatchException("重定向URI不匹配");
            }

            String pendingClientId = pendingOAuth2Request.getClientId();
            String clientId = tokenRequest.getClientId();
            if (clientId != null && !clientId.equals(pendingClientId)) {
                // just a sanity check.
                throw new InvalidClientException("缺少应用ID");
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

            Authentication userAuth = storedAuth.getUserAuthentication();

            return new OAuth2Authentication(finalStoredOAuth2Request, userAuth);
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
}

