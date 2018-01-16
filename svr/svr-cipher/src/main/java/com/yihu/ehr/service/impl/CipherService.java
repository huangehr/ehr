package com.yihu.ehr.service.impl;

import com.yihu.ehr.service.IBaseCipher;
import com.yihu.ehr.service.ICipherService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

/**
 * 加密解密服务
 *
 * @author lyr
 * @version 1.0
 * @created on 2016/5/10.
 */
@Service
public class CipherService implements ICipherService {

    @Resource
    private IBaseCipher desCipher;

    /**
     * 明文加密
     *
     * @param plainText String 明文数据
     * @param type String 加密类型
     * @return String 密文数据
     */
    public String encrypt(String plainText,String type) throws Exception
    {
        String cipherText = "";
        switch (type)
        {
            case "des":
                cipherText = desCipher.encrypt(plainText);
                break;
            default:
                break;
        }

        return cipherText;
    }

    /**
     * 密文解密
     *
     * @param cipherText String 密文数据
     * @param type String 解密类型
     * @return String 明文数据
     */
    public String decrypt(String cipherText,String type) throws Exception
    {
        String plainText = "";

        switch (type)
        {
            case "des":
                plainText = desCipher.decrypt(cipherText);
                break;
            default:
                break;
        }

        return plainText;
    }
}
