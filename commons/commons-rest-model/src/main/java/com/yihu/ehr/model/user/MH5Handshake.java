package com.yihu.ehr.model.user;

import java.io.Serializable;

/**
 * 提供二次握手的URL,返回实体
 *
 * @author <a href="mailto:yhy23456@163.com">huiyang.yu</a>
 * @since 2018.03.14
 */
public class MH5Handshake implements Serializable {


    /**
     * code : 10000
     * message : Yes
     * userName : 王X
     * tel : 18059159651
     * sex : 1
     * cardNo : 350583198411092252
     */

    /**
     * 10000表示成功
     * -10000 表示账号不存在
     * -100001 表示签名校验失败
     * -100002~-20000其他系统级的错误
     */
    private String code;
    /**
     * 为二次握手的详细说明
     */
    private String message;
    /**
     * 用户的姓名
     */
    private String userName;
    /**
     * 用户的手机号
     */
    private String tel;
    /**
     * 用户的性别(1男,2女)
     */
    private int sex;
    /**
     * 身份证号码
     */
    private String cardNo;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    @Override
    public String toString() {
        return "MH5Handshake{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", userName='" + userName + '\'' +
                ", tel='" + tel + '\'' +
                ", sex=" + sex +
                ", cardNo='" + cardNo + '\'' +
                '}';
    }
}
