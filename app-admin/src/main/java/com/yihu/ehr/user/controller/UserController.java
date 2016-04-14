package com.yihu.ehr.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.deploy.util.ArrayUtil;
import com.yihu.ehr.agModel.user.UserDetailModel;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.SessionAttributeKeys;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.RestTemplates;
import com.yihu.ehr.util.controller.BaseUIController;
import com.yihu.ehr.util.encode.Base64;
import com.yihu.ehr.util.log.LogService;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zlf
 * @version 1.0
 * @created 2015.08.10 17:57
 */
@Controller
@RequestMapping("/user")
@SessionAttributes(SessionAttributeKeys.CurrentUser)
public class UserController extends BaseUIController {
    public static final String GroupField = "groupName";
    public static final String RemoteFileField = "remoteFileName";
    public static final String FileIdField = "fid";
    public static final String FileUrlField = "fileUrl";

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

    @RequestMapping("initialChangePassword")
    public String inChangePassword(Model model) {
        model.addAttribute("contentPage", "user/changePassword");
        return "generalView";
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
        String url = "/users/admin/" + userId;
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
        String url = "/users/admin/"+userId;
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
        Envelop envelop = new Envelop();
//        Map<String, Object> params = new HashMap<>();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        ObjectMapper mapper = new ObjectMapper();
        RestTemplates templates = new RestTemplates();

        String userJsonDataModel = URLDecoder.decode(userModelJsonData,"UTF-8");
        UserDetailModel userDetailModel = mapper.readValue(userJsonDataModel, UserDetailModel.class);

        request.setCharacterEncoding("UTF-8");
        InputStream inputStream = request.getInputStream();
        String imageName = request.getParameter("name");

        int temp = 0;
        byte[] tempBuffer = new byte[1024];
        byte[] fileBuffer = new byte[0];
        while ((temp = inputStream.read(tempBuffer)) != -1) {
            fileBuffer = ArrayUtils.addAll(fileBuffer,ArrayUtils.subarray(tempBuffer,0,temp));
        }
        inputStream.close();

        String restStream = Base64.encode(fileBuffer);
        String imageStream = URLEncoder.encode(restStream,"UTF-8");

        params.add("inputStream",imageStream);
        params.add("imageName",imageName);
        params.add("user_json_data", userJsonDataModel);

        try {
            if (!StringUtils.isEmpty(userDetailModel.getId())) {
                //修改
                String getUser = templates.doGet(comUrl + "/users/admin/"+userDetailModel.getId());
                envelop = mapper.readValue(getUser,Envelop.class);
                String userJsonModel = mapper.writeValueAsString(envelop.getObj());
                UserDetailModel userModel = mapper.readValue(userJsonModel,UserDetailModel.class);
                userModel.setRealName(userDetailModel.getRealName());
                userModel.setIdCardNo(userDetailModel.getIdCardNo());
                userModel.setGender(userDetailModel.getGender());
                userModel.setMartialStatus(userDetailModel.getMartialStatus());
                userModel.setEmail(userDetailModel.getEmail());
                userModel.setTelephone(userDetailModel.getTelephone());
                userModel.setUserType(userDetailModel.getUserType());
                userModel.setOrganization(userDetailModel.getOrganization());
                userModel.setMajor("");
                if(userDetailModel.getUserType().equals("Doctor")){
                    userModel.setMajor(userDetailModel.getMajor());
                }

                userJsonDataModel = mapper.writeValueAsString(userModel);
                params.add("user_json_datas", userJsonDataModel);

                resultStr = templates.doPost(comUrl + "/user", params);
            }else{
                resultStr = templates.doPost(comUrl + url, params);
            }
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
        }
        return resultStr;

    }

    @RequestMapping("resetPass")
    @ResponseBody
    public Object resetPass(String userId,@ModelAttribute(SessionAttributeKeys.CurrentUser) UserDetailModel userDetailModel) {
        String url = "/users/password/"+userId;
        String resultStr = "";
        Envelop result = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        try {
            resultStr = HttpClientUtil.doPut(comUrl + url, params, username, password);
            if (Boolean.parseBoolean(resultStr)) {
                result.setSuccessFlg(true);
                result.setObj(userDetailModel.getId()); //重置到当前用户时需重登
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

    @RequestMapping("getUser")
    public Object getUser(Model model, String userId, String mode) throws IOException {
        String url = "/users/admin/"+userId;
        String resultStr = "";
        Envelop envelop = new Envelop();
        Map<String, Object> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        params.put("userId", userId);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, password);
            model.addAttribute("allData", resultStr);
            model.addAttribute("mode", mode);
            model.addAttribute("contentPage", "user/userInfoDialog");
            return "simpleView";
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg(ErrorCode.SystemError.toString());
            return envelop;
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
            LogService.getLogger().error(e.getMessage());
        } finally {
            if (os != null) os.close();
            if (fis != null) fis.close();
        }
    }

    @RequestMapping("/changePassWord")
    @ResponseBody
    public Object chAangePassWord(String userId,String passWord){
        String getUserUrl = "/users/changePassWord";
        String resultStr = "";
        Envelop envelop = new Envelop();
        Map<String, Object> params = new HashMap<>();
        params.put("user_id",userId);
        params.put("password",passWord);

        try {
            resultStr = HttpClientUtil.doPut(comUrl + getUserUrl, params, username, password);
            envelop.setObj(resultStr);
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("密码修改失败");
        }

        return envelop;
    }



}
