package com.yihu.ehr.basic.apps.service;

import com.yihu.ehr.basic.apps.dao.UserAppDao;
import com.yihu.ehr.basic.apps.model.UserApp;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yeshijie
 * @version 1.0
 * @created 2017年2月16日18:04:13
 */
@Service
@Transactional
public class UserAppService extends BaseJpaService<UserApp, UserAppDao> {
    @Autowired
    private UserAppDao userAppRepository;

    public UserApp findByAppIdAndUserId(String appId, String userId){
        return  userAppRepository.findByAppIdAndUserId(appId,userId);
    }

    /**
     * 根据userIds查询
     * @param userIds
     * @return
     */
    public List<UserApp> findListByUserIds(List<String> userIds){
        return userAppRepository.findByuserId(userIds);
    }

}