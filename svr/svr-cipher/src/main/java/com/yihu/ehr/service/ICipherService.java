package com.yihu.ehr.service;

/**
 * 加密解密服务
 *
 * @author lyr
 * @version 1.0
 * @created on 2016/5/10.
 */
public interface ICipherService {

    /**
     * 明文加密
     *
     * @param plainText String 明文数据
     * @param type String 加密类型
     * @return String 密文数据
     */
    String encrypt(String plainText,String type) throws Exception;

    /**
     * 密文解密
     *
     * @param cipherText String 密文数据
     * @param type String 解密类型
     * @return String 明文数据
     */
    String decrypt(String cipherText,String type) throws Exception;
}
