package com.yihu.ehr.ha.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.user.UserDetailModel;
import com.yihu.ehr.agModel.user.UsersModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.SystemDict.service.ConventionalDictEntryClient;
import com.yihu.ehr.ha.organization.service.OrganizationClient;
import com.yihu.ehr.ha.security.service.SecurityClient;
import com.yihu.ehr.ha.users.service.UserClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.security.MUserSecurity;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import com.yihu.ehr.util.operator.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by AndyCai on 2016/1/21.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "user", description = "用户管理接口，用于用户信息管理", tags = {"用户管理接口"})
public class UserController extends BaseController {

    @Autowired
    private UserClient userClient;

    @Autowired
    private ConventionalDictEntryClient conventionalDictClient;

    @Autowired
    private OrganizationClient orgClient;

    @Autowired
    private SecurityClient securityClient;

    @Autowired
    private ObjectMapper objectMapper;


    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户列表", notes = "根据查询条件获取用户列表在前端表格展示")
    public Envelop searchUsers(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {

        List<MUser> mUsers = userClient.searchUsers(fields, filters, sorts, size, page);
        List<UsersModel> usersModels = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss ");
        for (MUser mUser : mUsers) {
            UsersModel usersModel = convertToModel(mUser, UsersModel.class);
            //获取用户类别字典
            MConventionalDict dict = conventionalDictClient.getUserType(mUser.getUserType());
            usersModel.setUserTypeName(dict == null ? "" : dict.getValue());

            //获取机构信息
            MOrganization organization = orgClient.getOrg(mUser.getOrganization());
            usersModel.setOrganizationName(organization == null ? "" : organization.getFullName());

            usersModels.add(usersModel);
        }

        //TODO:获取总条数
//        String count = response.getHeader(AgAdminConstants.ResourceCount);
//        int totalCount = StringUtils.isNotEmpty(count) ? Integer.parseInt(count) : 0;

        Envelop envelop = getResult(usersModels, 0, page, size);
        return envelop;
    }

    @RequestMapping(value = "/users/{user_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户", notes = "根据用户id删除用户")
    public Envelop deleteUser(
            @ApiParam(name = "user_id", value = "用户编号", defaultValue = "")
            @PathVariable(value = "user_id") String userId) throws Exception {

        MUserSecurity userSecurity = securityClient.getUserSecurityByUserId(userId);
        if (userSecurity != null) {
            String userKeyId = securityClient.getUserKeyByUserId(userId);
            securityClient.deleteSecurity(userSecurity.getId());
            securityClient.deleteUserKey(userKeyId);
        }

        boolean result = userClient.deleteUser(userId);
        if (!result) {
            return failed("删除失败!");
        }
        return success(null);
    }


    @RequestMapping(value = "/users", method = RequestMethod.POST)
    @ApiOperation(value = "创建用户", notes = "重新绑定用户信息")
    public Envelop createUser(
            @ApiParam(name = "user_json_data", value = "", defaultValue = "")
            @RequestParam(value = "user_json_data") String userJsonData) throws Exception {

        UserDetailModel detailModel = objectMapper.readValue(userJsonData, UserDetailModel.class);
        MUser mUser = convertToModel(detailModel, MUser.class);
        mUser = userClient.createUser(objectMapper.writeValueAsString(mUser));
        if (mUser == null) {
            return failed("保存失败!");
        }
        detailModel = convertToModel(mUser, UserDetailModel.class);
        return success(detailModel);
    }


    @RequestMapping(value = "/users", method = RequestMethod.PUT)
    @ApiOperation(value = "修改用户", notes = "重新绑定用户信息")
    public Envelop updateUser(
            @ApiParam(name = "user_json_data", value = "", defaultValue = "")
            @RequestParam(value = "user_json_data") String userJsonData) throws Exception {

        UserDetailModel detailModel = objectMapper.readValue(userJsonData, UserDetailModel.class);
        MUser mUser = convertToModel(detailModel, MUser.class);
        mUser = userClient.updateUser(objectMapper.writeValueAsString(mUser));
        if (mUser == null) {
            return failed("保存失败!");
        }
        detailModel = convertToModel(mUser, UserDetailModel.class);

        return success(detailModel);
    }


    @RequestMapping(value = "users/{user_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户信息", notes = "包括地址信息等")
    public Envelop getUser(
            @ApiParam(name = "user_id", value = "", defaultValue = "")
            @PathVariable(value = "user_id") String userId) {

        MUser mUser = userClient.getUser(userId);
        if (mUser == null) {
            return failed("用户信息获取失败!");
        }
        UserDetailModel detailModel = MUserToUserDetailModel(mUser);

        return success(detailModel);
    }


    @RequestMapping(value = "/users/{user_id}", method = RequestMethod.PUT)
    @ApiOperation(value = "改变用户状态", notes = "根据用户状态改变当前用户状态")
    public boolean activityUser(
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "activity", value = "激活状态", defaultValue = "")
            @RequestParam(value = "activity") boolean activity) throws Exception {
        return userClient.activityUser(userId, activity);
    }


    @RequestMapping(value = "users/password/{user_id}", method = RequestMethod.PUT)
    @ApiOperation(value = "重设密码", notes = "用户忘记密码管理员帮助重新还原密码，初始密码123456")
    public boolean resetPass(
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId) throws Exception {

        return userClient.resetPass(userId);
    }


    @RequestMapping(value = "/users/binding/{user_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "取消关联绑定", notes = "取消相关信息绑定")
    public boolean unBinding(
            @ApiParam(name = "user_id", value = "", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "type", value = "", defaultValue = "")
            @RequestParam(value = "type") String type) {

        return userClient.unBinding(userId, type);
    }

    /**
     * 重新分配秘钥
     *
     * @param loginCode 账号
     * @return map  key{publicKey:公钥；validTime：有效时间; startTime：生效时间}
     */
    @RequestMapping(value = "/users/key/{login_code}", method = RequestMethod.PUT)
    @ApiOperation(value = "重新分配密钥", notes = "重新分配密钥")
    public Map<String, String> distributeKey(
            @ApiParam(name = "login_code", value = "登录帐号", defaultValue = "")
            @PathVariable(value = "login_code") String loginCode) {
        MUser mUser = userClient.getUserByLoginCode(loginCode);
        if (mUser == null) {
            return null;
        }
        return userClient.distributeKey(mUser.getId());
    }


    /**
     * 根据登陆用户名及密码验证用户.
     *
     * @param loginCode
     * @param psw
     */
    @RequestMapping(value = "/users/verification/{login_code}", method = RequestMethod.GET)
    @ApiOperation(value = "根据登陆用户名及密码验证用户", notes = "根据登陆用户名及密码验证用户")
    public Envelop loginVerification(
            @ApiParam(name = "login_code", value = "登录账号", defaultValue = "")
            @PathVariable(value = "login_code") String loginCode,
            @ApiParam(name = "psw", value = "密码", defaultValue = "")
            @RequestParam(value = "psw") String psw) {

        MUser mUser = userClient.loginVerification(loginCode, psw);
        if (mUser == null) {
            return failed("用户信息获取失败!");
        }
        UserDetailModel detailModel = MUserToUserDetailModel(mUser);

        return success(detailModel);
    }

    /**
     * 根据loginCode 获取user
     *
     * @param loginCode
     * @return
     */
    @RequestMapping(value = "/users/login/{login_code}", method = RequestMethod.GET)
    @ApiOperation(value = "根据登录账号获取当前用户", notes = "根据登陆用户名及密码验证用户")
    public Envelop getUserByLoginCode(
            @ApiParam(name = "login_code", value = "登录账号", defaultValue = "")
            @PathVariable(value = "login_code") String loginCode) {

        MUser mUser = userClient.getUserByLoginCode(loginCode);
        if (mUser == null) {
            return failed("用户信息获取失败!");
        }
        UserDetailModel detailModel = MUserToUserDetailModel(mUser);

        return success(detailModel);
    }

    /**
     * 将 MUser 转为 UserDetailModel
     *
     * @param mUser
     * @return UserDetailModel
     */
    public UserDetailModel MUserToUserDetailModel(MUser mUser) {

        UserDetailModel detailModel = convertToModel(mUser, UserDetailModel.class);

        //TODO:获取婚姻状态代码
        String marryCode = mUser.getMartialStatus();
        MConventionalDict dict = conventionalDictClient.getMartialStatus(marryCode);
        detailModel.setMartialStatusName(dict == null ? "" : dict.getValue());

        //TODO:获取用户类型
        String userType = mUser.getUserType();
        dict = conventionalDictClient.getUserType(userType);
        detailModel.setUserTypeName(dict == null ? "" : dict.getValue());

        //TODO:获取归属机构
        String orgCode = mUser.getOrganization();
        MOrganization orgModel = orgClient.getOrg(orgCode);
        detailModel.setOrganizationName(orgModel == null ? "" : orgModel.getFullName());

        //TODO:获取秘钥信息
        MUserSecurity userSecurity = securityClient.getUserSecurityByUserId(mUser.getId());
        if (userSecurity != null) {
            detailModel.setPublicKey(userSecurity.getPublicKey());
            String validTime = DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT)
                    + "~" + DateUtil.toString(userSecurity.getExpiryDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT);
            detailModel.setValidTime(validTime);
            detailModel.setStartTime(DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT));
        }
        return detailModel;
    }
}
