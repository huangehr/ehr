package com.yihu.ehr.user.user.model;

import com.yihu.ha.constrant.EnvironmentOptions;
import com.yihu.ha.constrant.ErrorCode;
import com.yihu.ha.constrant.Services;
import com.yihu.ha.data.sql.SQLGeneralDAO;
import com.yihu.ha.dict.model.common.UserType;
import com.yihu.ha.dict.model.common.XConventionalDictEntry;
import com.yihu.ha.factory.ServiceFactory;
import com.yihu.ha.organization.model.XOrgManager;
import com.yihu.ha.security.model.XSecurityManager;
import com.yihu.ha.security.model.XUserSecurity;
import com.yihu.ha.util.XEnvironmentOption;
import com.yihu.ha.util.log.LogService;
import com.yihu.ha.util.operator.DateUtil;
import com.yihu.ha.util.operator.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.*;

/**
 * 用户管理接口实现类.
 *
 * @author Sand
 * @version 1.0
 * @updated 02-6月-2015 20:10:56
 */
@Transactional
@Service(Services.UserManager)
public class UserManager extends SQLGeneralDAO implements XUserManager {

    @Resource(name = Services.ConventionalDictEntry)
    XConventionalDictEntry absDictEManage;

    public UserManager() {
    }

    /**
     * 根据用户类型获获取相应的Classic对象
     * @param userType
     * @return
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    Class<? extends XUser> getUserClass(String userType) {
        switch (userType) {
            case "Nurse":
                return Nurse.class;

            case "Doctor":
                return Doctor.class;

            case "GovEmployee":
                return GovEmployee.class;

            case "PlatformMaintance":
                return HAMaintainer.class;

            case "Heater":
                return Heater.class;

            case "ThirdPartPlatUser":
                return ThirdPartPlatUser.class;

            case "Unknown":
                return Unknown.class;

            default:
                return null;
        }
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    private int countRecordByField(String fieldName, String value){
        String hql = "select count(*) from AbstractUser where " + fieldName + " = :value";

        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query query = session.createQuery(hql);
        query.setString("value", value);

        Object ret = query.uniqueResult();
        if(ret == null) return 0;

        return Integer.parseInt(ret.toString());
    }

    /**
     * 根据ID获取用户类型
     * @param userId
     * @return
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    UserType getUserType(long userId) {
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Query query = session.createQuery("from AbstractUser where id = :id");
        query.setLong("id", userId);

        Object ret = query.uniqueResult();
        if(ret == null) throw new UserException("未注册用户");

        UserType usrType = ((XUser)ret).getUserType();
        //UserType usrType = UserType.values()[(Integer)ret];

        return usrType;
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
    public XUser registerUser(UserType userType, String loginCode, String nickName, String pwd, String email) {
        //Class<? extends XUser> cls = getUserClass(userType);
        Class<? extends XUser> cls = getUserClass(userType.getCode());
        if (cls == null) return null;

        AbstractUser usr = null;
        try {
            usr = (AbstractUser) cls.newInstance();
        } catch (InstantiationException ex) {
            LogService.getLogger(UserManager.class).error("初始化用户失败：" + ex.getMessage());
        } catch (IllegalAccessException ex) {
            LogService.getLogger(UserManager.class).error("非法访问：" + ex.getMessage());
        }

        usr.createDate = new Date();
        usr.loginCode = loginCode;
        usr.realName = nickName;
        usr.password = usr.hashPassword(pwd);
        usr.email = email;
        usr.activated = true;
        usr.userType = userType;

        saveEntity(usr);

        return (XUser) usr;
    }

    /**
     * 获取超级用户.
     */
    public XUser getSuperUser(){
        return getUser("00000000000000000000000000000000");
    }

    /**
     * 根据ID获取用户接口.
     *
     * @param userId
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public XUser getUser(String userId) {
        Session session = currentSession();

        XUser user = (AbstractUser)session.get(AbstractUser.class, userId);
        return user;
    }

    /**
     * 根据User获取用户页面信息.
     *
     * @param user
     */
    @Transactional(Transactional.TxType.SUPPORTS)
    public UserModel getUser(XUser user) {

        UserModel userModel = new UserModel();
        userModel.setId(user.getId());
        userModel.setLoginCode(user.getLoginCode());
        userModel.setEmail(user.getEmail());
        userModel.setRealName(user.getRealName());
        userModel.setTel(user.getTelephone());
        userModel.setIdCard(user.getIdCardNo());
        if (user.getGender() != null) {
            userModel.setSex(user.getGender().getCode());
        }
        if (user.getMartialStatus() != null) {
            userModel.setMarriage(user.getMartialStatus().getCode());
        }
        if (user.getOrganization() != null) {
            userModel.setOrgCode(user.getOrganization().getOrgCode());
            userModel.setOrgName(user.getOrganization().getFullName());
        }
        if (user.getUserType() != null) {
            userModel.setUserType(user.getUserType().getCode());
        }
        if (user instanceof XMedicalUser) {
            userModel.setMajor(((XMedicalUser)user).getMajor());
        }

        XSecurityManager securityManager = ServiceFactory.getService(Services.SecurityManager);
        XUserSecurity userSecurity = securityManager.getUserPublicKeyByUserId(user.getId());
        if (userSecurity != null) {
            userModel.setPublicKey(userSecurity.getPublicKey());
            String validTime = DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT)
                    + "~" + DateUtil.toString(userSecurity.getExpiryDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT);
            userModel.setValidTime(validTime);
            userModel.setStartTime( DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT));
        }

        return userModel;
    }


    /**
     * 根据登陆用户名获取用户接口.
     *
     * @param loginCode
     */
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public XUser getUserByLoginCode(String loginCode) {

        Map<String,String> map =new HashMap<>();
        Session session = currentSession();
        Query query = session.createQuery("from AbstractUser where loginCode = :loginCode");

        List<XUser> userList = query.setString("loginCode", loginCode).list();

        if(userList.size()== 0)
        {
            return null;
        }
        else
        {
            return userList.get(0);
        }
    }

    /**
     * 根据登陆用户名获取用户接口.
     *
     * @param loginCode
     * @param email
     */
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public XUser getUserByCodeAndEmail(String loginCode, String email) {

        Map<String,String> map =new HashMap<>();
        Session session = currentSession();
        Query query = session.createQuery("from AbstractUser where loginCode = :loginCode and email = :email");
        query.setString("loginCode", loginCode);
        query.setString("email", email);
        List<XUser> userList = query.list();

        if(userList.size()== 0)
        {
            return null;
        }
        else
        {
            return userList.get(0);
        }
    }

    /**
     * 根据登陆用户名及密码验证用户.
     *
     * @param loginCode
     * @param psw
     */
    @Override
    @Transactional(Transactional.TxType.SUPPORTS)
    public XUser loginIndetification(String loginCode,String psw) {

        XUser user = getUserByLoginCode(loginCode);
        boolean result = user.isPasswordRight(psw);

        if(result) {
            return user;
        }
        else {
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
    public List<XUser> searchUser(Map<String, Object> args) {

        Session session = currentSession();
        String realName = (String) args.get("realName");
        //String organization = (String) args.get("organization");
        String fullName = (String) args.get("organization");
        String shortName = (String) args.get("organization");
        String type = (String) args.get("type");
        Integer page = (Integer) args.get("page");
        Integer pageSize = (Integer) args.get("pageSize");
        //String hql = "from AbstractUser where (realName like :realName or organization like :organization)";
        String hql = "from AbstractUser where (realName like :realName or organization in " +
                "(from Organization where fullName like :fullName or shortName like :shortName ))";
        if (!StringUtil.isEmpty(type)) {
            hql += " and userType = :userType";
        }
        Query query = session.createQuery(hql);
        query.setString("realName", "%"+realName+"%");
        //query.setString("organization", "%"+organization+"%");
        query.setString("fullName","%"+fullName+"%");
        query.setString("shortName","%"+shortName+"%");
        if (!StringUtil.isEmpty(type)) {
            //query.setParameter("userType", UserType.valueOf(type));
            query.setParameter("userType", absDictEManage.getUserType(type));
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
    public List<UserDetailModel> searchUserDetailModel(Map<String, Object> args) {

        List<XUser> userList = searchUser (args);
        List<UserDetailModel> detailModelList = new ArrayList<>();
        Integer order = 1;

        for (XUser user : userList) {
            UserDetailModel detailModel = new UserDetailModel();
            detailModel.setOrder(order++);
            detailModel.setId(user.getId());
            detailModel.setEmail(user.getEmail());
            detailModel.setLoginCode(user.getLoginCode());
            detailModel.setRealName(user.getRealName());
            detailModel.setTelephone(user.getTelephone());
            detailModel.setActivated(absDictEManage.getYesNo(user.isActivated()).getValue());
            if (user.getOrganization() != null) {
                detailModel.setOrganization(user.getOrganization().getFullName());
            }
            if (user.getLastLoginTime() != null) {
                detailModel.setLastLoginTime(DateUtil.toString(user.getLastLoginTime(), DateUtil.DEFAULT_YMDHMSDATE_FORMAT));
            }
            if (user.getUserType() != null) {
                detailModel.setUserType(user.getUserType().getCode());
                detailModel.setUserTypeValue(user.getUserType().getValue());
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

        Session session = currentSession();
        String realName = (String) args.get("realName");
        String organization = (String) args.get("organization");
        String type = (String) args.get("type");
        String hql = "select count(*) from AbstractUser where (realName like :realName or organization like :organization) ";
        if (!StringUtil.isEmpty(type)) {
            hql += " and userType = :userType";
        }
        Query query = session.createQuery(hql);
        query.setString("realName", "%"+realName+"%");
        query.setString("organization", "%"+organization+"%");
        if (!StringUtil.isEmpty(type)) {
            query.setParameter("userType", absDictEManage.getUserType(type));
        }
        return Integer.parseInt(query.list().get(0).toString());
    }

    /**
     * 更新用户信息.
     *
     * @param user
     */
    @Override
    public void updateUser(XUser user) {
        updateEntity(user);
    }

    /**
     * 更新用户信息.
     *
     * @param userModel
     */
    @Override
    public Map<ErrorCode, String> updateUser(UserModel userModel) {

        XUser user;
        Map<ErrorCode, String> message = new HashMap<>();
        if (StringUtils.isEmpty(userModel.getId())) {
            XEnvironmentOption environmentConfig = ServiceFactory.getService(Services.EnvironmentOption);
            String password = environmentConfig.getOption(EnvironmentOptions.DefaultPassword);
            user = registerUser(absDictEManage.getUserType(userModel.getUserType()), userModel.getLoginCode(), userModel.getRealName(), password, userModel.getEmail());
        } else {
            user = getUser(userModel.getId());
        }
        if (user == null) {
            message.put(ErrorCode.InvalidUser, "用户不存在");
            return message;
        }
        user.setEmail(userModel.getEmail());
        user.setRealName(userModel.getRealName());
        if (userModel.getUserType() != null) {
            user.setUserType(absDictEManage.getUserType(userModel.getUserType()));
        }
        XOrgManager orgManager = ServiceFactory.getService(Services.OrgManager);
        user.setOrganization(orgManager.getOrg(userModel.getOrgCode()==null?"":userModel.getOrgCode()));
        if (userModel.getSex() != null) {
            user.setGender(absDictEManage.getGender(userModel.getSex()));
        }
        user.setIdCardNo(userModel.getIdCard());
        if (userModel.getMarriage() != null) {
            user.setMartialStatus(absDictEManage.getMartialStatus(userModel.getMarriage()));
        }
        user.setTelephone(userModel.getTel());
        if(user instanceof XMedicalUser){
            ((XMedicalUser)user).setMajor(userModel.getMajor());
        }
        Session session = currentSession();
        session.update(user);
        session.flush();

        return null;
    }

    @Override
    public void resetPass(String userId) {
        XUser user = getUser(userId);
        user.resetPassword();
        updateUser(user);
    }

    @Override
    public void deleteUser(String userId) {

        Session session = currentSession();
        Query query = session.createQuery("delete AbstractUser where id = :id");
        query.setString("id", userId);
        query.executeUpdate();
    }

    public void activityUser(String userId, boolean activity) {

        XUser user =  getUser(userId);
        user.setActivated(activity);
        updateEntity(user);
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
        if (StringUtil.isEmpty(userId)) {
            return false;
        } else {
            Session session = currentSession();
            XUser user = (XUser) session.get(AbstractUser.class, userId);
            user.setLastLoginTime(lastLoginTime);
            updateEntity(user);
            return true;
        }
    }

    @Override
    public boolean searchUser(String loginCode, String searchNm){
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        String sql = "select * from users where "+loginCode+" ="+ "'"+searchNm+"'";
        SQLQuery sqlQuery = session.createSQLQuery(sql);
        List userList = sqlQuery.list();
        if(userList.size()>0){
            return false;
        }
        return true;
    }
}