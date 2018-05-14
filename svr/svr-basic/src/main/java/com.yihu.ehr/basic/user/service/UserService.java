package com.yihu.ehr.basic.user.service;

import com.yihu.ehr.basic.patient.dao.XDemographicInfoRepository;
import com.yihu.ehr.basic.user.dao.XDoctorRepository;
import com.yihu.ehr.basic.user.dao.XUserRepository;
import com.yihu.ehr.basic.user.entity.User;
import com.yihu.ehr.entity.patient.DemographicInfo;
import com.yihu.ehr.query.BaseJpaService;
import com.yihu.ehr.basic.user.entity.Doctors;
import com.yihu.ehr.util.datetime.DateUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 用户管理接口实现类.
 *
 * @author Sand
 * @version 1.0
 * @updated 02-6月-2015 20:10:56
 */

@Service
@Transactional
public class UserService extends BaseJpaService<User, XUserRepository> {


    @Value("${default.password}")
    private String default_password;
    @Autowired
    private XUserRepository userRepository;
    @Autowired
    private RoleUserService roleUserService;
    @Autowired
    private XDemographicInfoRepository xDemographicInfoRepository;
    @Autowired
    private XDoctorRepository xDoctorRepository;


    @PostConstruct
    void init() {
        if (default_password.startsWith("$")) {
            default_password = "12345678";
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

    /**
     * 根据登陆用户名&手机号&身份证号获取用户接口.
     *
     * @param loginCode
     */
    public List<User> getUserForLogin(String loginCode) {
        return userRepository.findUserForLogin(loginCode);
    }

    public User getUserByTel(String telphone) {
        List<User> users = userRepository.findByTelephone(telphone);
        if (users.size() > 0) {
            return users.get(0);
        }
        return null;
    }

    public User getUserByIdCardNo(String idCardNo) {
        List<User> users = userRepository.findByIdCardNo(idCardNo);
        if (users.size() > 0) {
            return users.get(0);
        } else {
            return null;
        }
    }

    public User getUserByEmail(String email) {
        Map<String, String> map = new HashMap<>();
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery("from User where email = :email");
        List<User> userList = query.setString("email", email).list();
        if (userList.size() == 0) {
            return null;
        } else {
            return userList.get(0);
        }
    }

    /**
     * 根据电话号码查询账号信息
     * @param telephone
     * @return
     */
    public User getUserByTelephone(String telephone) {
        Map<String, String> map = new HashMap<>();
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery("from User where telephone = :telephone");
        List<User> userList = query.setString("telephone", telephone).list();
        if (userList.size() == 0) {
            return null;
        } else {
            return userList.get(0);
        }
    }

    public String hashPassword(String pwd) {
        return DigestUtils.md5Hex(pwd);
    }

    public boolean isPasswordRight(User user, String pwd) {
        return DigestUtils.md5Hex(pwd).equals(user.getPassword());
    }

    /**
     * 根据登陆用户名及密码验证用户.
     *
     * @param loginCode
     * @param psw
     */
    public User loginVerification(String loginCode, String psw) {

        User user = getUserByUserName(loginCode);
        if (user == null) {
            user = getUserByTel(loginCode);
            if (user == null) {
                user = getUserByIdCardNo(loginCode);
                if (user == null)
                    return null;
            }
        }
        boolean result = isPasswordRight(user, psw);
        if (result) {
            return user;
        } else {
            return null;
        }
    }

    public User resetPassword(User user) {
        user.setPassword(hashPassword(default_password));
        return user;
    }

    public void resetPass(String userId) {
        User user = userRepository.findOne(userId);
        resetPassword(user);
        userRepository.save(user);
    }


    public User saveUser(User user) {
        user = userRepository.save(user);
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
        userRepository.changePassWord(userId, password);
    }

    /**
     * 查询电话号码是否已存在， 返回已存在电话号码
     */
    public List idExist( String[] phones)
    {
        String sql = "SELECT telephone FROM users WHERE telephone in(:phones)";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("phones", phones);
        return sqlQuery.list();
    }

    /**
     * 批量创建医生
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean addUserBatch(List<Doctors> doctorLs)
    {
        String header = "INSERT INTO users(id,login_code, real_name,id_card_no,gender, tech_title, email, telephone, password, create_date, activated, user_type, DType, doctor_id, province_id, city_id, area_id) VALUES \n";
        StringBuilder sql = new StringBuilder(header) ;
        Doctors map;
        SQLQuery query;
        int total = 0;
        for(int i=1; i<=doctorLs.size(); i++){
            map = doctorLs.get(i-1);
            String uuid=UUID.randomUUID().toString().replace("-","");
            sql.append("('"+ uuid+"'");
            sql.append(",'"+ null2Space(map.getPhone()) +"'");
            sql.append(",'"+ map .getName() +"'");
            sql.append(",'"+ map .getIdCardNo() +"'");
            sql.append(",'"+ map .getSex() +"'");
            sql.append(",'"+ map .getSkill() +"'");
            sql.append(",'"+ map .getEmail() +"'");
            sql.append(",'"+ null2Space(map .getPhone()) +"'");
            sql.append(",'"+ hashPassword(default_password) +"'");
            sql.append(",'"+ DateUtil.getNowDateTime() +"'");
            sql.append(","+ 1 +"");
            sql.append(",'"+ "Doctor" +"'");
            sql.append(",'"+ "Doctor" +"'");
            sql.append(",'"+ map .getId() +"'");
            sql.append(","+ 0 +"");
            sql.append(","+ 0 +"");
            sql.append(","+ 0 +")");

            if(i%100==0 || i == doctorLs.size()){
                query = currentSession().createSQLQuery(sql.toString());
                total += query.executeUpdate();
                sql = new StringBuilder(header) ;
            }else
                sql.append(",");
        }
        return true;
    }
    private Object null2Space(Object o){
        return o==null? "" : o;
    }

    /**
     * 查询电话号码是否已存在， 返回已存在邮箱
     */
    public List emailsExistence(String[] emails)
    {
        String sql = "SELECT email FROM users WHERE email in(:emails)";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("emails", emails);
        return sqlQuery.list();
    }

    /**
     * 查询电话号码是否已存在， 返回已存在电话号码
     */
    public List idCardNosExist(String[] idCardNos)
    {
        String sql = "SELECT id_card_no FROM users WHERE id_card_no in(:idCardNos)";
        SQLQuery sqlQuery = currentSession().createSQLQuery(sql);
        sqlQuery.setParameterList("idCardNos", idCardNos);
        return sqlQuery.list();
    }

    /**
     * 根据医生ID查询账号信息
     * @param doctorId
     * @return
     */
    public User getUserByDoctorId(String doctorId) {
        Session session = entityManager.unwrap(Session.class);
        Query query = session.createQuery("from User where doctorId = :doctorId");
        List<User> userList = query.setString("doctorId", doctorId).list();
        if (userList.size() == 0) {
            return null;
        } else {
            return userList.get(0);
        }
    }

    public List<User> searchUsers(String[] orgCode, String realName, String userType, int page, int size) {
        Session s = currentSession();
        String hql = "SELECT user.* FROM Users user WHERE user.id IN(SELECT DISTINCT(u.id) from Users u INNER JOIN " +
                "role_User ru ON u.id = ru.user_Id INNER JOIN Roles r ON ru.role_Id =  r.id " +
                "WHERE r.org_code IN(:orgCode))";

        if (!StringUtils.isEmpty(realName)) {
            hql += " AND user.real_Name LIKE :realName";
        }
        if (!StringUtils.isEmpty(userType)) {
            hql += " AND user.user_Type = :userType";
        }
        hql += " ORDER BY user.create_Date desc";

        Query query = s.createSQLQuery(hql).addEntity(User.class);

        query.setParameterList("orgCode", orgCode);
        if (!StringUtils.isEmpty(realName)) {
            query.setParameter("realName", "%" + realName + "%");
        }
        if (!StringUtils.isEmpty(userType)) {
            query.setParameter("userType", userType);
        }
        query.setMaxResults(size);
        query.setFirstResult((page - 1) * size);
        return query.list();
    }

    public Long searchUsersCount(String[] orgCode, String realName, String userType) {
        Session s = currentSession();
        String hql = "SELECT COUNT(user.id) FROM Users user WHERE user.id IN(SELECT DISTINCT(u.id) from Users u INNER JOIN " +
                "role_User ru ON u.id = ru.user_Id INNER JOIN Roles r ON ru.role_Id =  r.id " +
                "WHERE r.org_code IN(:orgCode))";
        if (!StringUtils.isEmpty(realName)) {
            hql += " AND user.real_Name LIKE :realName";
        }
        if (!StringUtils.isEmpty(userType)) {
            hql += " AND user.user_Type = :userType";
        }

        Query query = s.createSQLQuery(hql);

        query.setParameterList("orgCode", orgCode);
        if (!StringUtils.isEmpty(realName)) {
            query.setParameter("realName", "%" + realName + "%");
        }
        if (!StringUtils.isEmpty(userType)) {
            query.setParameter("userType", userType);
        }
        BigInteger count = (BigInteger) query.uniqueResult();
        return count.longValue();
    }

    public User save(User user, DemographicInfo demographicInfo) {
        User user1 = userRepository.save(user);
        xDemographicInfoRepository.save(demographicInfo);
        roleUserService.batchCreateRoleUsersRelation(user.getId(), user.getRole());
        return user1;
    }

    public User update(User user, Doctors doctors, DemographicInfo demographicInfo) {
        User user1 = userRepository.save(user);
        if (doctors != null) {
            xDoctorRepository.save(doctors);
        }
        if (demographicInfo != null) {
            xDemographicInfoRepository.save(demographicInfo);
        }
        if (!StringUtils.isEmpty(user.getRole())){
            roleUserService.batchUpdateRoleUsersRelation(user1.getId(), user.getRole());
        }
        return user1;
    }
}