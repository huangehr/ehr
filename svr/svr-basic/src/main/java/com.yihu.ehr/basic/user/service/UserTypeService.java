package com.yihu.ehr.basic.user.service;

import com.yihu.ehr.basic.user.dao.XUserTypeRepository;
import com.yihu.ehr.basic.user.dao.XUserTypeRolesRepository;
import com.yihu.ehr.basic.user.entity.UserType;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * 用户类型管理接口实现类.
 *
 * @author zdm
 * @version 1.0
 * @updated 2018-07-31
 */

@Service
@Transactional
public class UserTypeService extends BaseJpaService<UserType, XUserTypeRepository> {

    @Autowired
    private XUserTypeRepository xUserTypeRepository;
    @Autowired
    private XUserTypeRolesRepository xUserTypeRolesRepository;


    /**
     * 根据ID获取用户接口.
     *
     * @param userId
     */
/*    public User getUser(String userId) {
        User user = userRepository.findOne(userId);
        return user;
    }*/


}