package com.yihu.ehr.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.yihu.ehr.agModel.user.UserDetailModel;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.fastdfs.FastDFSClientPool;
import com.yihu.ehr.fastdfs.FastDFSUtil;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.ResourceProperties;
import com.yihu.ehr.util.encode.Base64;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang.ArrayUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zlf
 * @version 1.0
 * @created 2015.08.10 17:57
 */
@Controller
@RequestMapping("/user")
public class UserController {
    public static final String GroupField = "groupName";
    public static final String RemoteFileField = "remoteFileName";
    public static final String FileIdField = "fid";
    public static final String FileUrlField = "fileUrl";

    @Autowired
    FastDFSUtil dfsUtil;

    @Autowired
    FastDFSClientPool clientPool;

    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.url}")
    private String comUrl;

    @RequestMapping("initial")
    public String userInitial(Model model) {
        model.addAttribute("contentPage", "user/user");
        return "pageView";
    }

    @RequestMapping("addUserInfoDialog")
    public String addUser(Model model) {
        model.addAttribute("contentPage", "user/addUserInfoDialog");
        return "generalView";
    }

    @RequestMapping("searchUsers")
    @ResponseBody
    public Object searchUsers(String searchNm, String searchType, int page, int rows) {

        String url = "/users";
        String resultStr = "";
        Envelop envelop = new Envelop();
        Map<String, Object> params = new HashMap<>();

        StringBuffer stringBuffer = new StringBuffer();
        if (!StringUtils.isEmpty(searchNm)) {
            stringBuffer.append("realName?" + searchNm + " g1;organization?" + searchNm + " g1;");
        }
        if (!StringUtils.isEmpty(searchType)) {
            stringBuffer.append("userType=" + searchType);
        }

        params.put("filters", "");
        String filters = stringBuffer.toString();
        if (!StringUtils.isEmpty(filters)) {
            params.put("filters", filters);
        }

        params.put("page", page);
        params.put("size", rows);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            return resultStr;
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
        }

    }

    @RequestMapping("deleteUser")
    @ResponseBody
    public Object deleteUser(String userId) {
        String url = "/users/" + userId;
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        params.put("userId", userId);
        try {
            resultStr = HttpClientUtil.doDelete(comUrl + url, params, username, password);
            result = mapper.readValue(resultStr, Envelop.class);
            if (result.isSuccessFlg()) {
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidDelete.toString());
            }
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }

    }

    @RequestMapping("activityUser")
    @ResponseBody
    public Object activityUser(String userId, boolean activated) {
        String url = "/users/"+userId;
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("activity", activated);
        try {
            resultStr = HttpClientUtil.doPut(comUrl + url, params, username, password);

            if (Boolean.parseBoolean(resultStr)) {
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidUpdate.toString());
            }
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }

    }

    @RequestMapping(value = "updateUser", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public Object updateUser(String userModelJsonData,HttpServletRequest request, HttpServletResponse response) throws IOException {

        String url = "/users/";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        InputStream inputStream = request.getInputStream();

        int actualRead = 0;
        int bufferSize = 1024;
        byte tempBuffer[] = new byte[1024];
        byte[] fileBuffer = new byte[0];
        while ((actualRead = inputStream.read(tempBuffer)) != -1) {
            fileBuffer = ArrayUtils.addAll(fileBuffer, ArrayUtils.subarray(tempBuffer, 0, actualRead));
        }

        String imageStream = Base64.encode(fileBuffer);

        String userJsonDataModel = URLDecoder.decode(userModelJsonData,"UTF-8");
        UserDetailModel userDetailModel = mapper.readValue(userJsonDataModel, UserDetailModel.class);
        params.put("user_json_data", userDetailModel);
        params.put("imageStream", imageStream);
        try {
            if (!StringUtils.isEmpty(userDetailModel.getId())) {
                //修改
                resultStr = HttpClientUtil.doPut(comUrl + url, params, username, password);
            }else{
                //新增
                resultStr = HttpClientUtil.doPost(comUrl + url, params, username, password);
            }
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return resultStr;

    }
//    public String upload(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        try {
//            request.setCharacterEncoding("utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        //获取流
//        InputStream inputStream = request.getInputStream();
//        //获取文件名
//        String fileName = request.getParameter("name");
//        if (fileName == null || fileName.equals("")) {
//            return null;
//        }
//        //获取文件扩展名
//        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
//        //获取文件名
//        String description = null;
//        if ((fileName.length() > 0)) {
//            int dot = fileName.lastIndexOf('.');
//            if ((dot > -1) && (dot < (fileName.length()))) {
//                description = fileName.substring(0, dot);
//            }
//        }
//        ObjectNode objectNode = null;
//        String path = null;
//        try {
//            objectNode = dfsUtil.upload(inputStream, fileExtension, description);
////            objectNode = upload(inputStream, fileExtension, description);
//            String groupName = objectNode.get("groupName").toString();
//            String remoteFileName = objectNode.get("remoteFileName").toString();
//            path = "{groupName:" + groupName + ",remoteFileName:" + remoteFileName + "}";
//        } catch (Exception e) {
//            e.printStackTrace();
////            LogService.getLogger(AbstractUser.class).error("用户头像上传失败；错误代码："+e);
//        }
//        return path;
//    }








    @RequestMapping("resetPass")
    @ResponseBody
    public Object resetPass(String userId) {
        String url = "/users/password/"+userId;
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        try {
            resultStr = HttpClientUtil.doPut(comUrl + url, params, username, password);
            if (Boolean.parseBoolean(resultStr)) {
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidUpdate.toString());
            }
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        try {
//            userManager.resetPass(userId);
//            Result result = getSuccessResult(true);
//            return result.toJson();
//        } catch (Exception e) {
//
//            Result result = getSuccessResult(false);
//            return result.toJson();
//        }

    }

    @RequestMapping("getUser")
    public Object getUser(Model model, String userId, String mode) throws IOException {
        String url = "/users/"+userId;
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            model.addAttribute("allData", resultStr);
            model.addAttribute("mode", mode);
            model.addAttribute("contentPage", "user/userInfoDialog");
            return "simpleView";
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
    }

    @RequestMapping("unbundling")
    @ResponseBody
    public Object unbundling(String userId, String type) {
        String getUserUrl = "/users/binding/"+userId;//解绑 todo 网关中需添加此方法
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("type", type);
        try {
            resultStr = HttpClientUtil.doPost(comUrl + getUserUrl, params, username, password);
            if (Boolean.parseBoolean(resultStr)) {
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidUpdate.toString());
            }
            return result;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }
//        XUser user = userManager.getUser(userId);
//
//        if (type.equals("tel")) {
//            //tel尚未数据库映射
//            user.setTelephone("");
//        } else {
//            user.setEmail("");
//        }
//        userManager.updateUser(user);
    }

    @RequestMapping("distributeKey")
    @ResponseBody
    public Object distributeKey(String loginCode) {
        String getUserUrl = "/users/key/"+loginCode;
        String resultStr = "";
        Envelop envelop = new Envelop();
        Map<String, Object> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        params.put("loginCode", loginCode);
        try {
            resultStr = HttpClientUtil.doPut(comUrl + getUserUrl, params, username, password);
//            envelop = mapper.readValue(resultStr,Envelop.class);
            if (!StringUtils.isEmpty(resultStr)) {
                envelop.setObj(resultStr);
                envelop.setSuccessFlg(true);
            } else {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg(ErrorCode.InvalidUpdate.toString());
            }
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return envelop;
//        try {
//            XUserSecurity userSecurity = securityManager.getUserSecurityByUserName(loginCode);
//            Map<String, String> keyMap = new HashMap<>();
//            if (userSecurity == null) {
//                XUserManager userManager = ServiceFactory.getService(Services.UserManager);
//                XUser userInfo = userManager.getUserByLoginCode(loginCode);
//                String userId = userInfo.getId();
//                userSecurity = securityManager.createSecurityByUserId(userId);
//            } else {
//                //result.setErrorMsg("公钥信息已存在。");
//                //这里删除原有的公私钥重新分配
//                //1-1根据用户登陆名获取用户信息。
//                XUserManager userManager = ServiceFactory.getService(Services.UserManager);
//                XUser userInfo = userManager.getUserByLoginCode(loginCode);
//                String userId = userInfo.getId();
//                String userKeyId = securityManager.getUserKeyByUserId(userId);
//                securityManager.deleteSecurity(userSecurity.getId());
//                securityManager.deleteUserKey(userKeyId);
//                userSecurity = securityManager.createSecurityByUserId(userId);
//
//            }
//            String validTime = DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT)
//                    + "~" + DateUtil.toString(userSecurity.getExpiryDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT);
//            keyMap.put("publicKey", userSecurity.getPublicKey());
//            keyMap.put("validTime", validTime);
//            keyMap.put("startTime", DateUtil.toString(userSecurity.getFromDate(), DateUtil.DEFAULT_DATE_YMD_FORMAT));
//
//            Result result = getSuccessResult(true);
//            result.setObj(keyMap);
//            return result.toJson();
//        } catch (Exception ex) {
//
//            Result result = getSuccessResult(true);
//            return result.toJson();
//        }
    }

    @RequestMapping("/existence")
    @ResponseBody
    public Object searchUser(String existenceType, String existenceNm) {
        String getUserUrl = "/users/existence";
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("existenceType",existenceType);
        params.put("existenceNm",existenceNm);

        try {
            resultStr = HttpClientUtil.doGet(comUrl + getUserUrl, params, username, password);
            return resultStr;
        } catch (Exception e) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.SystemError.toString());
            return result;
        }

    }

//    public String upload(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        try {
//            request.setCharacterEncoding("utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        //获取流
//        InputStream inputStream = request.getInputStream();
//        //获取文件名
//        String fileName = request.getParameter("name");
//        if (fileName == null || fileName.equals("")) {
//            return null;
//        }
//        //获取文件扩展名
//        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
//        //获取文件名
//        String description = null;
//        if ((fileName.length() > 0)) {
//            int dot = fileName.lastIndexOf('.');
//            if ((dot > -1) && (dot < (fileName.length()))) {
//                description = fileName.substring(0, dot);
//            }
//        }
//        ObjectNode objectNode = null;
//        String path = null;
//        try {
//            objectNode = dfsUtil.upload(inputStream, fileExtension, description);
////            objectNode = upload(inputStream, fileExtension, description);
//            String groupName = objectNode.get("groupName").toString();
//            String remoteFileName = objectNode.get("remoteFileName").toString();
//            path = "{groupName:" + groupName + ",remoteFileName:" + remoteFileName + "}";
//        } catch (Exception e) {
//            e.printStackTrace();
////            LogService.getLogger(AbstractUser.class).error("用户头像上传失败；错误代码："+e);
//        }
//        return path;
//    }

//    public String download(UserModel userModel) throws IOException {
//        String getUserUrl = "/user/downFile";
//        String resultStr = "";
//        Result result = new Result();
//        Map<String, Object> params = new HashMap<>();
//
//        if(userModel.getRemotePath() == null||userModel.getRemotePath().equals("")){
//            return null;
//        }
//        Object obj = JSONObject.toBean(JSONObject.fromObject(userModel.getRemotePath()), HashMap.class);
//        String groupName = ((HashMap<String, String>) obj).get("groupName");
//        String remoteFileName = ((HashMap<String, String>) obj).get("remoteFileName");
//        String splitMark = System.getProperty("file.separator");
//        String strPath = System.getProperty("java.io.tmpdir");
//        strPath += splitMark + "userImages" + splitMark + remoteFileName;
//        File file = new File(strPath);
//        String path = String.valueOf(file.getParentFile());
//        if (!file.getParentFile().exists()) {
//            file.getParentFile().mkdirs();
//        }
//        if (userModel.getLocalPath() != null) {
//            File fileName = new File(userModel.getLocalPath());
//            if (fileName.exists()) {
//                return userModel.getLocalPath();
//            }
//        }
//        try {
//            params.put("groupName",groupName);
//            params.put("filePath",path);
//            resultStr = HttpClientUtil.doPost(comUrl + getUserUrl, params, username, password);
//            if(!StringUtil.isEmpty(resultStr)){
//                result.setSuccessFlg(true);
//                result.setObj(resultStr);
//                return result.toJson();
//            }
//            else {
//                result.setSuccessFlg(false);
//                result.setErrorMsg(ErrorCode.InvalidUpdate.toString());
//            }
//            return result.toJson();
//        } catch (Exception e) {
//            LogService.getLogger(AbstractUser.class).error("用户头像图片下载失败；错误代码：" + e);
//            result.setSuccessFlg(false);
//            result.setErrorMsg(ErrorCode.SystemError.toString());
//            return result.toJson();
//        }

//        if(userModel.getRemotePath() == null||userModel.getRemotePath().equals("")){
//            return null;
//        }
//        Object obj = JSONObject.toBean(JSONObject.fromObject(userModel.getRemotePath()), HashMap.class);
//        String groupName = ((HashMap<String, String>) obj).get("groupName");
//        String remoteFileName = ((HashMap<String, String>) obj).get("remoteFileName");
//        String splitMark = System.getProperty("file.separator");
//        String strPath = System.getProperty("java.io.tmpdir");
//        strPath += splitMark + "userImages" + splitMark + remoteFileName;
//        File file = new File(strPath);
//        String path = String.valueOf(file.getParentFile());
//        if (!file.getParentFile().exists()) {
//            file.getParentFile().mkdirs();
//        }
//        if (userModel.getLocalPath() != null) {
//            File fileName = new File(userModel.getLocalPath());
//            if (fileName.exists()) {
//                return userModel.getLocalPath();
//            }
//        }
//        //调用图片下载方法，返回文件的储存位置localPath，将localPath保存至人口信息表
//        String localPath = null;
//        try {
//            localPath = FastDFSUtil.download(groupName, remoteFileName, path);
//            userModel.setLocalPath(localPath);
//            userManager.updateUser(userModel);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (MyException e) {
//            LogService.getLogger(AbstractUser.class).error("用户头像图片下载失败；错误代码：" + e);
//        }
//
//        return localPath;
//    }

    @RequestMapping("showImage")
    @ResponseBody
    public void showImage(HttpServletRequest request, HttpServletResponse response, String localImgPath) throws Exception {
        response.setContentType("text/html; charset=UTF-8");
        response.setContentType("image/jpeg");
        FileInputStream fis = null;
        OutputStream os = null;
        try {
            File file = new File(localImgPath);
            if (!file.exists()) {
                //LogService.getLogger(AbstractUser.class).error("用户头像不存在：" + localImgPath);
                return;
            }
            fis = new FileInputStream(localImgPath);
            os = response.getOutputStream();
            int count = 0;
            byte[] buffer = new byte[1024 * 1024];
            while ((count = fis.read(buffer)) != -1)
                os.write(buffer, 0, count);
            os.flush();
        } catch (IOException e) {
            //LogService.getLogger(AbstractUser.class).error(e.getMessage());
        } finally {
            if (os != null)
                os.close();
            if (fis != null)
                fis.close();
        }
    }

}
