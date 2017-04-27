package com.yihu.ehr.user.service;

import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.user.dao.XUserRepository;
import com.yihu.ehr.user.entity.User;
import com.yihu.ehr.util.hash.HashUtil;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
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
    @Value("${default.password}")
    private String default_password;

    @PostConstruct
    void init() {
        if (default_password.startsWith("$")) {
            default_password="123456";
        }
    }

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
    public User getUserByUserName(String loginCode) {
        return userRepository.findByLoginCode(loginCode);
    }

    public User getUserByIdCardNo(String idCardNo) {
        List<User> users = userRepository.findByIdCardNo(idCardNo);
        if(users.size()>0){
            return users.get(0);
        }else {
            return null;
        }
    }

    public User getUserByEmail(String email) {
        Map<String,String> map =new HashMap<>();
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery("from User where email = :email");
        List<User> userList = query.setString("email", email).list();
        if(userList.size()== 0) {
            return null;
        }else {
            return userList.get(0);
        }
    }

    public String hashPassword(String pwd) {
        return HashUtil.hash(pwd);
    }

    public boolean isPasswordRight(User user, String pwd) {
        return hashPassword(pwd).equals(user.getPassword());
    }

    /**
     * 根据登陆用户名及密码验证用户.
     *
     * @param loginCode
     * @param psw
     */
    public User loginVerification(String loginCode,String psw) {

        User user = getUserByUserName(loginCode);
        if (user == null) {
            return null;
        }
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


    public User saveUser(User user) {
        userRepository.save(user);
        return user;
    }

    public void deleteUser(String userId) {
        userRepository.delete(userId);
    }

    public void activityUser(String userId, boolean activity) {
        User user = userRepository.findOne(userId);
        user.setActivated(activity);
        userRepository.save(user);
    }


    public void changePassWord(String userId, String password) {
        userRepository.changePassWord(userId,password);
    }
}