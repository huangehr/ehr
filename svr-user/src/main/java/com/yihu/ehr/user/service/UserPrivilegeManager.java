package com.yihu.ehr.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户权限接口
 */

@Service
@Transactional
public class UserPrivilegeManager  {

    @Autowired
    private XUserPrivilegeRepository userPrivilegeRepository;

    /**
     * 根据userId获取用户权限接口
     *
     * @param userId
     */
    public UserPrivilege getUserPrivilegeByUserId(String userId) {
        UserPrivilege userPrivilege = userPrivilegeRepository.findOne(userId);
        return userPrivilege;
    }

}