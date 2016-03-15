package com.yihu.ehr.ha.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.agModel.user.UserDetailModel;
import com.yihu.ehr.agModel.user.UsersModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.SystemDict.service.ConventionalDictEntryClient;
import com.yihu.ehr.ha.geography.service.AddressClient;
import com.yihu.ehr.ha.organization.service.OrganizationClient;
import com.yihu.ehr.ha.security.service.SecurityClient;
import com.yihu.ehr.ha.users.service.UserClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.geogrephy.MGeography;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.security.MUserSecurity;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import com.yihu.ehr.util.operator.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
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
    private AddressClient addressClient;

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

        ResponseEntity<List<MUser>> responseEntity = userClient.searchUsers(fields, filters, sorts, size, page);
        List<MUser> mUsers = responseEntity.getBody();
        List<UsersModel> usersModels = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(" yyyy-MM-dd HH:mm:ss ");
        for (MUser mUser : mUsers) {
            UsersModel usersModel = convertToModel(mUser, UsersModel.class);
            //获取用户类别字典
            if(StringUtils.isNotEmpty(mUser.getUserType())) {
                MConventionalDict dict = conventionalDictClient.getUserType(mUser.getUserType());
                usersModel.setUserTypeName(dict == null ? "" : dict.getValue());
            }
            //获取机构信息
            if(StringUtils.isNotEmpty(mUser.getOrganization())) {
                MOrganization organization = orgClient.getOrg(mUser.getOrganization());
                usersModel.setOrganizationName(organization == null ? "" : organization.getFullName());
            }
            usersModels.add(usersModel);
        }

        //获取总条数
        int totalCount = getTotalCount(responseEntity);

//        String count = response.getHeader(AgAdminConstants.ResourceCount);
//        int totalCount = StringUtils.isNotEmpty(count) ? Integer.parseInt(count) : 0;

        Envelop envelop = getResult(usersModels, totalCount, page, size);
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

        String errorMsg = null;
        if (StringUtils.isEmpty(detailModel.getLoginCode())) {
            errorMsg += "账户不能为空";
        }
        if (StringUtils.isEmpty(detailModel.getRealName())) {
            errorMsg += "姓名不能为空!";
        }
        if (StringUtils.isEmpty(detailModel.getIdCardNo())) {
            errorMsg += "身份证号不能为空!";
        }
        if (StringUtils.isEmpty(detailModel.getEmail())) {
            errorMsg += "邮箱不能为空!";
        }
        if (StringUtils.isEmpty(detailModel.getTelephone())) {
            errorMsg += "电话号码不能为空!";
        }
        if (StringUtils.isNotEmpty(errorMsg)) {
            return failed(errorMsg);
        }
        if (userClient.isLoginCodeExists(detailModel.getLoginCode())) {
            return failed("账户已存在!");
        }
        if (userClient.isIdCardExists(detailModel.getIdCardNo())) {
            return failed("身份证号已存在!");
        }

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

        String errorMsg = null;
        if (StringUtils.isEmpty(detailModel.getLoginCode())) {
            errorMsg += "账户不能为空";
        }
        if (StringUtils.isEmpty(detailModel.getRealName())) {
            errorMsg += "姓名不能为空!";
        }
        if (StringUtils.isEmpty(detailModel.getIdCardNo())) {
            errorMsg += "身份证号不能为空!";
        }
        if (StringUtils.isEmpty(detailModel.getEmail())) {
            errorMsg += "邮箱不能为空!";
        }
        if (StringUtils.isEmpty(detailModel.getTelephone())) {
            errorMsg += "电话号码不能为空!";
        }
        if (StringUtils.isNotEmpty(errorMsg)) {
            return failed(errorMsg);
        }
        MUser mUser = userClient.getUser(detailModel.getId());
        if (!mUser.getLoginCode().equals(detailModel.getLoginCode())
                && userClient.isLoginCodeExists(detailModel.getLoginCode())) {
            return failed("账户已存在!");
        }

        if (!mUser.getIdCardNo().equals(detailModel.getIdCardNo())
                && userClient.isIdCardExists(detailModel.getIdCardNo())) {
            return failed("身份证号已存在!");
        }

        mUser = convertToModel(detailModel, MUser.class);
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

        try {
            MUser mUser = userClient.getUser(userId);
            if (mUser == null) {
                return failed("用户信息获取失败!");
            }
            UserDetailModel detailModel = MUserToUserDetailModel(mUser);

            return success(detailModel);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
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

        MUser mUser = userClient.getUserByNameAndPassword(loginCode, psw);
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

    @RequestMapping(value = "/users/existence/{login_code}", method = RequestMethod.GET)
    @ApiOperation(value = "根据登录账号获取当前用户", notes = "根据登陆用户名及密码验证用户")
    public Envelop existence(
            @ApiParam(name = "login_code", value = "登录账号", defaultValue = "")
            @PathVariable(value = "login_code") String loginCode) {
        Envelop envelop = new Envelop();

        boolean bo = userClient.isLoginCodeExists(loginCode);
        envelop.setSuccessFlg(bo);
        return envelop;

    }



    /**
     * 将 MUser 转为 UserDetailModel
     *
     * @param mUser
     * @return UserDetailModel
     */
    public UserDetailModel MUserToUserDetailModel(MUser mUser) {

        UserDetailModel detailModel = convertToModel(mUser, UserDetailModel.class);

        //获取婚姻状态代码
        String marryCode = mUser.getMartialStatus();
        MConventionalDict dict=null;
        if(StringUtils.isNotEmpty(marryCode)) {
            dict = conventionalDictClient.getMartialStatus(marryCode);
            detailModel.setMartialStatusName(dict == null ? "" : dict.getValue());
        }
        //获取用户类型
        String userType = mUser.getUserType();
        if (StringUtils.isNotEmpty(userType)) {
            dict = conventionalDictClient.getUserType(userType);
            detailModel.setUserTypeName(dict == null ? "" : dict.getValue());
        }
        //获取归属机构
        String orgCode = mUser.getOrganization();
        if(StringUtils.isNotEmpty(orgCode)) {
            MOrganization orgModel = orgClient.getOrg(orgCode);
            MGeography  mGeography = addressClient.getAddressById(orgModel.getLocation());
            String orgAddress = mGeography.getProvince()+mGeography.getCity()+mGeography.getDistrict()+orgModel.getFullName();
            detailModel.setOrganizationName(orgModel == null ? "" : orgAddress);
        }
        //获取秘钥信息
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


    /*@RequestMapping(value = "/users/upload/", method = RequestMethod.POST)
    @ApiOperation(value = "", notes = "")
    public String upload(
            @ApiParam(name = "request")
            @RequestParam(value = "request")HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            request.setCharacterEncoding("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //获取流
        InputStream inputStream = request.getInputStream();
        //获取文件名
        String fileName = request.getParameter("name");
        if (fileName == null || fileName.equals("")) {
            return null;
        }
        //获取文件扩展名
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        //获取文件名
        String description = null;
        if ((fileName != null) && (fileName.length() > 0)) {
            int dot = fileName.lastIndexOf('.');
            if ((dot > -1) && (dot < (fileName.length()))) {
                description = fileName.substring(0, dot);
            }
        }
        ObjectNode objectNode = null;
        String path = null;
        try {
            FastDFSUtil fastDFSUtil = new FastDFSUtil();
            objectNode = fastDFSUtil.upload(inputStream, fileExtension, description);
            String groupName = objectNode.get("groupName").toString();
            String remoteFileName = objectNode.get("remoteFileName").toString();
            path = "{groupName:" + groupName + ",remoteFileName:" + remoteFileName + "}";
        } catch (Exception e) {
            //LogService.getLogger(user.class).error("用户头像上传失败；错误代码："+e);
        }
        return path;
    }*/

}
