package com.yihu.ehr.user.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.util.encode.HashUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理接口实现类.
 *
 * @author Sand
 * @version 1.0
 * @updated 02-6月-2015 20:10:56
 */

@Service
@Transactional
public class UserManager extends BaseJpaService<User, XUserRepository> {

    @Autowired
    private XUserRepository userRepository;
    @PersistenceContext
    private EntityManager entityManager;
    @Value("default.password")
    private String default_password = "123456";

    /**
     * 根据ID获取用户接口.
     *
     * @param userId
     */
    public User getUser(String userId) {
        User user = userRepository.findOne(userId);
        return user;
    }

    /**
     * 根据登陆用户名获取用户接口.
     *
     * @param loginCode
     */
    public User getUserByLoginCode(String loginCode) {
        Map<String,String> map =new HashMap<>();
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Query query = session.createQuery("from User where loginCode = :loginCode");
        List<User> userList = query.setString("loginCode", loginCode).list();
        if(userList.size()== 0) {
            return null;
        }else {
            return userList.get(0);
        }
    }

    public String hashPassword(String pwd) {
        return HashUtil.hashStr(pwd);
    }

    public boolean isPasswordRight(User user,String pwd) {
        return hashPassword(pwd).equals(user.getPassword());
    }

    /**
     * 根据登陆用户名及密码验证用户.
     *
     * @param loginCode
     * @param psw
     */
    public User loginVerification(String loginCode,String psw) {

        User user = getUserByLoginCode(loginCode);
        boolean result = isPasswordRight(user,psw);
        if(result) {
            return user;
        } else {
            return null;
        }
    }

    public User resetPassword(User user){
        user.setPassword(hashPassword(default_password));
        return user;
    }

    public void resetPass(String userId) {
        User user = userRepository.findOne(userId);
        resetPassword(user);
        userRepository.save(user);
    }


    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void deleteUser(String userId) {
        userRepository.delete(userId);
    }

    public void activityUser(String userId, boolean activity) {
        User user = userRepository.findOne(userId);
        user.setActivated(activity);
        userRepository.save(user);
    }



}