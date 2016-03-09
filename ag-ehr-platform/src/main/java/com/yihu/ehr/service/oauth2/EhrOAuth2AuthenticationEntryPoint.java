package com.yihu.ehr.service.oauth2;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.AbstractOAuth2SecurityExceptionHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 参见 {@link OAuth2AuthenticationEntryPoint}。此入口点为Security过滤器认证出错时被调用，并对返回值进行格式化。
 *
 * 本入口点仅返回401错误。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.03.07 17:46
 */
public class EhrOAuth2AuthenticationEntryPoint extends AbstractOAuth2SecurityExceptionHandler implements
        AuthenticationEntryPoint {

    private String typeName = OAuth2AccessToken.BEARER_TYPE;

    private String realmName = "oauth";

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        doHandle(request, response, authException);
    }

    @Override
    protected ResponseEntity<OAuth2Exception> enhanceResponse(ResponseEntity<OAuth2Exception> response, Exception exception) {
        HttpHeaders headers = response.getHeaders();
        String existing = null;
        if (headers.containsKey("WWW-Authenticate")) {
            existing = extractTypePrefix(headers.getFirst("WWW-Authenticate"));
        }
        StringBuilder builder = new StringBuilder();
        builder.append(typeName+" ");
        builder.append("realm=\"" + realmName + "\"");
        if (existing!=null) {
            builder.append(", "+existing);
        }
        HttpHeaders update = new HttpHeaders();
        update.putAll(response.getHeaders());
        update.set("WWW-Authenticate", builder.toString());

        return new ResponseEntity<OAuth2Exception>(response.getBody(), update, response.getStatusCode());
    }

    private String extractTypePrefix(String header) {
        String existing = header;
        String[] tokens = existing.split(" +");
        if (tokens.length > 1 && !tokens[0].endsWith(",")) {
            existing = StringUtils.arrayToDelimitedString(tokens, " ").substring(existing.indexOf(" ") + 1);
        }
        return existing;
    }

}
