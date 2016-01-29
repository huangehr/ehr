package com.yihu.ehr.user.service;

import com.yihu.ehr.model.address.MAddress;
import com.yihu.ehr.model.dict.MBaseDict;
import com.yihu.ehr.model.security.MUserSecurity;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.user.feignClient.address.AddressClient;
import com.yihu.ehr.user.feignClient.dict.ConventionalDictClient;
import com.yihu.ehr.user.feignClient.org.OrgClient;
import com.yihu.ehr.user.feignClient.security.SecurityClient;
import com.yihu.ehr.user.model.MedicalUser;
import com.yihu.ehr.util.ApiErrorEcho;
import com.yihu.ehr.util.encode.HashUtil;
import org.apache.commons.lang.time.DateFormatUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;

/**
 * 用户管理接口实现类.
 *
 * @author Sand
 * @version 1.0
 * @updated 02-6月-2015 20:10:56
 */

@Service
@Transactional
public class UserManager  {

    @Autowired
    private XUserRepository userRepository;

    @Autowired
    private ConventionalDictClient conventionalDictClient;

    @Autowired
    private OrgClient organizationClient;

    @Autowired
    private AddressClient addressClient;

    @Autowired
    SecurityClient securityClient;

    @PersistenceContext
    protected EntityManager entityManager;

    @Value("default.password")
    private String default_password = "123456";

    public UserManager() {
    }


    @Transactional(Transactional.TxType.SUPPORTS)
    private int countRecordByField(String fieldName, String value){
        String hql = "select count(*) from User where " + fieldName + " = :value";

        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Query query = session.createQuery(hql);
        query.setString("value", value);

        Object ret = query.uniqueResult();
        if(ret == null) return 0;

        return Integer.parseInt(ret.toString());
    }



    /**
     * 创建一个用户, 并返用户接口.
     *
     * @param userType
     * @param loginCode
     * @param nickName
     * @param pwd
     * @param email
     */
    public User registerUser(MBaseDict userType, String loginCode, String nickName, String pwd, String email) {

        User user = new User();
        user.setUserType(userType.getCode());
        user.setCreateDate(new Date());
        user.setLoginCode(loginCode);
        user.setRealName(nickName);
        user.setPassword(HashUtil.hashStr(pwd));
        user.setEmail(email);
        user.setActivated(true);
        userRepository.save(user);
        return user;
    }

    /**
     * 获取超级用户.
     */
    public User getSuperUser(){
        return getUser("00000000000000000000000000000000");
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

    public MAddress getAddressById(String apiVersion,String locarion) {
        return addressClient.getAddressById(apiVersion,locarion);

    }
    /**
     * 根据User获取用户页面信息.
     *
     * @param user
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public UserModel getUser(String apiVersion,User user) {

        UserModel userModel = new UserModel();
        userModel.setId(user.getId());
        userModel.setLoginCode(user.getLoginCode());
        userModel.setEmail(user.getEmail());
        userModel.setRealName(user.getRealName());
        userModel.setTel(user.getTelephone());
        userModel.setIdCard(user.getIdCardNo());
        if (user.getGender() != null) {
            userModel.setSex(user.getGender());
        }
        if (user.getMartialStatus() != null) {
            userModel.setMarriage(user.getMartialStatus());
        }
        if (user.getOrganization() != null) {
            userModel.setOrgCode(user.getOrganization());
            userModel.setOrgName(organizationClient.getOrg(apiVersion,user.getOrganization()).getFullName());
        }
        if (user.getUserType() != null) {
            userModel.setUserType(user.getUserType());
        }
        if (user instanceof MedicalUser) {
            userModel.setMajor((user).getMajor());
        }

        MUserSecurity userSecurity = securityClient.getUserSecurityByUserId(apiVersion,user.getId());
        if (userSecurity != null) {
            userModel.setPublicKey(userSecurity.getPublicKey());
            String validTime = DateFormatUtils.format(userSecurity.getFromDate(),"yyyy-MM-dd")
                    + "~" + DateFormatUtils.format(userSecurity.getExpiryDate(),"yyyy-MM-dd");
            userModel.setValidTime(validTime);
            userModel.setStartTime(DateFormatUtils.format(userSecurity.getFromDate(),"yyyy-MM-dd"));
        }

        return userModel;
    }


    /**
     * 根据登陆用户名获取用户接口.
     *
     * @param loginCode
     */
    @Transactional(Transactional.TxType.SUPPORTS)
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

    /**
     * 根据登陆用户名获取用户接口.
     *
     * @param loginCode
     * @param email
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public User getUserByCodeAndEmail(String loginCode, String email) {

        Map<String,String> map =new HashMap<>();
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Query query = session.createQuery("from User where loginCode = :loginCode and email = :email");
        query.setString("loginCode", loginCode);
        query.setString("email", email);
        List<User> userList = query.list();

        if(userList.size()== 0) {
            return null;
        } else {
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
    @Transactional(Transactional.TxType.SUPPORTS)
    public User loginIndetification(String loginCode,String psw) {

        User user = getUserByLoginCode(loginCode);
        boolean result = isPasswordRight(user,psw);
        if(result) {
            return user;
        } else {
            return null;
        }
    }




    /**
     * 判断电子邮箱是否已被占用.
     *
     * @param email
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public boolean isEmailExists(String email) {
        return countRecordByField("email", email) > 0;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Integer searchUserInt(String apiVersion,Map<String, Object> args) {

        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String realName = (String) args.get("realName");
        String type = (String) args.get("type");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("pageSize");
        String name = (String) args.get("organization");
        List<String> orgIds = new ArrayList<>();
        try{
            orgIds = organizationClient.getIdsByName(apiVersion,name);
        }catch (Exception e){
            orgIds.add("null");
        }
        orgIds = organizationClient.getIdsByName(apiVersion,name);
        String hql = "";
        if(orgIds.size()>0 && !orgIds.get(0).equals("null")){
            hql += "from User where (realName like :realName or  location in (:orgIds) ";

        }else{
            hql += "from User where realName like :realName ";
        }
        if (!StringUtils.isEmpty(type)) {
            hql += " and userType = :userType";
        }
        if (!StringUtils.isEmpty(type)) {
            hql += " and userType = :userType";
        }
        Query query = session.createQuery(hql);
        query.setString("realName", "%"+realName+"%");
        if (orgIds.size()>0 && !"00000".equals(orgIds.get(0))) {
            query.setParameterList("orgIds", orgIds);
        }
        if (!StringUtils.isEmpty(type)) {
            query.setParameter("userType", type);
        }

        return query.list().size();
    }


    /**
     * 根据条件搜索用户.
     *
     * @param args
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<User> searchUser(String apiVersion,Map<String, Object> args) {

        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String realName = (String) args.get("realName");
        String type = (String) args.get("type");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("pageSize");
        String name = (String) args.get("organization");
        List<String> orgIds = new ArrayList<>();
        try{
            orgIds = organizationClient.getIdsByName(apiVersion,name);
        }catch (Exception e){
            orgIds.add("null");
        }
        orgIds = organizationClient.getIdsByName(apiVersion,name);
        String hql = "";
        if(orgIds.size()>0 && !orgIds.get(0).equals("null")){
            hql += "from User where (realName like :realName or  location in (:orgIds) ";

        }else{
            hql += "from User where realName like :realName ";
        }
        if (!StringUtils.isEmpty(type)) {
            hql += " and userType = :userType";
        }
        if (!StringUtils.isEmpty(type)) {
            hql += " and userType = :userType";
        }
        Query query = session.createQuery(hql);
        query.setString("realName", "%"+realName+"%");
        if (orgIds.size()>0 && !"00000".equals(orgIds.get(0))) {
            query.setParameterList("orgIds", orgIds);
        }
        if (!StringUtils.isEmpty(type)) {
            query.setParameter("userType", type);
        }

        query.setMaxResults(pageSize);
        query.setFirstResult((page - 1) * pageSize);

        return query.list();
    }





    /**
     * 根据条件搜索用户.
     *
     * @param args
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<UserDetailModel> searchUserDetailModel(String apiVersion,Map<String, Object> args) {

        List<User> userList = searchUser (apiVersion,args);
        List<UserDetailModel> detailModelList = new ArrayList<>();
        Integer order = 1;

        for (User user : userList) {
            UserDetailModel detailModel = new UserDetailModel();
            detailModel.setOrder(order++);
            detailModel.setId(user.getId());
            detailModel.setEmail(user.getEmail());
            detailModel.setLoginCode(user.getLoginCode());
            detailModel.setRealName(user.getRealName());
            detailModel.setTelephone(user.getTelephone());
            detailModel.setActivated(user.getActivated());
            if (user.getOrganization() != null) {
                //detailModel.setOrganization(user.getOrganization().getFullName());
                detailModel.setOrganization(organizationClient.getOrg(apiVersion,user.getOrganization()).getFullName());
            }
            if (user.getLastLoginTime() != null) {
                detailModel.setLastLoginTime(DateFormatUtils.format(user.getLastLoginTime(),"yyyy-MM-dd HH:mm:ss"));
            }
            if (user.getUserType() != null) {
                detailModel.setUserType(user.getUserType());
                detailModel.setUserTypeValue(conventionalDictClient.getUserType(apiVersion,user.getUserType()).getValue());
            }
            detailModelList.add(detailModel);
        }

        return detailModelList;
    }






    /**
     * 更新用户信息.
     *
     * @param userModel
     */
    public void updateUser(String apiVersion,UserModel userModel) {

        User user;
        Map<String, Object> message = new HashMap<>();
        if (StringUtils.isEmpty(userModel.getId())) {
            String password = default_password;
            user = registerUser(conventionalDictClient.getUserType(apiVersion,userModel.getUserType()), userModel.getLoginCode(), userModel.getRealName(), default_password, userModel.getEmail());
        } else {
            user = getUser(userModel.getId());
        }
        if (user == null) {
            ApiErrorEcho apiErrorEcho = new ApiErrorEcho();
            apiErrorEcho.putMessage("用户不存在");
            return;
        }
        user.setEmail(userModel.getEmail());
        user.setRealName(userModel.getRealName());
        if (userModel.getUserType() != null) {
            user.setUserType(userModel.getUserType());
        }
        user.setOrganization(userModel.getOrgCode()==null?"":userModel.getOrgCode());
        if (userModel.getSex() != null) {
            user.setGender(userModel.getSex());
        }
        user.setIdCardNo(userModel.getIdCard());
        if (userModel.getMarriage() != null) {
            user.setMartialStatus(userModel.getMarriage());
        }
        user.setTelephone(userModel.getTel());
        if(user instanceof MedicalUser){
            ((MedicalUser)user).setMajor(userModel.getMajor());
        }
        userRepository.save(user);


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


    public void updateUser(User user) {
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

    /**
     * 记录用户的最近登入时间
     * @param userId
     * @param lastLoginTime
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public boolean lastLoginTime(String userId,Date lastLoginTime) {
        if (StringUtils.isEmpty(userId)) {
            return false;
        } else {
            User user = userRepository.findOne(userId);
            user.setLastLoginTime(lastLoginTime);
            userRepository.save(user);
            return true;
        }
    }

    public List<User> searchUser(String loginCode, String searchNm){
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        String sql = "select * from users where "+loginCode+" ="+ "'"+searchNm+"'";
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        List userList = sqlQuery.list();
        return userList;
    }



}