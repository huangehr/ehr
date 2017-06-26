package com.yihu.ehr.user.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.security.MKey;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.user.feign.ConventionalDictClient;
import com.yihu.ehr.user.feign.SecurityClient;
import com.yihu.ehr.user.entity.User;
import com.yihu.ehr.user.service.UserManager;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.util.hash.HashUtil;
import com.yihu.ehr.util.log.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.time.DateFormatUtils;
import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
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
    private String default_password = "123456";

    @Autowired
    private UserManager userManager;

    @Autowired
    private SecurityClient securityClient;

    @Autowired
    private ConventionalDictClient conventionalDictClient;

    @Autowired
    private FastDFSUtil fastDFSUtil;

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
            HttpServletRequest request,
            HttpServletResponse response) throws ParseException {
        List<User> userList = userManager.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, userManager.getCount(filters), page, size);

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
            user.setPassword(HashUtil.hash(user.getPassword()));
        } else {
            user.setPassword(HashUtil.hash(default_password));
        }
        String userType = user.getUserType();
        MConventionalDict dict = conventionalDictClient.getUserType(userType);
        if (dict != null) {
            user.setDType(userType);
        }
        user.setActivated(true);
        user = userManager.saveUser(user);
        return convertToModel(user, MUser.class, null);
    }

    @RequestMapping(value = ServiceApi.Users.Users, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改用户", notes = "重新绑定用户信息")
    public MUser updateUser(
            @ApiParam(name = "user_json_data", value = "", defaultValue = "")
            @RequestBody String userJsonData) throws Exception {
        User user = toEntity(userJsonData, User.class);
        String userType = user.getUserType();
        MConventionalDict dict = conventionalDictClient.getUserType(userType);
        if (dict != null) {
            user.setDType(userType);
        }
        userManager.saveUser(user);
        return convertToModel(user, MUser.class);
    }

    @RequestMapping(value = ServiceApi.Users.UserAdmin, method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取获取用户信息")
    public MUser getUser(
            @ApiParam(name = "user_id", value = "", defaultValue = "")
            @PathVariable(value = "user_id") String userId) {
        User user = userManager.getUser(userId);
        MUser userModel = convertToModel(user, MUser.class);
        return userModel;
    }

    @RequestMapping(value = ServiceApi.Users.UserAdmin, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除用户", notes = "根据id删除用户")
    public boolean deleteUser(
            @ApiParam(name = "user_id", value = "用户编号", defaultValue = "")
            @PathVariable(value = "user_id") String userId) throws Exception {
        userManager.deleteUser(userId);
        return true;
    }

    @RequestMapping(value = ServiceApi.Users.UserAdmin, method = RequestMethod.PUT)
    @ApiOperation(value = "改变用户状态", notes = "根据id更新用户")
    public boolean activityUser(
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "activity", value = "激活状态", defaultValue = "")
            @RequestParam(value = "activity") boolean activity) throws Exception {
        userManager.activityUser(userId, activity);
        return true;
    }

    @RequestMapping(value = ServiceApi.Users.User, method = RequestMethod.GET)
    @ApiOperation(value = "根据登录账号获取当前用户", notes = "根据登陆用户名及密码验证用户")
    public MUser getUserByLoginCode(
            @ApiParam(name = "user_name", value = "登录账号", defaultValue = "")
            @PathVariable(value = "user_name") String userName) {
        User user = userManager.getUserByUserName(userName);
        return convertToModel(user, MUser.class);
    }

    @RequestMapping(value = ServiceApi.Users.UserAdminPassword, method = RequestMethod.PUT)
    @ApiOperation(value = "重设密码", notes = "用户忘记密码管理员帮助重新还原密码，初始密码123456")
    public boolean resetPass(
            @ApiParam(name = "user_id", value = "id", defaultValue = "")
            @PathVariable(value = "user_id") String userId) throws Exception {
        userManager.resetPass(userId);
        return true;
    }

    @RequestMapping(value = ServiceApi.Users.UserAdminPasswordReset, method = RequestMethod.PUT)
    @ApiOperation(value = "修改密码", notes = "根基传入的用户id和新的密码修改用户的密码")
    public boolean changePassWord(
            @ApiParam(name = "user_id", value = "user_id", defaultValue = "")
            @PathVariable(value = "user_id") String userId,
            @ApiParam(name = "password", value = "密码", defaultValue = "")
            @RequestParam(value = "password") String password) throws Exception {
        String hashPassWord = HashUtil.hash(password);
        userManager.changePassWord(userId, hashPassWord);
        return true;
    }

    @RequestMapping(value = ServiceApi.Users.UserAdminKey, method = RequestMethod.PUT)
    @ApiOperation(value = "重新分配密钥", notes = "重新分配密钥")
    public Map<String, String> distributeKey(
            @ApiParam(name = "user_id", value = "登录帐号", defaultValue = "")
            @PathVariable(value = "user_id") String userId) {
        MKey userSecurity = securityClient.getUserKey(userId);
        Map<String, String> keyMap = new HashMap<>();
        if (userSecurity != null) {
            // 删除原有的公私钥重新分配
            boolean result = securityClient.deleteKeyByUserId(userId);

        }

        userSecurity = securityClient.createUserKey(userId);

        String validTime = DateFormatUtils.format(userSecurity.getFromDate(), "yyyy-MM-dd")
                + "~" + DateFormatUtils.format(userSecurity.getExpiryDate(), "yyyy-MM-dd");
        keyMap.put("publicKey", userSecurity.getPublicKey());
        keyMap.put("validTime", validTime);
        keyMap.put("startTime", DateFormatUtils.format(userSecurity.getFromDate(), "yyyy-MM-dd"));
        return keyMap;
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
        User user = userManager.loginVerification(userName, password);
        return convertToModel(user, MUser.class);
    }

    @RequestMapping(value = ServiceApi.Users.UserExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断用户名是否存在")
    public boolean isUserNameExists(
            @ApiParam(name = "user_name", value = "user_name", defaultValue = "")
            @PathVariable(value = "user_name") String userName) {
        return userManager.getUserByUserName(userName) != null;
    }

    @RequestMapping(value = ServiceApi.Users.UserIdCardNoExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断用户身份证号是否存在")
    public boolean isIdCardExists(
            @ApiParam(name = "id_card_no", value = "id_card_no", defaultValue = "")
            @RequestParam(value = "id_card_no") String idCardNo) {
        return userManager.getUserByIdCardNo(idCardNo) != null;
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
        return  userManager.getUserByEmail(email)!= null;
    }

    @RequestMapping(value = ServiceApi.Users.UserTelephoneNoExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断用户电话号码是否存在")
    public boolean isTelephoneExists(@RequestParam(value = "telephone") String telephone) {
        return userManager.getUserByTelephone(telephone) != null;
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


    @RequestMapping(value = ServiceApi.Users.UserPhoneExistence,method = RequestMethod.POST)
    @ApiOperation("获取已存在电话号码")
    public List idExistence(
            @ApiParam(name="phones",value="phones",defaultValue = "")
            @RequestBody String phones) throws Exception {

        List existPhones = userManager.idExist(toEntity(phones, String[].class));
        return existPhones;
    }

    @RequestMapping(value = ServiceApi.Users.UserOnePhoneExistence,method = RequestMethod.GET)
    @ApiOperation("根据过滤条件判断是否存在")
    public boolean isExistence(
            @ApiParam(name="filters",value="filters",defaultValue = "")
            @RequestParam(value="filters") String filters) throws Exception {

        List<User> user = userManager.search("",filters,"", 1, 1);
        return user!=null && user.size()>0;
    }
    @RequestMapping(value = ServiceApi.Users.UserEmailExistence,method = RequestMethod.POST)
    @ApiOperation("获取已存在邮箱")
    public List emailsExistence(
            @ApiParam(name="emails",value="emails",defaultValue = "")
            @RequestBody String emails) throws Exception {

        List existPhones = userManager.emailsExistence(toEntity(emails, String[].class));
        return existPhones;
    }
}