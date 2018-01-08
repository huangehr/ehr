package com.yihu.ehr.basic.security.service;

import com.yihu.ehr.basic.security.dao.UserKeyRepository;
import com.yihu.ehr.basic.security.dao.UserSecurityRepository;
import com.yihu.ehr.entity.security.UserKey;
import com.yihu.ehr.entity.security.UserSecurity;
import com.yihu.ehr.util.encrypt.RSA;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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
public class UserSecurityService {

    @Autowired
    private UserSecurityRepository keyRepository;

    @Autowired
    private UserKeyRepository keyMapRepository;

    HashMap<String, java.security.Key> hashMap;

    final static String PersonalKeyType = "Personal";
    final static String orgKeyType = "Org";

    public Map setUp() throws Exception {
        hashMap = RSA.generateKeys();
        return hashMap;
    }

    public UserSecurity createKey() throws Exception {
        UserSecurity key = new UserSecurity();

        Map userRsa = setUp();

        String publicKey = RSA.encodeKey((java.security.Key) userRsa.get(RSA.PUBLIC_KEY));
        String privateKey = RSA.encodeKey((java.security.Key) userRsa.get(RSA.PRIVATE_KEY));

        Date currentDate = new Date();
        Date fromDate = currentDate;                                //生效时间默认为当前日期
        Date expiryDate = DateUtils.addYears(currentDate, 100);      //截止日期为生效日期往后推一百年
        Integer valid = 1;                                          //默认设置为生效状态

        key.setValid(valid);
        key.setPublicKey(publicKey);
        key.setPrivateKey(privateKey);
        key.setFromDate(fromDate);
        key.setExpiryDate(expiryDate);

        keyRepository.save(key);
        return key;
    }

    public UserSecurity createKeyByUserId(String userId) throws Exception {
        UserSecurity key = createKey();
        createUserKey(key.getId(), userId, PersonalKeyType);

        return key;
    }

    public UserSecurity createKeyByOrgCode(String orgCode) throws Exception {
        UserSecurity key = createKey();

        createOrgKey(key, orgCode, orgKeyType);
        return key;
    }

    public UserSecurity getKeyByUserId(String userId, boolean isNull) throws Exception {
        List<UserKey> keyMapList = keyMapRepository.findByUserId(userId);
        if (keyMapList!=null && keyMapList.size()>0){
            //1-2-2当UserKey存在的情况下，查询用户关联的用户密钥信息。
            String userSecurityId = keyMapList.get(0).getKey();
            return keyRepository.findOne(userSecurityId);
        }else {
            if (isNull) {
                return null;
            }else{
                UserSecurity key = createKey();
                createUserKey(key.getId(), userId, PersonalKeyType);
                return key;
            }

        }
    }

    public void deleteKey(String id) {
        UserSecurity key = keyRepository.findOne(id);
        keyRepository.delete(key);
    }

    public UserKey createUserKey(String keyId, String userId, String keyType) {
        UserKey keyMap = new UserKey();
        keyMap.setKeyType(keyType);
        keyMap.setUser(userId);
        keyMap.setKey(keyId);

        keyMapRepository.save(keyMap);
        return keyMap;
    }

    public UserKey createOrgKey(UserSecurity key, String orgCode, String keyType) {
        UserKey keyMap = new UserKey();
        keyMap.setKeyType(keyType);
        keyMap.setOrg(orgCode);
        keyMap.setKey(key.getId());
        keyMapRepository.save(keyMap);
        return keyMap;
    }

    public UserKey getKeyMap(String keyId) {
        return keyMapRepository.findOne(keyId);
    }

    public String getOrgKey(String orgCode) {
        List<UserKey> keyMaps = keyMapRepository.findByOrgCdode(orgCode);
        if (keyMaps != null && keyMaps.size() > 0) {
            return keyMaps.get(0).getId();
        } else {
            return null;
        }
    }

    public UserSecurity findKey(String userKeyId) {
        if (StringUtils.isEmpty(userKeyId)) {
            return null;
        } else {
            UserKey keyMap = getKeyMap(userKeyId);
            String userSecurityId = keyMap.getKey();
            UserSecurity key = keyRepository.findOne(userSecurityId);
            return key;
        }
    }

    public UserSecurity getKeyByOrgCode(String orgCode) {
        String userKeyId = getOrgKey(orgCode);
        return findKey(userKeyId);
    }

    public void deleteKeyMap(String id) {
        keyMapRepository.delete(id);
    }

    public  List<UserKey> getKeyMapByOrgCode(String orgCode)
    {
        return keyMapRepository.findByOrgCdode(orgCode);
    }

    public List<UserKey> getKeyMapByUserId(String userId)
    {
        return keyMapRepository.findByUserId(userId);
    }

    public void deleteKey(List<UserKey> keyMaps){

        if(keyMaps!=null && keyMaps.size()>0)
        {
            for (UserKey keyMap:keyMaps)
            {
                String keyMapId = keyMap.getId();
                String keyId = keyMap.getKey();
                this.deleteKey(keyId);// user_security
                this.deleteKeyMap(keyMapId);//user_key
            }
        }
    }
}