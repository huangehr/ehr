package com.yihu.ehr.service.oauth2;

import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import java.util.HashMap;
import java.util.Map;

/**
 * 应用信息服务，表示一单个OAuth2应用。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.03.04 9:51
 */
public class EhrClientDetailsService implements ClientDetailsService {

    private Map<String, ClientDetails> clientDetailsStore = new HashMap<String, ClientDetails>();

    public EhrClientDetailsService(){
        // 构建一个测试应用
        BaseClientDetails baseClientDetails = new BaseClientDetails(
                "client-with-registered-redirect",
                "ehr",
                "user,user.demographic_id,user.health_profiles,organization",
                EhrTokenGranter.EhrAuthorizationCodeGranter.GRANT_TYPE,
                "ROLE_CLIENT",
                "http://www.yihu.com?key=value");

        baseClientDetails.setClientSecret("secret123");
        clientDetailsStore.put("client-with-registered-redirect", baseClientDetails);
    }

    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        ClientDetails details = clientDetailsStore.get(clientId);
        if (details == null) {
            throw new NoSuchClientException("所请求应用不存在，id: " + clientId);
        }

        return details;
    }
}
