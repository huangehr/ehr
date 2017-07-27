package com.yihu.ehr.apps.service;

import com.yihu.ehr.apps.model.UserApp;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.user.entity.RoleAppRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yeshijie
 * @version 1.0
 * @created 2017年2月16日18:04:13
 */
@Service
@Transactional
public class UserAppService extends BaseJpaService<UserApp, XUserAppRepository> {
    @Autowired
    private XUserAppRepository userAppRepository;

    public UserApp findByAppIdAndUserId(String appId, String userId){
        return  userAppRepository.findByAppIdAndUserId(appId,userId);
    }

}