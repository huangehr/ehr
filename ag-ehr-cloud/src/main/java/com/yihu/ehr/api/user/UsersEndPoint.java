package com.yihu.ehr.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.geogrephy.GeographyModel;
import com.yihu.ehr.agModel.patient.PatientDetailModel;
import com.yihu.ehr.agModel.user.UserDetailModel;
import com.yihu.ehr.api.model.OrgModel;
import com.yihu.ehr.api.model.UserModel;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.feign.*;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.geography.MGeography;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.model.user.MRoleUser;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 用户。
 *
 * @author Sand
 * @version 1.0
 * @created 2016.02.05 10:55
 */
@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "users", description = "用户服务")
public class UsersEndPoint extends BaseController {
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private SecurityClient securityClient;

    @Autowired
    private OrganizationClient organizationClient;
    @Autowired
    private RoleUserClient roleUserClient;

    @Autowired
    private ConventionalDictEntryClient conventionalDictClient;

    private String regex = "^[A-Za-z0-9\\-]+$";
    private Pattern pattern = Pattern.compile(regex);

    @ApiOperation("获取用户列表")
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<UserModel> getUsers(
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
        List<UserModel> UserModels = new ArrayList<>();
        List<com.yihu.ehr.model.user.MUser> mUsers = userClient.getUsers(fields, filters, sorts, size, page);
        for (com.yihu.ehr.model.user.MUser mUser : mUsers) {
            UserModel UserModel = convertToModel(mUser, UserModel.class);
            if (mUser.getOrganization() != null) {
                MOrganization mOrganization = organizationClient.getOrg(mUser.getOrganization());
                UserModel.setOrganization(convertToModel(mOrganization, OrgModel.class));
            }

            UserModels.add(UserModel);
        }

        return UserModels;
    }

    @ApiOperation("获取用户")
    @RequestMapping(value = "/users/{user_name}", method = RequestMethod.GET)
    public UserModel getUser(
            @ApiParam("user_name")
            @PathVariable("user_name") String userName) {
        MUser mUser = userClient.getUserByUserName(userName);
        UserModel UserModel = convertToModel(mUser, UserModel.class);

        if (mUser.getOrganization() != null) {
            MOrganization mOrganization = organizationClient.getOrg(mUser.getOrganization());
            UserModel.setOrganization(convertToModel(mOrganization, OrgModel.class));
        }

        return UserModel;
    }

    @ApiOperation("获取用户公钥")
    @RequestMapping(value = "/users/{user_name}/key", method = RequestMethod.GET)
    public MKey getPublicKey(@ApiParam("user_name")
                             @PathVariable("user_name")
                             String userName) {
        MKey key = securityClient.getUserKey(userName);
        key.setPrivateKey("");

        return key;
    }

    @ApiOperation("获取用户列表")
    @RequestMapping(value = "/getUserList", method = RequestMethod.GET)
    public Envelop getUserList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,realName,idCardNo,gender,createDate")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+realName,+createDate")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {
        Envelop envelop = new Envelop();
        List<UserModel> UserModels = new ArrayList<>();
        List<com.yihu.ehr.model.user.MUser> mUsers = userClient.getUsers(fields, filters, sorts, size, page);
        for (com.yihu.ehr.model.user.MUser mUser : mUsers) {
            UserModel UserModel = convertToModel(mUser, UserModel.class);
            if (mUser.getOrganization() != null) {
                MOrganization mOrganization = organizationClient.getOrg(mUser.getOrganization());
                UserModel.setOrganization(convertToModel(mOrganization, OrgModel.class));
            }
            UserModels.add(UserModel);
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(UserModels);
        envelop.setCurrPage(page);
        envelop.setPageSize(size);
        return envelop;
    }

    @RequestMapping(value = "/createUser", method = RequestMethod.POST)
    @ApiOperation(value = "创建用户", notes = "创建用户信息")
    public Envelop createUser(
            @ApiParam(name = "user_json_data", value = "", defaultValue = "")
            @RequestParam(value = "user_json_data") String userJsonData) {
        try {
            UserDetailModel detailModel = objectMapper.readValue(userJsonData, UserDetailModel.class);
            String idCard = detailModel.getIdCardNo();
            String userType = detailModel.getUserType();
            String errorMsg = null;

            if (StringUtils.isEmpty(detailModel.getLoginCode())) {
                errorMsg += "账户不能为空";
            }else  if (userClient.isUserNameExists(detailModel.getLoginCode())) {
                return failed("账户已存在!");
            }

            if (StringUtils.isEmpty(detailModel.getRealName())) {
                errorMsg += "姓名不能为空!";
            }

            if (StringUtils.isEmpty(idCard)) {
                errorMsg += "身份证号不能为空!";
            }else if(!pattern.matcher(idCard).find()){
                errorMsg += "身份证号格式有误!";
            }else  if (userClient.isIdCardExists(idCard)) {
                return failed("身份证号已存在!");
            }

            if (StringUtils.isEmpty(detailModel.getEmail())) {
                errorMsg += "邮箱不能为空!";
            }else if(userClient.isEmailExists(detailModel.getEmail())){
                return failed("邮箱已存在!");
            }

            if (StringUtils.isEmpty(detailModel.getTelephone())) {
                errorMsg += "电话号码不能为空!";
            }else if(userClient.isTelephoneExists(detailModel.getTelephone())){
                return failed("电话号码已存在!");
            }

            //设置默认密码为身份证后六位
            if(!org.springframework.util.StringUtils.isEmpty(detailModel.getIdCardNo())&&detailModel.getIdCardNo().length()>7){
                String  defaultPassword=detailModel.getIdCardNo().substring(detailModel.getIdCardNo().length()-6,detailModel.getIdCardNo().length());
                detailModel.setPassword(defaultPassword);
            }else{
                errorMsg += "身份证号格式有误!";
            }
            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            MUser mUser = convertToMUser(detailModel);
            mUser = userClient.createUser(objectMapper.writeValueAsString(mUser));
            if (mUser == null) {
                return failed("保存失败!");
            }
            detailModel = convertToUserDetailModel(mUser);
            return success(detailModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/updateUser", method = RequestMethod.PUT)
    @ApiOperation(value = "修改用户", notes = "重新绑定用户信息")
    public Envelop updateUser(
            @ApiParam(name = "user_json_data", value = "", defaultValue = "")
            @RequestParam(value = "user_json_data") String userJsonData) {
        try {
            UserDetailModel detailModel = toEntity(userJsonData, UserDetailModel.class);
            String errorMsg = "";
            String idCard = detailModel.getIdCardNo();
            if (StringUtils.isEmpty(detailModel.getLoginCode())) {
                errorMsg += "账户不能为空";
            }else  if (userClient.isUserNameExists(detailModel.getLoginCode())) {
                return failed("账户已存在!");
            }
            if (StringUtils.isEmpty(detailModel.getRealName())) {
                errorMsg += "姓名不能为空!";
            }
            if (StringUtils.isEmpty(idCard)) {
                errorMsg += "身份证号不能为空!";
            }else if(!pattern.matcher(idCard).find()){
                errorMsg += "身份证号格式有误!";
            }

            if (StringUtils.isEmpty(detailModel.getEmail())) {
                errorMsg += "邮箱不能为空!";
            }

            if (StringUtils.isEmpty(detailModel.getTelephone())) {
                errorMsg += "电话号码不能为空!";
            }

            //设置默认密码为身份证后六位
            if(!org.springframework.util.StringUtils.isEmpty(detailModel.getIdCardNo())&&detailModel.getIdCardNo().length()>7){
                String  defaultPassword=detailModel.getIdCardNo().substring(detailModel.getIdCardNo().length()-6,detailModel.getIdCardNo().length());
                detailModel.setPassword(defaultPassword);
            }else{
                errorMsg += "身份证号格式有误!";
            }
            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            MUser mUser = userClient.getUser(detailModel.getId());
            mUser = convertToMUser(detailModel);
            mUser.setRole(null);
            mUser = userClient.updateUser(objectMapper.writeValueAsString(mUser));
            if (mUser != null) {
                detailModel = convertToUserDetailModel(mUser);
                return success(detailModel);
            }

            return failed("保存失败!");
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    public MUser convertToMUser(UserDetailModel detailModel) {
        if (detailModel == null) {
            return null;
        }
        MUser mUser = convertToModel(detailModel, MUser.class);
        mUser.setCreateDate(StringToDate(detailModel.getCreateDate(), AgAdminConstants.DateTimeFormat));
        mUser.setLastLoginTime(StringToDate(detailModel.getLastLoginTime(), AgAdminConstants.DateTimeFormat));

        return mUser;
    }

    /**
     * 将 MUser 转为 UserDetailModel
     *
     * @param mUser
     * @return UserDetailModel
     */
    public UserDetailModel convertToUserDetailModel(MUser mUser) {
        if (mUser == null) {
            return null;
        }
        UserDetailModel detailModel = convertToModel(mUser, UserDetailModel.class);
        detailModel.setCreateDate(DateToString(mUser.getCreateDate(), AgAdminConstants.DateTimeFormat));
        detailModel.setLastLoginTime(DateToString(mUser.getLastLoginTime(), AgAdminConstants.DateTimeFormat));
        //获取婚姻状态代码
        String marryCode = mUser.getMartialStatus();
        MConventionalDict dict = null;
        if (StringUtils.isNotEmpty(marryCode)) {
            dict = conventionalDictClient.getMartialStatus(marryCode);
            detailModel.setMartialStatusName(dict == null ? "" : dict.getValue());
        }
        //获取用户类型
        String userType = mUser.getUserType();
//        if (StringUtils.isNotEmpty(userType)) {
//            dict = conventionalDictClient.getUserType(userType);
//            detailModel.setUserTypeName(dict == null ? "" : dict.getValue());
//        }
        //获取用户标准来源
        String userSource = mUser.getSource();
//        if (StringUtils.isNotEmpty(userSource)) {
//            dict = conventionalDictClient.getUserSource(userSource);
//            detailModel.setSourceName(dict == null ? "" : dict.getValue());
//        }
        //从用户-角色组关系表获取用户所属角色组ids
        detailModel.setRole("");
        Collection<MRoleUser> mRoleUsers = roleUserClient.searchRoleUserNoPaging("userId=" + mUser.getId());
        if (mRoleUsers.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            for (MRoleUser m : mRoleUsers) {
                buffer.append(m.getRoleId());
                buffer.append(",");
            }
            detailModel.setRole(buffer.substring(0, buffer.length() - 1));
        }
       // 获取秘钥信息
        MKey userSecurity = securityClient.getUserKey(mUser.getId());
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
