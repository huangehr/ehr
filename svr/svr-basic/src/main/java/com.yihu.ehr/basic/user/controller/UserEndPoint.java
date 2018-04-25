package com.yihu.ehr.basic.user.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.basic.dict.service.SystemDictEntryService;
import com.yihu.ehr.basic.org.model.OrgMemberRelation;
import com.yihu.ehr.basic.patient.service.DemographicService;
import com.yihu.ehr.basic.security.service.UserSecurityService;
import com.yihu.ehr.basic.user.entity.RoleUser;
import com.yihu.ehr.basic.user.entity.Roles;
import com.yihu.ehr.basic.user.service.RoleUserService;
import com.yihu.ehr.basic.user.service.RolesService;
import com.yihu.ehr.basic.user.entity.Roles;
import com.yihu.ehr.basic.user.service.RoleUserService;
import com.yihu.ehr.basic.user.service.RolesService;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.basic.user.entity.Doctors;
import com.yihu.ehr.basic.user.entity.User;
import com.yihu.ehr.basic.user.service.DoctorService;
import com.yihu.ehr.basic.user.service.UserService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.dict.SystemDictEntry;
import com.yihu.ehr.entity.patient.DemographicInfo;
import com.yihu.ehr.entity.security.UserKey;
import com.yihu.ehr.entity.security.UserSecurity;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.model.user.MH5Handshake;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.id.BizObject;
import com.yihu.ehr.util.log.LogService;
import com.yihu.ehr.util.phonics.PinyinUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;

/**
 * @author zlf
 * @version 1.0
 * @created 2015.08.10 17:57
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "user", description = "用户管理", tags = {"用户管理"})
public class UserEndPoint extends EnvelopRestEndPoint {

    @Value("${default.password}")
    private String default_password = "12345678";
    @Value("${h5.secret}")
    private String secret;
    @Value("${h5.appId}")
    private String appId;
    @Value("${jksr-app.orgcode}")
    private String orgcode;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleUserService roleUserService;
    @Autowired
    private RolesService rolesService;
    @Autowired
    private UserSecurityService userSecurityService;
    @Autowired
    private SystemDictEntryService dictEntryService;
    @Autowired
    private FastDFSUtil fastDFSUtil;
    @Autowired
    private DoctorService doctorService;
    @Autowired
    private DemographicService demographicService;

    @RequestMapping(value = ServiceApi.Users.Users, method = RequestMethod.GET)
    @ApiOperation(value = "获取用户列表", notes = "根据查询条件获取用户列表在前端表格展示")
    public List<MUser> searchUsers(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "orgCode", value = "机构编码", defaultValue = "")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            HttpServletRequest request,
            HttpServletResponse response) throws ParseException {
        List<User> userList = new ArrayList<>();
        if (!StringUtils.isEmpty(orgCode)) {
            String[] orgCodes = orgCode.split(",");
            String realName = "";
            String userType = "";
            if (!StringUtils.isEmpty(filters)) {
                boolean nameFlag = filters.contains("realName?");
                boolean typeFlag = filters.contains("userType=");
                if (nameFlag && typeFlag) {
                    realName = filters.substring(filters.indexOf("?") + 1, filters.indexOf(";"));
                    userType = filters.substring(filters.lastIndexOf("=") + 1, filters.lastIndexOf(";"));
                } else if (nameFlag) {
                    realName = filters.substring(filters.indexOf("?") + 1, filters.indexOf(";"));
                } else if (typeFlag) {
                    userType = filters.substring(filters.lastIndexOf("=") + 1, filters.lastIndexOf(";"));
                }
            }
            userList = userService.searchUsers(orgCodes, realName, userType, page, size);
            Long totalCount = userService.searchUsersCount(orgCodes, realName, userType);
            pagedResponse(request, response, totalCount, page, size);

        } else {
            userList = userService.search(fields, filters, sorts, page, size);
            pagedResponse(request, response, userService.getCount(filters), page, size);
        }

        return (List<MUser>) convertToModels(userList, new ArrayList<MUser>(userList.size()), MUser.class, fields);
    }

    @RequestMapping(value = ServiceApi.Users.Users, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建用户", notes = "重新绑定用户信息")
    public MUser createUser(
            @ApiParam(name = "user_json_data", value = "", defaultValue = "")
            @RequestBody String userJsonData) throws Exception {
        User user = toEntity(userJsonData, User.class);
        user.setId(getObjectId(BizObject.User));
        user.setCreateDate(new Date());
        if (!StringUtils.isEmpty(user.getPassword())) {
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        } else {
            user.setPassword(DigestUtils.md5Hex(default_password));
        }
        String userType = user.getUserType();
        SystemDictEntry dict = dictEntryService.getDictEntry(15, userType);
        if (dict != null) {
            user.setDType(userType);
        }
        user.setActivated(true);
        user = userService.saveUser(user);
        return convertToModel(user, MUser.class, null);
    }

    @RequestMapping(value = ServiceApi.Users.Users, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改用户", notes = "重新绑定用户信息")
    public MUser updateUser(
            @ApiParam(name = "user_json_data", value = "", defaultValue = "")
            @RequestBody String userJsonData) throws Exception {
        User user = toEntity(userJsonData, User.class);
        String userType = user.getUserType();
        SystemDictEntry dict = dictEntryService.getDictEntry(15, userType);
        if (dict != null) {
            user.setDType(userType);
        }
        userService.saveUser(user);
        //同时修改医生表及用户表信息
        Doctors doctors = doctorService.getByIdCardNo(user.getIdCardNo());
        if (!StringUtils.isEmpty(doctors)) {
            doctors.setName(user.getRealName());
            doctors.setPyCode(PinyinUtil.getPinYinHeadChar(user.getRealName(), false));
            doctors.setSex(user.getGender());
            doctors.setPhone(user.getTelephone());
            doctorService.save(doctors);
        }
        DemographicInfo demographicInfo = demographicService.getDemographicInfoByIdCardNo(user.getIdCardNo());
        if (!StringUtils.isEmpty(demographicInfo)) {
            demographicInfo.setName(user.getRealName());
            demographicInfo.setTelephoneNo("{\"联系电话\":\"" + user.getTelephone() + "\"}");
            demographicInfo.setGender(user.getGender());
            demographicInfo.setMartialStatus(user.getMartialStatus());
            demographicInfo.setBirthday(DateUtil.strToDate(user.getBirthday()));
            demographicService.save(demographicInfo);
        }
        return convertToModel(user, MUser.class);
    }

    @RequestMapping(value = ServiceApi.Users.UserAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取用户信息")
    public MUser getUser(
            @ApiParam(name = "user_id", value = "", defaultValue = "")
            @PathVariable(value = "user_id") String userId) {
        User user = userService.getUser(userId);
        MUser userModel = convertToModel(user, MUser.class);
        return userModel;
    }

    @RequestMapping(value = ServiceApi.Users.UserAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户", notes = "根据id删除用户")
    public boolean deleteUser(
            @ApiParam(name = "user_id", value = "用户编号", defaultValue = "")
            @PathVariable(value = "user_id") String userId) throws Exception {
        userService.deleteUser(userId);
        return true;
    }

    @RequestMapping(value = ServiceApi.Users.UserAdmin, method = RequestMethod.PUT)
    @ApiOperation(value = "改变用户状态", notes = "根据id更新用户")
    public boolean activityUser(
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "activity", value = "激活状态", defaultValue = "")
            @RequestParam(value = "activity") boolean activity) throws Exception {
        userService.activityUser(userId, activity);
        return true;
    }

    @RequestMapping(value = ServiceApi.Users.User, method = RequestMethod.GET)
    @ApiOperation(value = "根据登录账号获取当前用户", notes = "根据登陆用户名及密码验证用户")
    public MUser getUserByLoginCode(
            @ApiParam(name = "user_name", value = "登录账号", defaultValue = "")
            @PathVariable(value = "user_name") String userName) {
        //User user = userManager.getUserByUserName(userName);
        //TODO 可根据帐户，手机号，身份证号登陆接口新增
        List<User> users = userService.getUserForLogin(userName);
        if (users != null) {
            if (users.size() == 1) {
                return convertToModel(users.get(0), MUser.class);
            }
        }
        return null;
    }

    @RequestMapping(value = ServiceApi.Users.UserAdminPassword, method = RequestMethod.PUT)
    @ApiOperation(value = "重设密码", notes = "用户忘记密码管理员帮助重新还原密码，初始密码12345678")
    public boolean resetPass(
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId) throws Exception {
        userService.resetPass(userId);
        return true;
    }

    @RequestMapping(value = ServiceApi.Users.UserAdminPasswordReset, method = RequestMethod.PUT)
    @ApiOperation(value = "修改密码", notes = "根基传入的用户id和新的密码修改用户的密码")
    public boolean changePassWord(
            @ApiParam(name = "user_id", value = "user_id", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "password", value = "密码", defaultValue = "")
            @RequestParam(value = "password") String password) throws Exception {
        String hashPassWord = DigestUtils.md5Hex(password);
        userService.changePassWord(userId, hashPassWord);
        return true;
    }

    @RequestMapping(value = ServiceApi.Users.UserAdminKey, method = RequestMethod.PUT)
    @ApiOperation(value = "重新分配密钥", notes = "重新分配密钥")
    public Map<String, String> distributeKey(
            @ApiParam(name = "user_id", value = "登录帐号", defaultValue = "")
            @PathVariable(value = "user_id") String userId) throws Exception {
        User user = userService.getUser(userId);
        if (null == user) {
            return null;
        }
        UserSecurity userSecurity = userSecurityService.getKeyByUserId(userId, false);
        Map<String, String> keyMap = new HashMap<>();
        if (userSecurity != null) {
            // 删除原有的公私钥重新分配
            List<UserKey> userKeyList = userSecurityService.getKeyMapByUserId(userId);
            userSecurityService.deleteKey(userKeyList);
        }
        userSecurity = userSecurityService.createKeyByUserId(userId);
        String validTime = DateFormatUtils.format(userSecurity.getFromDate(), "yyyy-MM-dd")
                + "~" + DateFormatUtils.format(userSecurity.getExpiryDate(), "yyyy-MM-dd");
        keyMap.put("publicKey", userSecurity.getPublicKey());
        keyMap.put("validTime", validTime);
        keyMap.put("startTime", DateFormatUtils.format(userSecurity.getFromDate(), "yyyy-MM-dd"));
        return keyMap;
    }

    @RequestMapping(value = ServiceApi.Users.UserAdminKey, method = RequestMethod.GET)
    @ApiOperation(value = "查询用户公钥", notes = "查询用户公钥")
    public Envelop getKey(
            @ApiParam(name = "user_id", value = "登录帐号", defaultValue = "")
            @PathVariable(value = "user_id") String userId) throws Exception {
        Envelop envelop = new Envelop();
        User user = userService.getUser(userId);
        if (null == user) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("用户不存在");
            return envelop;
        }
        UserSecurity userSecurity = userSecurityService.getKeyByUserId(userId, false);
        if (null == userSecurity) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("请先获取公钥");
            return envelop;
        }
        Map<String, String> keyMap = new HashMap<>();
        String validTime = DateFormatUtils.format(userSecurity.getFromDate(), "yyyy-MM-dd")
                + "~" + DateFormatUtils.format(userSecurity.getExpiryDate(), "yyyy-MM-dd");
        keyMap.put("publicKey", userSecurity.getPublicKey());
        keyMap.put("validTime", validTime);
        keyMap.put("startTime", DateFormatUtils.format(userSecurity.getFromDate(), "yyyy-MM-dd"));
        envelop.setSuccessFlg(true);
        envelop.setObj(keyMap);
        return envelop;
    }

    /**
     * 根据登陆用户名及密码验证用户.
     *
     * @param userName
     * @param password
     */
    @RequestMapping(value = ServiceApi.Users.UserVerification, method = RequestMethod.GET)
    @ApiOperation(value = "根据登陆用户名及密码验证用户", notes = "根据登陆用户名及密码验证用户")
    public MUser getUserByNameAndPassword(
            @ApiParam(name = "user_name", value = "登录账号", defaultValue = "")
            @RequestParam(value = "user_name") String userName,
            @ApiParam(name = "password", value = "密码", defaultValue = "")
            @RequestParam(value = "password") String password) {
        User user = userService.loginVerification(userName, password);
        return convertToModel(user, MUser.class);
    }

    @RequestMapping(value = ServiceApi.Users.UserExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断登陆账户（用户名）是否存在")
    public boolean isUserNameExists(
            @ApiParam(name = "user_name", value = "user_name", defaultValue = "")
            @PathVariable(value = "user_name") String userName) {
        return userService.getUserByUserName(userName) != null;
    }

    @RequestMapping(value = ServiceApi.Users.UserIdCardNoExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断用户身份证号是否存在")
    public boolean isIdCardExists(
            @ApiParam(name = "id_card_no", value = "id_card_no", defaultValue = "")
            @RequestParam(value = "id_card_no") String idCardNo) {
        return userService.getUserByIdCardNo(idCardNo) != null;
    }

//    @RequestMapping(value = ServiceApi.Users.UserAdminContact, method = RequestMethod.DELETE)
//    @ApiOperation(value = "用户联系方式解绑", notes = "将用户电话或邮件地址设置为空")
//    public boolean delteContact(
//            @ApiParam(name = "user_id", value = "", defaultValue = "")
//            @PathVariable(value = "user_id") String userId,
//            @ApiParam(name = "type", value = "", defaultValue = "")
//            @RequestParam(value = "type") String type) {
//        User user = userManager.getUser(userId);
//        if (type.equals("tel")) {
//            user.setTelephone("");
//        } else {
//            user.setEmail("");
//        }
//
//        userManager.saveUser(user);
//        return true;
//    }

    @RequestMapping(value = ServiceApi.Users.UserEmailNoExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断用户邮件是否存在")
    public boolean isEmailExists(@RequestParam(value = "email") String email) {
        return userService.getUserByEmail(email) != null;
    }

    @RequestMapping(value = ServiceApi.Users.UserTelephoneNoExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断用户电话号码是否存在")
    public boolean isTelephoneExists(@RequestParam(value = "telephone") String telephone) {
        return userService.getUserByTelephone(telephone) != null;
    }


    /**
     * 用户头像图片上传
     *
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/user/picture", method = RequestMethod.POST)
    @ApiOperation(value = "上传头像,把图片转成流的方式发送")
    public String uploadPicture(
            @ApiParam(name = "jsonData", value = "头像转化后的输入流")
            @RequestBody String jsonData) throws IOException {
        if (jsonData == null) {
            return null;
        }
        String date = URLDecoder.decode(jsonData, "UTF-8");

        String[] fileStreams = date.split(",");
        String is = URLDecoder.decode(fileStreams[0], "UTF-8").replace(" ", "+");
        byte[] in = Base64.getDecoder().decode(is);

        String pictureName = fileStreams[1].substring(0, fileStreams[1].length() - 1);
        String fileExtension = pictureName.substring(pictureName.lastIndexOf(".") + 1).toLowerCase();
        String description = null;
        if ((pictureName != null) && (pictureName.length() > 0)) {
            int dot = pictureName.lastIndexOf('.');
            if ((dot > -1) && (dot < (pictureName.length()))) {
                description = pictureName.substring(0, dot);
            }
        }
        String path = null;
        try {

            InputStream inputStream = new ByteArrayInputStream(in);
            ObjectNode objectNode = fastDFSUtil.upload(inputStream, fileExtension, description);
            String groupName = objectNode.get("groupName").toString();
            String remoteFileName = objectNode.get("remoteFileName").toString();
//            path = "{\"groupName\":" + groupName + ",\"remoteFileName\":" + remoteFileName + "}";
            path = groupName.substring(1, groupName.length() - 1) + ":" + remoteFileName.substring(1, remoteFileName.length() - 1);

        } catch (Exception e) {
            LogService.getLogger(User.class).error("人口头像图片上传失败；错误代码：" + e);
        }
        //返回文件路径
        return path;
    }


    /**
     * 用户头像图片下载
     *
     * @return
     * @throws IOException
     * @throws MyException
     */
    @RequestMapping(value = "/user/picture", method = RequestMethod.GET)
    @ApiOperation(value = "下载头像")
    public String downloadPicture(
            @ApiParam(name = "group_name", value = "分组", defaultValue = "")
            @RequestParam(value = "group_name") String groupName,
            @ApiParam(name = "remote_file_name", value = "服务器头像名称", defaultValue = "")
            @RequestParam(value = "remote_file_name") String remoteFileName) throws Exception {
        String imageStream = null;
        try {

            byte[] bytes = fastDFSUtil.download(groupName, remoteFileName);

            String fileStream = new String(Base64.getEncoder().encode(bytes));
            imageStream = URLEncoder.encode(fileStream, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            LogService.getLogger(User.class).error("人口头像图片下载失败；错误代码：" + e);
        }
        return imageStream;
    }


    @RequestMapping(value = ServiceApi.Users.UserPhoneExistence, method = RequestMethod.POST)
    @ApiOperation("获取已存在电话号码")
    public List idExistence(
            @ApiParam(name = "phones", value = "phones", defaultValue = "")
            @RequestBody String phones) throws Exception {

        List existPhones = userService.idExist(toEntity(phones, String[].class));
        return existPhones;
    }

    @RequestMapping(value = ServiceApi.Users.UserOnePhoneExistence, method = RequestMethod.GET)
    @ApiOperation("根据过滤条件判断是否存在")
    public boolean isExistence(
            @ApiParam(name = "filters", value = "filters", defaultValue = "")
            @RequestParam(value = "filters") String filters) throws Exception {

        List<User> user = userService.search("", filters, "", 1, 1);
        return user != null && user.size() > 0;
    }

    @RequestMapping(value = ServiceApi.Users.UserEmailExistence, method = RequestMethod.POST)
    @ApiOperation("获取已存在邮箱")
    public List emailsExistence(
            @ApiParam(name = "emails", value = "emails", defaultValue = "")
            @RequestBody String emails) throws Exception {

        List existPhones = userService.emailsExistence(toEntity(emails, String[].class));
        return existPhones;
    }

    @RequestMapping(value = ServiceApi.Users.UseridCardNoExistence, method = RequestMethod.POST)
    @ApiOperation("获取已存在身份证号码")
    public List idCardNoExistence(
            @ApiParam(name = "idCardNos", value = "idCardNos", defaultValue = "")
            @RequestBody String idCardNos) throws Exception {

        List existidCardNos = userService.idCardNosExist(toEntity(idCardNos, String[].class));
        return existidCardNos;
    }

    @RequestMapping(value = ServiceApi.Users.UserByIdCardNo, method = RequestMethod.GET)
    @ApiOperation("根据身份证号码获取用户id")
    public String getUserIdByIdCardNo(String idCardNo) {
        User user = userService.getUserByIdCardNo(idCardNo);
        return StringUtils.isEmpty(user) ? "" : user.getId();
    }

    @RequestMapping(value = ServiceApi.Users.UpdateSystemUser, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改用户", notes = "账户体系-修改用户信息")
    public MUser UpdateSystemUser(
            @ApiParam(name = "user_json_data", value = "用户信息json", defaultValue = "")
            @RequestBody String userJsonData) throws Exception {
        User user = toEntity(userJsonData, User.class);
        String userType = user.getUserType();
        if (!StringUtils.isEmpty(userType)) {
            SystemDictEntry dict = dictEntryService.getDictEntry(15, userType);
            if (dict != null) {
                user.setDType(userType);
            }
        }
        userService.saveUser(user);
        return convertToModel(user, MUser.class);
    }

    @RequestMapping(value = ServiceApi.Users.UsersByTel, method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "根据手机号码查询用户信息", notes = "根据手机号码查询用户信息")
    public MUser getUserByTel(
            @ApiParam(name = "tel", value = "手机号码", defaultValue = "")
            @RequestParam(value = "tel") String tel) throws Exception {

        User user = userService.getUserByTelephone(tel);
        if (user == null) {
            return null;
        }
        MUser mUser = convertToModel(user, MUser.class);
        return mUser;
    }


    @RequestMapping(value = ServiceApi.Users.H5Handshake, method = RequestMethod.GET)
    @ApiOperation(value = "医疗服务:提供二次握手的URL", notes = "医疗服务:提供二次握手的URL")
    public MH5Handshake getH5Handshake(
            @ApiParam(name = "thirdPartyUserId", value = "第三方登录账号ID", defaultValue = "")
            @RequestParam(name = "thirdPartyUserId") String thirdPartyUserId,
            @ApiParam(name = "ts", value = "时间戳（相对于1970-1-1的毫秒数）", defaultValue = "")
            @RequestParam(name = "ts") String ts,
            @ApiParam(name = "sign", value = "签名串", defaultValue = "")
            @RequestParam(name = "sign") String sign) {
        MH5Handshake handshake = new MH5Handshake();
        //校验合法性
        if (!validSign(thirdPartyUserId, ts, sign)) {
            handshake.setCode("-100001");
            handshake.setMessage("签名校验失败");
            return handshake;
        }

        User user = userService.getUser(thirdPartyUserId);
        if (user == null) {
            handshake.setCode("-10000");
            handshake.setMessage("账号不存在");
            return handshake;
        }

        handshake.setCode("10000");
        handshake.setMessage("Yes");
        handshake.setUserName(user.getRealName());
        handshake.setCardNo(user.getIdCardNo());
        if (!StringUtils.isEmpty(user.getGender())) {
            handshake.setSex(Integer.parseInt(user.getGender()));
        }
        handshake.setTel(user.getTelephone());
        return handshake;
    }

    /**
     * 校验sign签名的合法性
     * 算法为：thirdPartyUserId的值+ts的值+appId+secret（健康之路分配给第三方的秘钥） 字符串串起来的做SHA1签名，
     * 最后将签名值转换为小写（其中加号表示字符串拼接，不代表实际字符）
     *
     * @param thirdPartyUserId 第三方登录账号ID
     * @param ts               时间戳
     * @param sign             签名串
     * @return 如果通过返回 <code>true</code>
     */
    private boolean validSign(String thirdPartyUserId, String ts, String sign) {
        String tempStr = new StringBuilder(thirdPartyUserId)
                .append(ts)
                .append(appId)
                .append(secret).toString();
        tempStr = DigestUtils.sha1Hex(tempStr).toLowerCase();
        if (tempStr.equals(sign)) {
            return true;
        }
        return false;
    }

    // ---------------------------- 适配zuul新代码 start -----------------------------------

    @RequestMapping(value = ServiceApi.Users.Save, method = RequestMethod.POST)
    @ApiOperation("保存")
    public Envelop save(
            @ApiParam(name = "user", value = "Json串")
            @RequestParam(value = "user") String user) throws Exception {
        User user1 = objectMapper.readValue(user, User.class);
        DemographicInfo demographicInfo = objectMapper.readValue(user, DemographicInfo.class);
        String msg = this.basicVerify(user1, false);
        if (!StringUtils.isEmpty(msg)) {
            return failed(msg);
        }
        //设置默认密码为身份证后六位
        if (!StringUtils.isEmpty(user1.getIdCardNo()) && user1.getIdCardNo().length() > 9){
            String  defaultPassword = user1.getIdCardNo().substring(user1.getIdCardNo().length() - 8);
            user1.setPassword(DigestUtils.md5Hex(defaultPassword));
        } else {
            user1.setPassword(DigestUtils.md5Hex(default_password));
        }
        //更新居民demographics表中居民信息（无则创建，有则更新）
        String telephone = "{\"联系电话\":\"telephone\"}";
        telephone = telephone.replace("telephone", user1.getTelephone());
        demographicInfo.setTelephoneNo(telephone);
        demographicInfo.setName(user1.getRealName());
        //新增家庭地址信息
        String homeAddress = "";
        if (!StringUtils.isEmpty(user1.getProvinceName())) {
            homeAddress += user1.getProvinceName();
        }
        if (!StringUtils.isEmpty(user1.getCityName())) {
            homeAddress += user1.getCityName();
        }
        if (!StringUtils.isEmpty(user1.getAreaName())) {
            homeAddress += user1.getAreaName();
        }
        if (!StringUtils.isEmpty(homeAddress)) {
            demographicInfo.setHomeAddress(homeAddress);
        }
        user1.setId(getObjectId(BizObject.User));
        user1.setCreateDate(new Date());
        String userType = user1.getUserType();
        SystemDictEntry dict = dictEntryService.getDictEntry(15, userType);
        if (dict != null) {
            user1.setUserType(userType);
        }
        user1.setActivated(true);
        User user2 = userService.save(user1, demographicInfo);
        return success(user2);
    }

    @RequestMapping(value = ServiceApi.Users.Update, method = RequestMethod.POST)
    @ApiOperation("更新")
    public Envelop update(
            @ApiParam(name = "user", value = "Json串")
            @RequestParam(value = "user") String user) throws Exception {
        User detailModel = toEntity(user, User.class);
        String msg = this.basicVerify(detailModel, true);
        if (!StringUtils.isEmpty(msg)) {
            return failed(msg);
        }
        //修改时先修改所属角色组再修改用户，修改角色组失败（修改失败）、修改用户失败 （回显角色组）
        String userType = detailModel.getUserType();
        SystemDictEntry dict = dictEntryService.getDictEntry(15, userType);
        if (dict != null) {
            detailModel.setUserType(userType);
        }
        //同时修改医生表及用户表信息
        Doctors doctor = doctorService.getByIdCardNo(detailModel.getIdCardNo());
        if (doctor != null) {
            doctor.setName(detailModel.getRealName());
            doctor.setPyCode(PinyinUtil.getPinYinHeadChar(detailModel.getRealName(), false));
            doctor.setSex(detailModel.getGender());
            doctor.setPhone(detailModel.getTelephone());
        }
        DemographicInfo demographicInfo = demographicService.getDemographicInfoByIdCardNo(detailModel.getIdCardNo());
        if (demographicInfo != null) {
            demographicInfo.setName(detailModel.getRealName());
            demographicInfo.setTelephoneNo("{\"联系电话\":\"" + detailModel.getTelephone() + "\"}");
            demographicInfo.setGender(detailModel.getGender());
            demographicInfo.setMartialStatus(detailModel.getMartialStatus());
            demographicInfo.setBirthday(DateUtil.strToDate(detailModel.getBirthday()));
        } else {
            demographicInfo = objectMapper.readValue(user, DemographicInfo.class);
            demographicInfo.setName(detailModel.getRealName());
            demographicInfo.setTelephoneNo("{\"联系电话\":\"" + detailModel.getTelephone() + "\"}");
            String homeAddress = "";
            if (!StringUtils.isEmpty(detailModel.getProvinceName())) {
                homeAddress += detailModel.getProvinceName();
            }
            if (!StringUtils.isEmpty(detailModel.getCityName())) {
                homeAddress += detailModel.getCityName();
            }
            if (!StringUtils.isEmpty(detailModel.getAreaName())) {
                homeAddress += detailModel.getAreaName();
            }
            if (!StringUtils.isEmpty(homeAddress)) {
                demographicInfo.setHomeAddress(homeAddress);
            }
        }
        User user1 = userService.update(detailModel, doctor, demographicInfo);
        return success(user1);
    }

    @RequestMapping(value = ServiceApi.Users.Check, method = RequestMethod.GET)
    @ApiOperation(value = "检查字段是否重复")
    public Boolean check(
            @ApiParam(name = "field", value = "检查字段", required = true)
            @RequestParam(value = "field") String field,
            @ApiParam(name = "value", value = "检查值", required = true)
            @RequestParam(value = "value") String value) {
        if (userService.findByField(field, value).size() <= 0) {
            return false;
        }
        return true;
    }

    @RequestMapping(value = ServiceApi.Users.ChangePassword, method = RequestMethod.POST)
    @ApiOperation(value = "修改密码")
    public Boolean passwordChange(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "password", value = "password", required = true)
            @RequestParam(value = "password") String password) {
        User user = userService.getUser(userId);
        if (null == user) {
            return false;
        }
        user.setPassword(DigestUtils.md5Hex(password));
        userService.save(user);
        return true;
    }

    @RequestMapping(value = ServiceApi.Users.DistributeSecurityKey, method = RequestMethod.POST)
    @ApiOperation(value = "分配密钥", notes = "重新分配密钥")
    public Map<String, String> distributeSecurityKey (
            @ApiParam(name = "userId", value = "用户ID")
            @RequestParam(value = "userId") String userId) throws Exception{
        User user = userService.getUser(userId);
        if(null == user) {
            return null;
        }
        UserSecurity userSecurity = userSecurityService.getKeyByUserId(userId, false);
        Map<String, String> keyMap = new HashMap<>();
        if (userSecurity != null) {
            // 删除原有的公私钥重新分配
            List<UserKey> userKeyList = userSecurityService.getKeyMapByUserId(userId);
            userSecurityService.deleteKey(userKeyList);
        }
        userSecurity = userSecurityService.createKeyByUserId(userId);
        String validTime = DateFormatUtils.format(userSecurity.getFromDate(), "yyyy-MM-dd")
                + "~" + DateFormatUtils.format(userSecurity.getExpiryDate(), "yyyy-MM-dd");
        keyMap.put("publicKey", userSecurity.getPublicKey());
        keyMap.put("validTime", validTime);
        keyMap.put("startTime", DateFormatUtils.format(userSecurity.getFromDate(), "yyyy-MM-dd"));
        return keyMap;
    }

    @RequestMapping(value = ServiceApi.Users.GetSecurityKey, method = RequestMethod.GET)
    @ApiOperation(value = "查询用户公钥", notes = "查询用户公钥")
    public Map<String, String> UserId(
            @ApiParam(name = "userId", value = "登录帐号")
            @RequestParam(value = "userId") String userId) throws Exception{
        User user = userService.getUser(userId);
        if(null == user) {
            return null;
        }
        UserSecurity userSecurity = userSecurityService.getKeyByUserId(userId, true);
        if (null == userSecurity) {
            return null;
        }
        Map<String, String> keyMap = new HashMap<>();
        String validTime = DateFormatUtils.format(userSecurity.getFromDate(), "yyyy-MM-dd")
                + "~" + DateFormatUtils.format(userSecurity.getExpiryDate(), "yyyy-MM-dd");
        keyMap.put("publicKey", userSecurity.getPublicKey());
        keyMap.put("validTime", validTime);
        keyMap.put("startTime", DateFormatUtils.format(userSecurity.getFromDate(), "yyyy-MM-dd"));
        return keyMap;
    }

    /**
     * 用户基本信息验证
     * @param user
     * @return
     */
    private String basicVerify(User user, boolean update) {
        String errorMsg = "";
        if (StringUtils.isEmpty(user.getLoginCode())) {
            errorMsg += "账户不能为空!";
        }
        if (StringUtils.isEmpty(user.getRealName())) {
            errorMsg += "姓名不能为空!";
        }
        if (StringUtils.isEmpty(user.getIdCardNo())) {
            errorMsg += "身份证号不能为空!";
        }
        if (StringUtils.isEmpty(user.getEmail())) {
            errorMsg += "邮箱不能为空!";
        }
        if (StringUtils.isEmpty(user.getTelephone())) {
            errorMsg += "电话号码不能为空!";
        }
        if (StringUtils.isEmpty(user.getRole())) {
            errorMsg += "用户角色不能为空!";
        }
        if (!StringUtils.isEmpty(errorMsg)) {
            return errorMsg;
        }

        if (update) {
            List<User> oldUserList1 = userService.findByField("id", user.getId());
            if (oldUserList1.size() <= 0) {
                return "操作用户不存在";
            }
            User oldUser = oldUserList1.get(0);
            if (!user.getLoginCode().equals(oldUser.getLoginCode()) && userService.findByField("loginCode", user.getLoginCode()).size() > 0) {
                return "账户已存在";
            }
            if (!user.getIdCardNo().equals(oldUser.getIdCardNo()) && userService.findByField("idCardNo", user.getIdCardNo()).size() > 0) {
                return "身份证号已存在";
            }
            if (!user.getEmail().equals(oldUser.getEmail()) && userService.findByField("email", user.getEmail()).size() > 0) {
                return "邮箱已存在";
            }
            if (!user.getTelephone().equals(oldUser.getTelephone()) && userService.findByField("telephone", user.getTelephone()).size() > 0) {
                return "电话号码已存在";
            }
        } else {
            if (userService.findByField("loginCode", user.getLoginCode()).size() > 0) {
                return "账户已存在";
            }
            if (userService.findByField("idCardNo", user.getIdCardNo()).size() > 0) {
                return "身份证号已存在";
            }
            if (userService.findByField("email", user.getEmail()).size() > 0) {
                return "邮箱已存在";
            }
            if (userService.findByField("telephone", user.getTelephone()).size() > 0) {
                return "电话号码已存在";
            }
        }

        return errorMsg;
    }


    @RequestMapping(value = ServiceApi.Users.UsersOfApp, method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @ApiOperation(value = "App用户注册信息-创建用户", notes = "App用户注册信息")
    public Envelop appCreateUser(
            @ApiParam(name = "userJsonData", required = true,value = "用户json", defaultValue = "")
            @RequestParam(value = "userJsonData") String userJsonData,
            @ApiParam(name = "appId", value = "应用id-健康上饶appid", defaultValue = "WYo0l73F8e")
            @RequestParam(value = "appId") String appId) throws Exception {
        Envelop envelop = new Envelop();
        User user = toEntity(userJsonData, User.class);
        if( StringUtils.isEmpty(user.getDemographicId()) ){
            envelop.setErrorMsg("身份证不能为空");
            return envelop;
        }
        if( StringUtils.isEmpty(user.getTelephone()) ){
            envelop.setErrorMsg("手机号不能为空");
            return envelop;
        }
        if( StringUtils.isEmpty(user.getPassword()) ){
            envelop.setErrorMsg("密码不能为空");
            return envelop;
        }
        String userId = getObjectId(BizObject.User);
        user.setId(userId);
        user.setCreateDate(new Date());
        user.setIdCardNo(user.getDemographicId());
        if (!StringUtils.isEmpty(user.getPassword())) {
            user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        } else {
            user.setPassword(DigestUtils.md5Hex(default_password));
        }
        user.setLoginCode(user.getDemographicId());
        user.setRealName(user.getTelephone());
        user.setDType("Patient");
        user.setActivated(true);
        if (userService.findByField("loginCode", user.getDemographicId()).size() > 0) {
            envelop.setErrorMsg("账户已存在");
            return envelop;
        }
        if (userService.findByField("demographicId", user.getDemographicId()).size() > 0) {
            envelop.setErrorMsg("身份证号已存在");
            return envelop;
        }

        if (userService.findByField("telephone", user.getTelephone()).size() > 0) {
            envelop.setErrorMsg("电话号码已存在");
            return envelop;
        }
        user = userService.saveUser(user);
        // orgcode卫计委机构编码-PDY026797 添加居民的时候 默认 加到卫计委-居民角色中
        List<Roles> rolesList = rolesService.findByCodeAndAppIdAndOrgCode(Arrays.asList(new String[]{orgcode}),appId,"Patient");
        //在org_member_relation 表里追加关联关系
        if(null != rolesList && rolesList.size()>0){
            roleUserService.batchCreateRoleUsersRelation(userId,String.valueOf(rolesList.get(0).getId()));
        }else{
            envelop.setErrorMsg("角色不存在！");
            return envelop;
        }
        // 根据身份证号码查找居民，若不存在则创建居民。
        DemographicInfo demographicInfo = demographicService.getDemographicInfo(user.getDemographicId());
        if(null == demographicInfo){
            demographicInfo = new DemographicInfo();
            demographicInfo.setIdCardNo(user.getIdCardNo());
            demographicInfo.setTelephoneNo("{\"联系电话\":\"" + user.getTelephone() + "\"}");
            demographicInfo.setName(user.getRealName());
            demographicInfo.setPassword(user.getPassword());
            demographicService.savePatient(demographicInfo);
        }
        envelop.setObj(convertToModel(user, MUser.class, null));
        envelop.setSuccessFlg(true);
        return envelop ;
    }

    @RequestMapping(value = ServiceApi.Users.changePasswordByTelephone, method = RequestMethod.POST)
    @ApiOperation(value = "手机号码-修改密码")
    public Envelop changePasswordByTelephone(
            @ApiParam(name = "telephone", value = "电话号码", required = true)
            @RequestParam(value = "telephone") String telephone,
            @ApiParam(name = "password", value = "password", required = true)
            @RequestParam(value = "password") String password) {
        Envelop envelop = new Envelop();
        User user = userService.getUserByTel(telephone);
        if (null == user) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("电话号码不存在！");
            return envelop;
        }
        user.setPassword(DigestUtils.md5Hex(password));
        user = userService.save(user);
        envelop.setSuccessFlg(true);
        envelop.setObj(user);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Users.changePasswordByOldPassword, method = RequestMethod.POST)
    @ApiOperation(value = "健康上饶-根据旧密码修改用户的密码", notes = "根据旧密码修改用户的密码信息")
    public Envelop updatePasswordByOldPwd(
            @ApiParam(name = "userId", value = "用户id", defaultValue = "")
            @RequestParam(value = "userId", required = false) String userId,
            @ApiParam(name = "passwordOld", value = "旧密码", defaultValue = "")
            @RequestParam(value = "passwordOld", required = false) String passwordOld,
            @ApiParam(name = "passwordNew", value = "新密码", defaultValue = "")
            @RequestParam(value = "passwordNew", required = false) String passwordNew) throws Exception {
        Envelop envelop = new Envelop();
        //获取用户信息，根据用户ID
        User user  = userService.getUser(userId);
        if (user == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("对不起，该用户不存在，请确认！");
            return envelop;
        }
        //对旧密码进行MD5加密后，进行对比验证
        String hashPassWordOld = DigestUtils.md5Hex(passwordOld);
        if (org.apache.commons.lang3.StringUtils.equals(hashPassWordOld, user.getPassword().toString())) {
            //当验证通过后，进行新密码的更新在微服务中会将该密码信息进行MD5加密
            user.setPassword(DigestUtils.md5Hex(passwordNew));
            user = userService.save(user);
            if (null != user) {
                envelop.setSuccessFlg(true);
                envelop.setObj(user);
            } else {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("密码修改失败，请联系管理员！");
            }
        } else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("对不起，原密码不正确，请确认！");
        }

        return envelop;
    }

    @RequestMapping(value = ServiceApi.Users.UsersOfAppPhoneExistence, method = RequestMethod.GET)
    @ApiOperation("根据过滤条件判断是否存在")
    public boolean UsersOfAppPhoneExistence(
            @ApiParam(name = "filters", value = "filters", defaultValue = "")
            @RequestParam(value = "filters") String filters) throws Exception {
        List<User> user = userService.search("", filters, "", 1, 1);
        return user != null && user.size() > 0;
    }

    @RequestMapping(value = ServiceApi.Users.updateUserTelePhone, method = RequestMethod.POST)
    @ApiOperation(value = "更换手机号码", notes = "更换手机号码")
    public Envelop updateUserTelePhone(
            @ApiParam(name = "userId", value = "用户id", defaultValue = "")
            @RequestParam(value = "userId", required = false) String userId,
            @ApiParam(name = "telePhoneNew", value = "新手机号码", defaultValue = "")
            @RequestParam(value = "telePhoneNew") String telePhoneNew) throws Exception {
        Envelop envelop = new Envelop();
        //获取用户信息，根据用户ID
        User user  = userService.getUser(userId);
        if (user == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("对不起，该用户不存在，请确认！");
            return envelop;
        }
        user.setTelephone(telePhoneNew);
        user = userService.save(user);
        if (null != user) {
            envelop.setSuccessFlg(true);
            envelop.setObj(user);
        } else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("更换手机号码失败，请联系管理员！");
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Users.GetUserOfUsersOfApp, method = RequestMethod.GET)
    @ApiOperation(value = "公众健康服务-根据用户身份证号码或者电话号码，获取用户")
    public Envelop getUserOfUsersOfApp(
            @ApiParam(name = "userName", value = "身份证号码或者电话号码", defaultValue = "")
            @RequestParam(value = "userName") String userName) {
        Envelop envelop = new Envelop();
        List<User> userList = userService.getUserForLogin(userName);
        if(null != userList && userList.size()>0){
            User user = userList.get(0);
            envelop.setSuccessFlg(true);
            envelop.setObj(user);
        }else{
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("用户不存在！");
        }
        return  envelop;
    }

}