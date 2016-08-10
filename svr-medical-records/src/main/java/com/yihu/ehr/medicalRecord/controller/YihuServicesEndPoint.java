package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.medicalRecord.comom.UserMgmt;
import com.yihu.ehr.medicalRecord.comom.YihuResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by shine on 2016/7/14.
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "总部接口联调", description = "总部接口联调")
public class YihuServicesEndPoint extends BaseRestEndPoint {

    @Autowired
    UserMgmt userMgmt;

    @ApiOperation("健康之路账号登录")
    @RequestMapping(value =  "/medicalRecords/yihu/userLogin", method = RequestMethod.GET)
    public YihuResponse userLogin(
            @ApiParam(name = "loginID", value = "登录账号",defaultValue = "18064563372")
            @RequestParam(value = "loginID", required = true)String loginID,
            @ApiParam(name = "loginPwd", value = "登录密码",defaultValue = "123456")
            @RequestParam(value = "loginPwd", required = true)String loginPwd) {
        return userMgmt.userLogin(loginID,loginPwd);
    }

    @ApiOperation("单点登录接口")
    @RequestMapping(value =  "/medicalRecords/yihu/userSessionCheck", method = RequestMethod.GET)
    public YihuResponse userSessionCheck(
            @ApiParam(name = "userId", value = "用户ID",defaultValue = "B834C7A0417E4CA4BEC00FD3524EE870")
            @RequestParam(value = "userId", required = true)String userId,
            @ApiParam(name = "ticket", value = "ticket")
            @RequestParam(value = "ticket", required = true)String ticket,
            @ApiParam(name="appUid",value="应用轻ID",defaultValue = "KK66VXAFN9QTVLTI7NWJLEX3NXH4BUIRGOV83331O75")
            @RequestParam(value="appUid",required = true)String appUid)
    {
        return userMgmt.userSessionCheck(userId,ticket,appUid);
    }

    @ApiOperation("获取用户信息")
    @RequestMapping(value =  "/medicalRecords/yihu/queryUserInfoByID", method = RequestMethod.GET)
    public YihuResponse queryUserInfoByID (
            @ApiParam(name = "userId", value = "用户ID",defaultValue = "B834C7A0417E4CA4BEC00FD3524EE870")
            @RequestParam(value = "userId", required = true)String userId)
    {
        return userMgmt.queryUserInfoByID(userId);
    }
}
