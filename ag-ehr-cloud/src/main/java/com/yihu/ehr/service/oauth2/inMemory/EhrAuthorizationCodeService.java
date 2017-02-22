package com.yihu.ehr.service.oauth2.inMemory;

import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;

import java.util.concurrent.ConcurrentHashMap;

/**
 * OAuth2验证码服务
 *
 * @author Sand
 * @version 1.0
 * @created 2016.03.05 11:03
 */
public class EhrAuthorizationCodeService extends RandomValueAuthorizationCodeServices {
    protected final ConcurrentHashMap<String, OAuth2Authentication> authorizationCodeStore = new ConcurrentHashMap<String, OAuth2Authentication>();

    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        this.authorizationCodeStore.put(code, authentication);
    }

    @Override
    public OAuth2Authentication remove(String code) {
        OAuth2Authentication auth = this.authorizationCodeStore.remove(code);
        return auth;
    }

    @Override
    public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {
        OAuth2Authentication auth = this.remove(code);

        if (auth == null) {
            throw new BadVerificationCodeException("授权码: " + code + "无效或已过期");
        }

        return auth;
    }

    static class BadVerificationCodeException extends ClientAuthenticationException {

        public BadVerificationCodeException(String msg, Throwable t) {
            super(msg, t);
        }

        public BadVerificationCodeException(String msg) {
            super(msg);
        }

        @Override
        public String getOAuth2ErrorCode() {
            return "无效验证码";
        }
    }
}
