package com.yihu.ehr.service.oauth2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 从请求中提取Token。兼容Swagger Api Key。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.03.07 19:32
 */
public class EhrTokenExtractor implements TokenExtractor {

    private final static Log logger = LogFactory.getLog(EhrTokenExtractor.class);
    private final static String SWAGGER_API_KEY = "api_key";

    @Override
    public Authentication extract(HttpServletRequest request) {
        String tokenValue = extractToken(request);
        if (tokenValue != null) {
            PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(tokenValue, "");
            return authentication;
        }

        return null;
    }

    protected String extractToken(HttpServletRequest request) {
        String token = extractHeaderToken(request);

        // bearer 类型Token可能在请求参数中
        if (token == null) {
            logger.debug("请求头不包含Token，尝试从请求参数中查找...");

            token = request.getParameter(OAuth2AccessToken.ACCESS_TOKEN);
            if (token == null) {
                token = request.getParameter(SWAGGER_API_KEY);

                if (token == null) {
                    logger.debug("找不到Token，非OAuth2请求类型.");
                }
            } else {
                request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, OAuth2AccessToken.BEARER_TYPE);
            }
        }

        return token;
    }

    /**
     * 从请求头中提取Token。
     *
     * @param request The request.
     * @return Token，若没有则返回null
     */
    protected String extractHeaderToken(HttpServletRequest request) {
        String apiKey = request.getHeader(SWAGGER_API_KEY);
        if (apiKey != null) return apiKey;

        Enumeration<String> headers = request.getHeaders(SWAGGER_API_KEY);
        if(headers == null){
            headers = request.getHeaders("Authorization");
        }

        while (headers.hasMoreElements()) { // typically there is only one (most servers enforce that)
            String value = headers.nextElement();
            if ((value.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase()))) {
                String authHeaderValue = value.substring(OAuth2AccessToken.BEARER_TYPE.length()).trim();

                request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_TYPE, value.substring(0, OAuth2AccessToken.BEARER_TYPE.length()).trim());
                int commaIndex = authHeaderValue.indexOf(',');
                if (commaIndex > 0) {
                    authHeaderValue = authHeaderValue.substring(0, commaIndex);
                }
                return authHeaderValue;
            }
        }

        return null;
    }
}
