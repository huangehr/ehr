package com.yihu.ehr.medicalRecord.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseRestEndPoint;
import com.yihu.ehr.medicalRecord.model.MrDoctorsEntity;
import com.yihu.ehr.medicalRecord.service.DoctorService;
import com.yihu.ehr.yihu.UserMgmt;
import com.yihu.ehr.yihu.YihuResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
            @ApiParam(name = "userId", value = "用户ID")
            @RequestParam(value = "userId", required = true)String userId,
            @ApiParam(name = "ticket", value = "ticket")
            @RequestParam(value = "ticket", required = true)String ticket)
    {
        return userMgmt.userSessionCheck(userId,ticket);
    }

    @ApiOperation("获取用户信息")
    @RequestMapping(value =  "/medicalRecords/yihu/queryUserInfoByID", method = RequestMethod.GET)
    public YihuResponse queryUserInfoByID (
            @ApiParam(name = "userId", value = "用户ID")
            @RequestParam(value = "userId", required = true)String userId)
    {
        return userMgmt.queryUserInfoByID(userId);
    }
}
