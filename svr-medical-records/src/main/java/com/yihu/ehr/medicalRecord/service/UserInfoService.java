package com.yihu.ehr.medicalRecord.service;


import com.yihu.ehr.medicalRecord.dao.intf.UserInfoDao;
import com.yihu.ehr.medicalRecord.model.User;
import com.yihu.ehr.query.BaseJpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Created by Guo Yanshan on 2016/7/12.
 */
@Service
@Transactional
public class UserInfoService extends BaseJpaService<User, UserInfoDao> {
    @Autowired
    private UserInfoDao usDao;

    /**
     * 根据LoginCode获取用户信息
     *
     * @param loginCode String loginCode
     * @return User
     */
    public User getInfoByLoginCode(String loginCode)
    {
        return usDao.findByLoginCode(loginCode);
    }

}
