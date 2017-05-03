package com.yihu.ehr.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.app.AppFeatureModel;
import com.yihu.ehr.agModel.user.UserDetailModel;
import com.yihu.ehr.agModel.user.UsersModel;
import com.yihu.ehr.apps.service.AppFeatureClient;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.fileresource.service.FileResourceClient;
import com.yihu.ehr.geography.service.AddressClient;
import com.yihu.ehr.model.app.MAppFeature;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.geography.MGeography;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.model.user.MRoleFeatureRelation;
import com.yihu.ehr.model.user.MRoleUser;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.organization.service.OrganizationClient;
import com.yihu.ehr.security.service.SecurityClient;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.users.service.RoleFeatureRelationClient;
import com.yihu.ehr.users.service.RoleUserClient;
import com.yihu.ehr.users.service.UserClient;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.util.datetime.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by AndyCai on 2016/1/21.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "user", description = "用户管理", tags = {"用户管理"})
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
    @Autowired
    private RoleFeatureRelationClient roleFeatureRelationClient;
    @Autowired
    AppFeatureClient appFeatureClient;
    @Autowired
    private RoleUserClient roleUserClient;
    @Autowired
    private FileResourceClient fileResourceClient;

    private String resetFilter(String filters) {

        if(!StringUtils.isEmpty(filters)){
            String orgName, orgCodes = "";
            boolean searchOrg = false;
            String[] values;
            String[] filterArr = filters.split(";");
            for(int i=0; i< filterArr.length; i++){
                String filter = filterArr[i];
                if(filter.startsWith("organization")){
                    if(filter.contains("<>") || filter.contains(">=") || filter.contains("<="))
                        values = filter.substring(14).split(" ");
                    else
                        values = filter.substring(13).split(" ");

                    orgName =  values[0];
                    ResponseEntity<List<MOrganization>> rs = orgClient.searchOrgs("", "fullName?" + orgName, "", 1000, 1);
                    if(rs.getStatusCode().value() <=200){
                        List<MOrganization> orgList = rs.getBody();
                        if(orgList.size()>0){
                            for(MOrganization org : orgList){
                                orgCodes += "," + org.getOrgCode();
                            }
                            filterArr[i] = "organization=" + orgCodes.substring(1);
                        }else
                            filterArr[i] = "organization=-1";

                        if(values.length>1)
                            filterArr[i] = filterArr[i] + " " + values[1];
                        searchOrg = true;
                        break;
                    }
                    else
                        throw new IllegalAccessError("解析错误");
                }
            }

            if(searchOrg){
                filters = "";
                for(String filter : filterArr){
                    filters += filter + ";";
                }
            }
        }
        return filters;
    }

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

        filters = resetFilter(filters);
        ResponseEntity<List<MUser>> responseEntity = userClient.searchUsers(fields, filters, sorts, size, page);
        List<MUser> mUsers = responseEntity.getBody();
        List<UsersModel> usersModels = new ArrayList<>();
        for (MUser mUser : mUsers) {
            UsersModel usersModel = convertToModel(mUser, UsersModel.class);
            //usersModel.setLastLoginTime(DateToString( mUser.getLastLoginTime(),AgAdminConstants.DateTimeFormat));
            usersModel.setLastLoginTime(mUser.getLastLoginTime() == null?"":DateTimeUtil.simpleDateTimeFormat(mUser.getLastLoginTime()));
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
            //获取用户来源信息
            if(StringUtils.isNotEmpty(mUser.getSource())){
                MConventionalDict dict = conventionalDictClient.getUserSource(mUser.getSource());
                usersModel.setSourceName(dict == null ? "" : dict.getValue());
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

    @RequestMapping(value = "/users/admin/{user_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户", notes = "根据用户id删除用户")
    public Envelop deleteUser(
            @ApiParam(name = "user_id", value = "用户编号", defaultValue = "")
            @PathVariable(value = "user_id") String userId) {

//        MKey userSecurity = securityClient.getUserSecurityByUserId(userId);
//        if (userSecurity != null) {
//            String userKeyId = securityClient.getUserKeyByUserId(userId);
//            securityClient.deleteSecurity(userSecurity.getId());
//            securityClient.deleteUserKey(userKeyId);
//        }
        try {
            // 删除用户秘钥信息
            boolean _res = securityClient.deleteKeyByUserId(userId);

            if (!_res) {
                return failed("删除失败!");
            }

            boolean result = userClient.deleteUser(userId);
            if (!result) {
                return failed("删除失败!");
            }
            //删除用户-用户角色组关系
            //Todo 根据用户id删除的接口
            Collection<MRoleUser> mRoleUsers = roleUserClient.searchRoleUserNoPaging("userId=" + userId);
            if(mRoleUsers != null){
                StringBuffer buffer = new StringBuffer();
                for(MRoleUser m : mRoleUsers){
                    buffer.append(m.getRoleId());
                    buffer.append(",");
                }
                String roleIds = buffer.substring(0,buffer.length()-1);
                boolean bo = roleUserClient.batchDeleteRoleUserRelation(userId,roleIds);
                if(!bo){
                    return failed("删除失败！");
                }
            }
            try{
             fileResourceClient.filesDelete(userId);
            }catch (Exception e){
                return success("数据删除成功！头像图片删除失败！");
            }
            return success(null);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/user", method = RequestMethod.POST)
    @ApiOperation(value = "创建用户", notes = "重新绑定用户信息")
    public Envelop createUser(
            @ApiParam(name = "user_json_data", value = "", defaultValue = "")
            @RequestParam(value = "user_json_data") String userJsonData) {

        try {

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
            String roles = detailModel.getRole();
            if (StringUtils.isEmpty(roles)) {
                errorMsg += "用户角色不能为空!";
            }
            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            if (userClient.isUserNameExists(detailModel.getLoginCode())) {
                return failed("账户已存在!");
            }
            if (userClient.isIdCardExists(detailModel.getIdCardNo())) {
                return failed("身份证号已存在!");
            }
            if (userClient.isEmailExists(detailModel.getEmail())) {
                return failed("邮箱已存在!");
            }
            detailModel.setPassword(AgAdminConstants.DefaultPassword);
            MUser mUser = convertToMUser(detailModel);
            mUser = userClient.createUser(objectMapper.writeValueAsString(mUser));
            if (mUser == null) {
                return failed("保存失败!");
            }
            //新增时先新增用户再保存所属角色组-人员关系表，用户新增失败（新增失败）、角色组关系表新增失败（删除新增用户-提示新增失败），
            boolean bo = roleUserClient.batchCreateRolUsersRelation(mUser.getId(), roles);
            if(!bo){
                userClient.deleteUser(mUser.getId());
                return failed("保存失败!");
            }
            detailModel = convertToUserDetailModel(mUser);
            return success(detailModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/user", method = RequestMethod.PUT)
    @ApiOperation(value = "修改用户", notes = "重新绑定用户信息")
    public Envelop updateUser(
            @ApiParam(name = "user_json_data", value = "", defaultValue = "")
            @RequestParam(value = "user_json_data") String userJsonData) {
        try {
            UserDetailModel detailModel = toEntity(userJsonData, UserDetailModel.class);
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
            String roles = detailModel.getRole();
            if (StringUtils.isEmpty(roles)) {
                errorMsg += "用户角色不能为空!";
            }
            if (StringUtils.isNotEmpty(errorMsg)) {
                return failed(errorMsg);
            }
            MUser mUser = userClient.getUser(detailModel.getId());
            if (!mUser.getLoginCode().equals(detailModel.getLoginCode())
                    && userClient.isUserNameExists(detailModel.getLoginCode())) {
                return failed("账户已存在!");
            }

            if (mUser.getIdCardNo()!=null&&!mUser.getIdCardNo().equals(detailModel.getIdCardNo())
                    && userClient.isIdCardExists(detailModel.getIdCardNo())) {
                return failed("身份证号已存在!");
            }

            if (!mUser.getEmail().equals(detailModel.getEmail())
                    && userClient.isEmailExists(detailModel.getEmail())) {
                return failed("邮箱已存在!");
            }
            //-----------
//            mUser = convertToMUser(detailModel);
//            mUser = userClient.updateUser(objectMapper.writeValueAsString(mUser));
//            if (mUser == null) {
//                return failed("保存失败!");
//            }
//            detailModel = convertToUserDetailModel(mUser);
//            return success(detailModel);

            //修改时先修改所属角色组再修改用户，修改角色组失败（修改失败）、修改用户失败 （回显角色组）
            Collection<MRoleUser> mRoleUsers = roleUserClient.searchRoleUserNoPaging("userId=" + mUser.getId());
            boolean bo = roleUserClient.batchUpdateRoleUsersRelation(mUser.getId(),detailModel.getRole());
            if(!bo){
                return failed("保存失败！");
            }
            mUser = convertToMUser(detailModel);
            mUser = userClient.updateUser(objectMapper.writeValueAsString(mUser));
            if (mUser != null) {
                detailModel = convertToUserDetailModel(mUser);
                return success(detailModel);
            }
            StringBuffer buffer = new StringBuffer();
            for(MRoleUser m : mRoleUsers){
                buffer.append(m.getRoleId());
                buffer.append(",");
            }
            if(buffer.length()>0){
                String oldRoleIds = buffer.substring(0, buffer.length() - 1);
                roleUserClient.batchDeleteRoleUserRelation(mUser.getId(),oldRoleIds);
            }
            return failed("保存失败!");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "users/admin/{user_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取用户信息", notes = "包括地址信息等")
    public Envelop getUser(
            @ApiParam(name = "user_id", value = "", defaultValue = "")
            @PathVariable(value = "user_id") String userId) {

        try {
            MUser mUser = userClient.getUser(userId);
            if (mUser == null) {
                return failed("用户信息获取失败!");
            }
//            if (!StringUtils.isEmpty(mUser.getImgRemotePath())) {
////                Map<String, String> map = toEntity(mUser.getImgRemotePath(), Map.class);
//                String imagePath[] = mUser.getImgRemotePath().split(":");
//                String localPath = userClient.downloadPicture(imagePath[0], imagePath[1]);
//                mUser.setImgLocalPath(localPath);
//            }
            mUser.setBirthday(mUser.getBirthday().substring(0,10));
            UserDetailModel detailModel = convertToUserDetailModel(mUser);

            return success(detailModel);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }


    @RequestMapping(value = "/users/admin/{user_id}", method = RequestMethod.PUT)
    @ApiOperation(value = "改变用户状态", notes = "根据用户状态改变当前用户状态")
    public boolean activityUser(
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "activity", value = "激活状态", defaultValue = "")
            @RequestParam(value = "activity") boolean activity) {
        try {
            return userClient.activityUser(userId, activity);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }


    @RequestMapping(value = "users/password/{user_id}", method = RequestMethod.PUT)
    @ApiOperation(value = "重设密码", notes = "用户忘记密码管理员帮助重新还原密码，初始密码123456")
    public boolean resetPass(
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId) {
        try {
            return userClient.resetPass(userId);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }


    @RequestMapping(value = "/users/binding/{user_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "取消关联绑定", notes = "取消相关信息绑定")
    public boolean unBinding(
            @ApiParam(name = "user_id", value = "", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "type", value = "", defaultValue = "")
            @RequestParam(value = "type") String type) {
        try {
            return userClient.unBinding(userId, type);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 重新分配秘钥
     *
     * @param userName 账号
     * @return map  key{publicKey:公钥；validTime：有效时间; startTime：生效时间}
     */
    @RequestMapping(value = "/users/key/{login_code}", method = RequestMethod.PUT)
    @ApiOperation(value = "重新分配密钥", notes = "重新分配密钥")
    public Map<String, String> distributeKey(
            @ApiParam(name = "login_code", value = "登录帐号", defaultValue = "")
            @PathVariable(value = "login_code") String userName) {
        try {
            MUser mUser = userClient.getUserByUserName(userName);
            if (mUser == null) {
                return null;
            }
            return userClient.distributeKey(mUser.getId());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }


    /**
     * 根据登陆用户名及密码验证用户.
     *
     * @param userName
     * @param psw
     */
    @RequestMapping(value = "/users/verification/{login_code}", method = RequestMethod.GET)
    @ApiOperation(value = "根据登陆用户名及密码验证用户", notes = "根据登陆用户名及密码验证用户")
    public Envelop loginVerification(
            @ApiParam(name = "login_code", value = "登录账号", defaultValue = "")
            @PathVariable(value = "login_code") String userName,
            @ApiParam(name = "psw", value = "密码", defaultValue = "")
            @RequestParam(value = "psw") String psw) {
        try {
            MUser mUser = userClient.getUserByNameAndPassword(userName, psw);
            if (mUser == null) {
                return failed("用户信息获取失败!");
            }
            UserDetailModel detailModel = convertToUserDetailModel(mUser);

            return success(detailModel);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    /**
     * 根据loginCode 获取user
     *
     * @param userName
     * @return
     */
    @RequestMapping(value = "/users/{login_code}", method = RequestMethod.GET)
    @ApiOperation(value = "根据登录账号获取当前用户", notes = "根据登陆用户名及密码验证用户")
    public Envelop getUserByLoginCode(
            @ApiParam(name = "login_code", value = "登录账号", defaultValue = "")
            @PathVariable(value = "login_code") String userName) {
        try {
            MUser mUser = userClient.getUserByUserName(userName);
            if (mUser == null) {
                return failed("用户信息获取失败!");
            }

            UserDetailModel detailModel = convertToUserDetailModel(mUser);

            return success(detailModel);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }
    }

    @RequestMapping(value = "/users/existence", method = RequestMethod.GET)
    @ApiOperation(value = "用户属性唯一性验证", notes = "用户属性唯一性验证（用户名、省份证号、邮箱）")
    public Envelop existence(
            @ApiParam(name = "existenceType",value = "", defaultValue = "")
            @RequestParam(value = "existenceType") String existenceType,
            @ApiParam(name = "existenceNm",value = "", defaultValue = "")
            @RequestParam(value = "existenceNm") String existenceNm) {
        try {
            Envelop envelop = new Envelop();
            boolean bo = false;
            switch (existenceType) {
                case "login_code":
                    bo = userClient.isUserNameExists(existenceNm);
                    break;
                case "id_card_no":
                    bo = userClient.isIdCardExists(existenceNm);
                    break;
                case "email":
                    bo = userClient.isEmailExists(existenceNm);
                    break;
            }
            envelop.setSuccessFlg(bo);

            return envelop;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return failedSystem();
        }

    }


    @RequestMapping(value = "/users/changePassWord", method = RequestMethod.PUT)
    @ApiOperation(value = "修改用户密码", notes = "修改用户密码")
    public Envelop changePassWord(
            @ApiParam(name = "user_id",value = "password", defaultValue = "")
            @RequestParam(value = "user_id") String userId,
            @ApiParam(name = "password",value = "密码", defaultValue = "")
            @RequestParam(value = "password") String password) {

        Envelop envelop = new Envelop();
        try {
            boolean bo = userClient.changePassWord(userId,password);
            if (bo){
                envelop.setSuccessFlg(true);
            }else {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("密码修改失败");
            }
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("密码修改失败");
        }
        return envelop;
    }

    /**
     * 将 MUser 转为 UserDetailModel
     *
     * @param mUser
     * @return UserDetailModel
     */
    public UserDetailModel convertToUserDetailModel(MUser mUser) {
        if(mUser==null)
        {
            return null;
        }
        UserDetailModel detailModel = convertToModel(mUser, UserDetailModel.class);

        detailModel.setCreateDate(DateToString(mUser.getCreateDate(),AgAdminConstants.DateTimeFormat));
        detailModel.setLastLoginTime(DateToString(mUser.getLastLoginTime(),AgAdminConstants.DateTimeFormat));

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
        //获取用户标准来源
        String userSource = mUser.getSource();
        if (StringUtils.isNotEmpty(userSource)){
            dict = conventionalDictClient.getUserSource(userSource);
            detailModel.setSourceName(dict == null ? "":dict.getValue());
        }
        //从用户-角色组关系表获取用户所属角色组ids
        detailModel.setRole("");
        Collection<MRoleUser> mRoleUsers = roleUserClient.searchRoleUserNoPaging("userId=" + mUser.getId());
        if(mRoleUsers.size()>0){
            StringBuffer buffer = new StringBuffer();
            for(MRoleUser m : mRoleUsers){
                buffer.append(m.getRoleId());
                buffer.append(",");
            }
            detailModel.setRole(buffer.substring(0,buffer.length()-1));
        }
        //获取归属机构
        String orgCode = mUser.getOrganization();
        if(StringUtils.isNotEmpty(orgCode)) {
            MOrganization orgModel = orgClient.getOrg(orgCode);
            detailModel.setOrganizationName(orgModel == null ? "" : orgModel.getFullName());
            if(orgModel!=null&&StringUtils.isNotEmpty(orgModel.getLocation())) {
                MGeography mGeography = addressClient.getAddressById(orgModel.getLocation());
                detailModel.setProvinceName(mGeography==null?"":mGeography.getProvince());
                detailModel.setCityName(mGeography==null?"":mGeography.getCity());
            }
        }
        //获取秘钥信息
        MKey userSecurity = securityClient.getUserKey(mUser.getId(),true);
        if (userSecurity != null) {
            detailModel.setPublicKey(userSecurity.getPublicKey());
            String validTime = DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT)
                    + "~" + DateUtil.toString(userSecurity.getExpiryDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT);
            detailModel.setValidTime(validTime);
            detailModel.setStartTime(DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT));
        }
        return detailModel;
    }

    public MUser convertToMUser(UserDetailModel detailModel)
    {
        if(detailModel==null)
        {
            return null;
        }
        MUser mUser = convertToModel(detailModel,MUser.class);
        mUser.setCreateDate(StringToDate(detailModel.getCreateDate(),AgAdminConstants.DateTimeFormat));
        mUser.setLastLoginTime(StringToDate(detailModel.getLastLoginTime(),AgAdminConstants.DateTimeFormat));

        return mUser;
    }

    //查看用户权限列表
    @RequestMapping(value = "/users/user_features",method = RequestMethod.GET)
    @ApiOperation(value = "查看用户权限", notes = "查看用户权限")
    public Envelop getUserFeatureList(
            @ApiParam(name = "roles_ids",value = "用户所属角色组ids，多个以逗号隔开")
            @RequestParam(value = "roles_ids") String roleIds){
        Collection<MRoleFeatureRelation> relations = roleFeatureRelationClient.searchRoleFeatureNoPaging("roleId=" + roleIds);
        String featureIds = "";
        for(MRoleFeatureRelation relation : relations){
            featureIds += relation.getFeatureId()+",";
        }
        if(StringUtils.isEmpty(featureIds)){
            return success(null);
        }
        featureIds = featureIds.substring(0,featureIds.length()-1);
        Collection<MAppFeature> mAppFeatures = appFeatureClient.getAppFeatureNoPage("id=" + featureIds);
        Envelop envelop = new Envelop();
        List<AppFeatureModel> appFeatureModels = new ArrayList<>();
        for(MAppFeature mAppFeature: mAppFeatures ){
            AppFeatureModel appFeatureModel = convertToModel(mAppFeature, AppFeatureModel.class);
            appFeatureModels.add(appFeatureModel);
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(appFeatureModels);
        return envelop;
    }

}
