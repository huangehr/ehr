package com.yihu.ehr.security.service;

import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.security.feign.UserClient;
import com.yihu.ehr.util.DateUtil;
import com.yihu.ehr.util.encrypt.RSA;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 用户公私钥管理
 *
 * @author CWS
 * @version 1.0
 * @created 02-6月-2015 17:38:05
 */
@Transactional
@Service
public class SecurityManager {


    @Autowired
    private XUserSecurityRepository userSecurityRepository;

    @Autowired
    private  XUserKeyRepository userKeyRepository;

    @Autowired
    private UserClient userClient;


    @PersistenceContext
    protected EntityManager entityManager;


    HashMap<String, Key> hashMap;

    static String persenalKeyType  = "Personal";
    static String orgKeyType = "Org";


    public Map setUp() throws Exception {
        hashMap = RSA.generateKeys();
        return hashMap;
    }

    public UserSecurity createSecurity() throws Exception {

        UserSecurity userSecurity = new UserSecurity();

        Map userRsa = setUp();

        String publicKey = RSA.encodeKey((Key) userRsa.get(RSA.PUBLIC_KEY));
        String privateKey = RSA.encodeKey((Key) userRsa.get(RSA.PRIVATE_KEY));

        Date currentDate = new Date();

        //Date fromDate = DateUtil.add(currentDate, "d", -1);       //生效时间默认为当前日期的前一天
        Date fromDate = currentDate;       //生效时间默认为当前日期
        Date expiryDate = DateUtil.add(currentDate, "y", 100);   //截止日期为生效日期往后推一百年
        Integer valid = 1;                                      //默认设置为生效状态

        userSecurity.setValid(valid);
        userSecurity.setPublicKey(publicKey);
        userSecurity.setPrivateKey(privateKey);
        userSecurity.setFromDate(fromDate);
        userSecurity.setExpiryDate(expiryDate);

        userSecurityRepository.save(userSecurity);

        return  userSecurity;
    }

    public UserSecurity createSecurityByUserId(String apiVersion,String userId) throws Exception {

        //1-1根据用户登陆名获取用户信息。

        MUser userInfo = userClient.getUser(apiVersion,userId);
        if(userInfo==null) {
            return null;
        }
        else {
            UserSecurity userSecurity = createSecurity();
            //1-2-1-2 与用户进行关联，user_key数据增加。
            createUserKey(userSecurity, userInfo, persenalKeyType);

            return userSecurity;
        }
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public UserSecurity createSecurityByOrgCode(String orgCode) throws Exception {

        UserSecurity userSecurity = createSecurity();
        //1-2-1-2 与用户进行关联，user_key数据增加。
        createUserKeyByOrg(userSecurity, orgCode, orgKeyType);

        return userSecurity;
    }

    public UserSecurity getUserSecurity(String securityId) throws Exception {
        return userSecurityRepository.findOne(securityId);
    }

    public UserSecurity getUserSecurityByLoginCode(String apiVersion,String loginCode) throws Exception {

        //1-1根据用户登陆名获取用户信息。
        MUser userInfo = userClient.getUserByLoginCode(apiVersion,loginCode);
        if(userInfo==null) {
            return null;
        } else {
            String userId = userInfo.getId();
            String userKeyId = getUserKeyByUserId(userId);

            if(userKeyId == null || userKeyId.equals("")){
                //1-2-1当UserKey不存在的情况下，需要重新生成公私钥并保存记录，并与用户表进行关联。
                //1-2-1-1 公私钥生成
                UserSecurity userSecurity = createSecurity();
                //1-2-1-2 与用户进行关联，user_key数据增加。
                createUserKey(userSecurity, userInfo, persenalKeyType);
                return userSecurity;
            }else{
                //1-2-2当UserKey存在的情况下，查询用户关联的用户密钥信息。
                UserKey userKey = getUserKey(userKeyId);
                String userSecurityId =  userKey.getKey();
                return userSecurityRepository.findOne(userSecurityId);
            }
        }
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<UserSecurity> getUserSecurityList(int from, int count) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        Criteria criteria = session.createCriteria(UserSecurity.class);
        if (from >= 0 && count > 0) {
            criteria.setFirstResult(from);
            criteria.setMaxResults(count);
        }
        List<UserSecurity> list = criteria.list();

        return list;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public void updateSecurity(UserSecurity security) {
        userSecurityRepository.save(security);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public void deleteSecurity(String id) {

        UserSecurity userSecurity = userSecurityRepository.findOne(id);
        userSecurityRepository.delete(userSecurity);
    }

    public UserKey createUserKey(UserSecurity security, MUser user, String keyType) {

        UserKey userKey = new UserKey();

        userKey.setKeyType(keyType);
        userKey.setUser(user.getId());
        userKey.setKey(security.getId());

        userKeyRepository.save(userKey);
        return userKey;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public UserKey createUserKeyByOrg(UserSecurity security, String orgCode, String keyType) {

        UserKey userKey = new UserKey();
        userKey.setKeyType(keyType);
        userKey.setOrg(orgCode);
        userKey.setKey(security.getId());
        userKeyRepository.save(userKey);
        return userKey;
    }

    public UserKey getUserKey(String userKeyId) {
        return userKeyRepository.findOne(userKeyId);
    }

    public String getUserKeyByUserId(String userId) {
        Session session = entityManager.unwrap(org.hibernate.Session.class);
        StringBuilder sb = new StringBuilder();

        sb.append(" select id		                ");
        sb.append("       ,user_id		            ");
        sb.append("       ,key_id		            ");
        sb.append("       ,key_type		            ");
        sb.append("   from user_key 	            ");
        sb.append("  where user_id = '" + userId + "' ");
        sb.append("    and key_type = 'Personal' ");

        String sql = sb.toString();

        SQLQuery sqlQuery = session.createSQLQuery(sql);

        if (sqlQuery.list().size() == 0) {
            return null;
        } else {
            Object[] userKeyInfo = (Object[]) sqlQuery.list().get(0);
            String userKeyId = userKeyInfo[0].toString();
            return userKeyId;
        }
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public String getUserKeyByOrgCd(String orgCode) {

        Session session = entityManager.unwrap(org.hibernate.Session.class);
        StringBuilder sb = new StringBuilder();
        sb.append(" select id		                ");
        sb.append("       ,org_code		            ");
        sb.append("       ,key_id		            ");
        sb.append("       ,key_type		            ");
        sb.append("   from user_key 	            ");
        sb.append("  where org_code = '" + orgCode + "' ");
        sb.append("    and key_type = 'Org' ");

        String sql = sb.toString();

        SQLQuery sqlQuery = session.createSQLQuery(sql);

        if (sqlQuery.list().size() == 0) {
            return null;
        } else {
            Object[] userKeyInfo = (Object[]) sqlQuery.list().get(0);
            String userKeyId = userKeyInfo[0].toString();
            return userKeyId;
        }
    }

    public UserSecurity findByUserKey(String userKeyId){
        if (StringUtils.isEmpty(userKeyId)) {
            return null;
        } else {
            UserKey userKey = getUserKey(userKeyId);
            String userSecurityId = userKey.getKey();
            UserSecurity userSecurity = userSecurityRepository.findOne(userSecurityId);
            return userSecurity;
        }
    }


    public UserSecurity getUserPublicKeyByUserId(String userId) {

        String userKeyId = getUserKeyByUserId(userId);
        return findByUserKey(userKeyId);
    }


    @Transactional(Transactional.TxType.SUPPORTS)
    public UserSecurity getUserPublicKeyByOrgCd(String orgCode) {

        String userKeyId = getUserKeyByOrgCd(orgCode);
        return findByUserKey(userKeyId);
    }

    public void updateUserKey(UserKey userKey) {
        userKeyRepository.save(userKey);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public void deleteUserKey(String userKeyId) {
        userKeyRepository.delete(userKeyId);
    }
}