package com.yihu.ehr.user.user.model;

import com.yihu.ehr.model.BaseDict;
import com.yihu.ehr.util.ApiErrorEcho;
import com.yihu.ehr.util.encode.HashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

//    @Autowired
//    private XUserRepository userRepository;


    @Autowired
    private ConventionalDictClient conventionalDictClient;

//    @PersistenceContext
//    protected EntityManager entityManager;

    //@Value("default.password")
    private String default_password = "123456";

    public UserManager() {
    }


    @Transactional(Transactional.TxType.SUPPORTS)
    private int countRecordByField(String fieldName, String value){
//        String hql = "select count(*) from User where " + fieldName + " = :value";
//
//        Session session = entityManager.unwrap(org.hibernate.Session.class);
//        Query query = session.createQuery(hql);
//        query.setString("value", value);
//
//        Object ret = query.uniqueResult();
//        if(ret == null) return 0;
//
//        return Integer.parseInt(ret.toString());
        return 1;
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
    public User registerUser(BaseDict userType, String loginCode, String nickName, String pwd, String email) {

        User user = new User();
        user.setUserType(userType.getCode());
        user.setCreateDate(new Date());
        user.setLoginCode(loginCode);
        user.setRealName(nickName);
        user.setPassword(HashUtil.hashStr(pwd));
        user.setEmail(email);
        user.setActivated(true);
        //userRepository.save(user);
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
        User user = new User();
//        User user = userRepository.findOne(userId);
        return user;
    }



    /**
     * 根据User获取用户页面信息.
     *
     * @param user
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public UserModel getUser(User user) {

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
            //// TODO: 2016/1/12 调用机构服务接口，获取机构全名称
            //userModel.setOrgName(user.getOrganization().getFullName());
        }
        if (user.getUserType() != null) {
            userModel.setUserType(user.getUserType());
        }
        if (user instanceof MedicalUser) {
            userModel.setMajor((user).getMajor());
        }

//        XSecurityManager securityManager = ServiceFactory.getService(Services.SecurityManager);
//        UserSecurity userSecurity = securityManager.getUserPublicKeyByUserId(user.getId());
//        if (userSecurity != null) {
//            userModel.setPublicKey(userSecurity.getPublicKey());
//            String validTime = DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT)
//                    + "~" + DateUtil.toString(userSecurity.getExpiryDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT);
//            userModel.setValidTime(validTime);
//            userModel.setStartTime( DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT));
//        }

        return userModel;
    }


    /**
     * 根据登陆用户名获取用户接口.
     *
     * @param loginCode
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public User getUserByLoginCode(String loginCode) {

//        Map<String,String> map =new HashMap<>();
//        Session session = entityManager.unwrap(org.hibernate.Session.class);
//        Query query = session.createQuery("from User where loginCode = :loginCode");
//
//        List<User> userList = query.setString("loginCode", loginCode).list();
//
//        if(userList.size()== 0) {
//            return null;
//        }else {
//            return userList.get(0);
//        }
        return null;
    }

    /**
     * 根据登陆用户名获取用户接口.
     *
     * @param loginCode
     * @param email
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public User getUserByCodeAndEmail(String loginCode, String email) {

//        Map<String,String> map =new HashMap<>();
//        Session session = entityManager.unwrap(org.hibernate.Session.class);
//        Query query = session.createQuery("from User where loginCode = :loginCode and email = :email");
//        query.setString("loginCode", loginCode);
//        query.setString("email", email);
//        List<User> userList = query.list();
//
//        if(userList.size()== 0) {
//            return null;
//        } else {
//            return userList.get(0);
//        }
        return null;
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

    /**
     * 根据条件搜索用户.
     *
     * @param args
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<User> searchUser(Map<String, Object> args) {

        //// TODO: 2016/1/12 这里要查询机构服务接口根据条件查询机构服务列表
//        Session session = entityManager.unwrap(org.hibernate.Session.class);
//        String realName = (String) args.get("realName");
//        //String organization = (String) args.get("organization");
//        String fullName = (String) args.get("organization");
//        String shortName = (String) args.get("organization");
//        String type = (String) args.get("type");
//        Integer page = (Integer) args.get("page");
//        Integer pageSize = (Integer) args.get("pageSize");
//        //String hql = "from AbstractUser where (realName like :realName or organization like :organization)";
//        String hql = "from User where (realName like :realName or organization in " +
//                "(from Organization where fullName like :fullName or shortName like :shortName ))";
//        if (!StringUtils.isEmpty(type)) {
//            hql += " and userType = :userType";
//        }
//        Query query = session.createQuery(hql);
//        query.setString("realName", "%"+realName+"%");
//        //query.setString("organization", "%"+organization+"%");
//        query.setString("fullName","%"+fullName+"%");
//        query.setString("shortName","%"+shortName+"%");
//        if (!StringUtils.isEmpty(type)) {
//            //query.setParameter("userType", UserType.valueOf(type));
//            query.setParameter("userType", type);
//        }
//
//        query.setMaxResults(pageSize);
//        query.setFirstResult((page - 1) * pageSize);
//
//        return query.list();
        return null;
    }

    /**
     * 根据条件搜索用户.
     *
     * @param args
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public List<UserDetailModel> searchUserDetailModel(Map<String, Object> args) {

        List<User> userList = searchUser (args);
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
            //detailModel.setActivated(absDictEManage.getYesNo(user.isActivated()).getValue());
//            if (user.getOrganization() != null) {
//                detailModel.setOrganization(user.getOrganization().getFullName());
//            }
//            if (user.getLastLoginTime() != null) {
//                detailModel.setLastLoginTime(DateUtil.toString(user.getLastLoginTime(), DateUtil.DEFAULT_YMDHMSDATE_FORMAT));
//            }
            if (user.getUserType() != null) {
                detailModel.setUserType(user.getUserType());
                detailModel.setUserTypeValue(user.getUserType());
            }
            detailModelList.add(detailModel);
        }

        return detailModelList;
    }

    /**
     * 根据条件搜索用户.
     *
     * @param args
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public Integer searchUserInt(Map<String, Object> args) {

//        Session session = entityManager.unwrap(org.hibernate.Session.class);
//        String realName = (String) args.get("realName");
//        String organization = (String) args.get("organization");
//        String type = (String) args.get("type");
//        String hql = "select count(*) from User where (realName like :realName or organization like :organization) ";
//        if (!StringUtils.isEmpty(type)) {
//            hql += " and userType = :userType";
//        }
//        Query query = session.createQuery(hql);
//        query.setString("realName", "%"+realName+"%");
//        query.setString("organization", "%"+organization+"%");
//        if (!StringUtils.isEmpty(type)) {
//            query.setParameter("userType", type);
//        }
//        return Integer.parseInt(query.list().get(0).toString());
        return null;
    }



    /**
     * 更新用户信息.
     *
     * @param userModel
     */
    public void updateUser(UserModel userModel) {

        User user;
        Map<String, Object> message = new HashMap<>();
        if (StringUtils.isEmpty(userModel.getId())) {
            String password = default_password;
            user = registerUser(conventionalDictClient.getUserType(userModel.getUserType()), userModel.getLoginCode(), userModel.getRealName(), default_password, userModel.getEmail());
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
        //userRepository.save(user);


    }

    public User resetPassword(User user){
        user.setPassword(default_password);
        return user;
    }

    public void resetPass(String userId) {
//        User user = userRepository.findOne(userId);
//        resetPassword(user);
//        userRepository.save(user);
    }


    public void updateUser(User user) {
//        userRepository.save(user);
    }

    public void deleteUser(String userId) {

        //userRepository.delete(userId);
    }

    public void activityUser(String userId, boolean activity) {
//        User user = userRepository.findOne(userId);
//        user.setActivated(activity);
//        userRepository.save(user);
    }

    public void acquire() {
    }

    public <T> T queryInterface(Class<T> type) {
        if (type.isInterface() && type.isInstance(this)) {
            return (T) this;
        }
        return null;
    }

    public void release() {
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
//            User user = userRepository.findOne(userId);
//            user.setLastLoginTime(lastLoginTime);
//            userRepository.save(user);
            return true;
        }
    }

    public List<User> searchUser(String loginCode, String searchNm){
//        Session session = entityManager.unwrap(org.hibernate.Session.class);
//        String sql = "select * from users where "+loginCode+" ="+ "'"+searchNm+"'";
//        SQLQuery sqlQuery = session.createSQLQuery(sql);
//        List userList = sqlQuery.list();
//        return userList;
        return null;
    }
}