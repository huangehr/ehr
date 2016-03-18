package com.yihu.ehr.security.service;

import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.security.feign.UserClient;
import com.yihu.ehr.util.DateUtil;
import com.yihu.ehr.util.encrypt.RSA;
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
public class KeyManager {

    @Autowired
    private XKeyRepository keyRepository;

    @Autowired
    private XKeyMapRepository keyMapRepository;

    @Autowired
    private UserClient userClient;

    HashMap<String, java.security.Key> hashMap;

    final static String PersonalKeyType = "Personal";
    final static String orgKeyType = "Org";

    public Map setUp() throws Exception {
        hashMap = RSA.generateKeys();
        return hashMap;
    }

    public Key createKey() throws Exception {
        Key key = new Key();

        Map userRsa = setUp();

        String publicKey = RSA.encodeKey((java.security.Key) userRsa.get(RSA.PUBLIC_KEY));
        String privateKey = RSA.encodeKey((java.security.Key) userRsa.get(RSA.PRIVATE_KEY));

        Date currentDate = new Date();
        Date fromDate = currentDate;                                //生效时间默认为当前日期
        Date expiryDate = DateUtil.add(currentDate, "y", 100);      //截止日期为生效日期往后推一百年
        Integer valid = 1;                                          //默认设置为生效状态

        key.setValid(valid);
        key.setPublicKey(publicKey);
        key.setPrivateKey(privateKey);
        key.setFromDate(fromDate);
        key.setExpiryDate(expiryDate);

        keyRepository.save(key);
        return key;
    }

    public Key createKeyByUserId(String userId) throws Exception {
        Key key = createKey();
        createUserKey(key.getId(), userId, PersonalKeyType);

        return key;
    }

    public Key createKeyByOrgCode(String orgCode) throws Exception {
        Key key = createKey();

        createOrgKey(key, orgCode, orgKeyType);
        return key;
    }

    public Key getKeyByUserId(String userId) throws Exception {
        KeyMap keyMap = getKeyMap(userId);
        if (keyMap == null) {
            Key key = createKey();
            createUserKey(key.getId(), userId, PersonalKeyType);
            return key;
        } else {
            //1-2-2当UserKey存在的情况下，查询用户关联的用户密钥信息。
            String userSecurityId = keyMap.getKey();
            return keyRepository.findOne(userSecurityId);
        }
    }

    public void deleteKey(String id) {
        Key key = keyRepository.findOne(id);
        keyRepository.delete(key);
    }

    public KeyMap createUserKey(String keyId, String userId, String keyType) {
        KeyMap keyMap = new KeyMap();
        keyMap.setKeyType(keyType);
        keyMap.setUser(userId);
        keyMap.setKey(keyId);

        keyMapRepository.save(keyMap);
        return keyMap;
    }

    public KeyMap createOrgKey(Key key, String orgCode, String keyType) {
        KeyMap keyMap = new KeyMap();
        keyMap.setKeyType(keyType);
        keyMap.setOrg(orgCode);
        keyMap.setKey(key.getId());
        keyMapRepository.save(keyMap);
        return keyMap;
    }

    public KeyMap getKeyMap(String keyId) {
        return keyMapRepository.findOne(keyId);
    }

    public String getOrgKey(String orgCode) {
        List<KeyMap> keyMaps = keyMapRepository.findByOrgCdode(orgCode);
        if (keyMaps != null && keyMaps.size() > 0) {
            return keyMaps.get(0).getId();
        } else {
            return null;
        }
    }

    public Key findKey(String userKeyId) {
        if (StringUtils.isEmpty(userKeyId)) {
            return null;
        } else {
            KeyMap keyMap = getKeyMap(userKeyId);
            String userSecurityId = keyMap.getKey();
            Key key = keyRepository.findOne(userSecurityId);
            return key;
        }
    }

    public Key getKeyByOrgCode(String orgCode) {
        String userKeyId = getOrgKey(orgCode);
        return findKey(userKeyId);
    }

    public void deleteKeyMap(String id) {
        keyMapRepository.delete(id);
    }

}