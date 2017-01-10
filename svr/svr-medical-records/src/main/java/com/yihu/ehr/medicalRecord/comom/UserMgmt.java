package com.yihu.ehr.medicalRecord.comom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hzp on 2016/7/28.
 * 用户账号管理接口
 */
@Service
public class UserMgmt {

    @Autowired
    private YihuHttpService service;

    /***
     * 健康之路账号登录
     */
    public YihuResponse userLogin(String loginID, String loginPwd)
    {
        return service.doPost("UserMgmt.Account.userLogin", "{ \"loginID\":\"" + loginID + "\",\"loginPwd\":\"" + loginPwd + "\",\"isNesdSession\": 1 }");
    }

    /***
     * 单点登录接口
     */
    public YihuResponse userSessionCheck(String userId, String ticket, String appUid)
    {
        return service.doPost("UserMgmt.Account.userSessionCheck", "{ \"userId\":\"" + userId + "\",\"ticket\":\"" + ticket + "\",\"appUid\": \""+appUid+"\" }");
    }

    /***
     * 获取用户信息
     */
    public YihuResponse queryUserInfoByID (String userID)
    {
        return service.doPost("UserMgmt.User.queryUserInfoByID", "{ \"UserID\":\"" + userID + "\" }");
    }

}
