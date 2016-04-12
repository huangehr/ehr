package com.yihu.ehr.service.oauth2;

import com.yihu.ehr.feign.AppClient;
import com.yihu.ehr.model.app.MApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private AppClient appClient;

//    public EhrClientDetailsService(){
//
//        // 直接在内存中添加简易ESB应用，重构后需要将此应用保存在数据库中
//        BaseClientDetails baseClientDetails = new BaseClientDetails(
//                "kHAbVppx44",
//                "ehr",
//                "read,user,user.demographic_id,user.health_profiles,organization",
//                EhrTokenGranter.EhrAuthorizationCodeGranter.GRANT_TYPE,
//                "ROLE_CLIENT",
//                "http://www.yihu.com?key=value");
//
//        // 简易ESB
//        baseClientDetails.setClientSecret("Bd2h8rdYhep6NKOO");
//        clientDetailsStore.put("kHAbVppx44", baseClientDetails);
//
//        // 康赛应用
//        baseClientDetails.setClientSecret("ks0odkx5hqcsOIPM");
//        clientDetailsStore.put("iPOuvtrbzx", baseClientDetails);
//    }




    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {

        ClientDetails details = clientDetailsStore.get(clientId);

        if(details!=null){
            return details;
        }

        MApp app = appClient.getApp(clientId);

        if (app == null) {
            throw new NoSuchClientException("所请求应用不存在，id: " + clientId);
        }

        BaseClientDetails baseClientDetails = new BaseClientDetails(
                app.getId(),
                "ehr",
                "read,user,user.demographic_id,user.health_profiles,organization",
                EhrTokenGranter.EhrAuthorizationCodeGranter.GRANT_TYPE,
                "ROLE_CLIENT",
                app.getUrl());
        baseClientDetails.setClientSecret(app.getSecret());

        clientDetailsStore.put(app.getId(), baseClientDetails);

        return baseClientDetails;
    }
}
