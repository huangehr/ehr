package com.yihu.ehr.redis;

import com.jcraft.jsch.UserInfo;

/**
 * JSch UserInfo 的实现
 *
 * @author 张进军
 * @date 2017/12/11 16:23
 */
public class DefaultJSchUserInfo implements UserInfo {

    @Override
    public String getPassphrase() {
        System.out.println("getPassphrase");
        return null;
    }

    @Override
    public String getPassword() {
        System.out.println("getPassword");
        return null;
    }

    @Override
    public boolean promptPassword(String s) {
        System.out.println("promptPassword:" + s);
        return false;
    }

    @Override
    public boolean promptPassphrase(String s) {
        System.out.println("promptPassphrase:" + s);
        return false;
    }

    @Override
    public boolean promptYesNo(String s) {
        System.out.println("promptYesNo:" + s);
        return true; // notice here!
    }

    @Override
    public void showMessage(String s) {
        System.out.println("showMessage:" + s);
    }

}
