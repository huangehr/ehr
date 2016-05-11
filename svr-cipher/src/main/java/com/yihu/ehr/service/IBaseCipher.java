package com.yihu.ehr.service;

/**
 * 加密解密基础接口
 *
 * @author lyr
 * @version 1.0
 * @created on 2016/5/10.
 */
public interface IBaseCipher {
    /**
     * 明文加密
     *
     * @param plainText String 明文数据
     * @return String 密文数据
     */
    String encrypt(String plainText) throws Exception;

    /**
     * 密文加密
     *
     * @param cipher String 密文数据
     * @return String 明文数据
     */
    String decrypt(String cipher) throws Exception;
}
