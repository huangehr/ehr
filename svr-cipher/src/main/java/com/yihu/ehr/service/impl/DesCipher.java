package com.yihu.ehr.service.impl;

import com.yihu.ehr.foreign.IDictService;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.service.IBaseCipher;
import com.yihu.ehr.util.encrypt.DES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Random;

/**
 * DES加密解密基础
 *
 * @author lyr
 * @version 1.0
 * @created on 2016/5/10.
 */
@Component("desCipher")
public class DesCipher implements IBaseCipher {

    @Autowired
    private IDictService dictService;

    /**
     * 明文加密
     *
     * @param plainText String 明文数据
     * @return String 密文数据
     */
    public String encrypt(String plainText) throws Exception{
        MDictionaryEntry dictEntry = dictService.getDictEntry(28,"DES_KEY");

        return DES.encrypt(plainText,dictEntry.getValue());
    }

    /**
     * 密文加密
     *
     * @param cipher String 密文数据
     * @return String 明文数据
     */
    public String decrypt(String cipher) throws Exception{
        MDictionaryEntry dictEntry = dictService.getDictEntry(28,"DES_KEY");

        return DES.decrypt(cipher,dictEntry.getValue());
    }
}
