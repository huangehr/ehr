package com.yihu.ehr.basic.getui.service;

import com.yihu.ehr.basic.apps.dao.UserAppDao;
import com.yihu.ehr.basic.apps.model.UserApp;
import com.yihu.ehr.basic.getui.dao.GeTuiClientRepository;
import com.yihu.ehr.basic.getui.model.GeTuiUserClient;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author litaohong on 2018/4/20
 * @project ehr
 */
@Service
@Transactional
public class GeTuiClientService extends BaseJpaService<UserApp, UserAppDao> {

    @Autowired
    private GeTuiClientRepository geTuiClientRepository;

    /**
     * 根据用户id获取对应的clientId
     * @param userId
     * @return
     */
    public String getClientIdByUserId(String userId){
        if(StringUtils.isEmpty(userId)){
            return "";
        }
        GeTuiUserClient geTuiUserClient = this.geTuiClientRepository.getUserGeTuiClientByUserId(userId);

        return null == geTuiUserClient ? "" : geTuiUserClient.getClientId();
    }

    /**
     * 根据用户id获取对应的clientId列表
     * @param userIds
     * @return
     */
    public List<String> getListClientIdByUserId(String userIds){
        if(StringUtils.isEmpty(userIds)){
            return null;
        }
        if(userIds.indexOf(",") == 0){
            return null;
        }

        String[] arr = userIds.split(",");
        if(arr.length == 0){
            return null;
        }
        List<String> list = new ArrayList<>();
        for(String str: arr){
            list.add(str);
        }
        List<GeTuiUserClient> geTuiUserClientList = this.geTuiClientRepository.getListByuserId(list);
        List<String> result = new ArrayList<>();
        geTuiUserClientList.forEach(
                one ->{
                    result.add(one.getClientId());
                }
        );
        return result;
    }

    /**
     * 根据用户id更新对应的clientId
     * @param userId
     * @return
     */
    public String updateClientIdByUserId(String userId, String clientId) {
        if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(clientId) ){
            return "params userId|clientId is empty";
        }
        GeTuiUserClient geTuiUserClient = this.geTuiClientRepository.getUserGeTuiClientByUserId(userId);
        if (null == geTuiUserClient) {
            geTuiUserClient = new GeTuiUserClient();
            geTuiUserClient.setUserId(userId);
            geTuiUserClient.setClientId(clientId);
            this.geTuiClientRepository.save(geTuiUserClient);
            return "success";
        }
        this.geTuiClientRepository.updateClientIdByUserId(userId, clientId);
        return "success";
    }
}
