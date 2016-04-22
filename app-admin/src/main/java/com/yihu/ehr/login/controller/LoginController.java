package com.yihu.ehr.login.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.user.UserDetailModel;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.SessionAttributeKeys;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.RestTemplates;
import com.yihu.ehr.util.controller.BaseUIController;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by lingfeng on 2015/6/30.
 */
@Controller
@RequestMapping("/login")
@SessionAttributes(SessionAttributeKeys.CurrentUser)
public class LoginController extends BaseUIController {

    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.url}")
    private String comUrl;

    @RequestMapping(value = "")
    public String login(Model model) {
        model.addAttribute("contentPage","login/login");
        return "generalView";
    }

    @RequestMapping(value = "validate", method = RequestMethod.POST)
    public String loginValid(Model model, String userName, String password, HttpServletRequest request, HttpServletResponse response) {

        String url = "/users/verification/" + userName;
        String resultStr = "";
        Map<String, Object> params = new HashMap<>();

        params.put("psw", password);
        try {
            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, this.password);
            Envelop envelop = getEnvelop(resultStr);
            UserDetailModel userDetailModel =getEnvelopModel(envelop.getObj(),UserDetailModel.class);

//            判断用户是否登入成功
            if (envelop.isSuccessFlg()){
                String lastLoginTime = null;

//                判断用户密码是否初始密码
                if (password.equals("123456"))
                    model.addAttribute("defaultPassWord",true);

//                判断用户是否失效
                if(!userDetailModel.getActivated()){
                    model.addAttribute("userName", userName);
                    model.addAttribute("successFlg", false);
                    model.addAttribute("failMsg", "该用户已失效，请联系系统管理员重新生效。");
                    model.addAttribute("contentPage","login/login");

                    return "generalView";
                }

                SimpleDateFormat sdf = new SimpleDateFormat(AgAdminConstants.DateTimeFormat);
                String now = sdf.format(new Date());
                if(userDetailModel.getLastLoginTime()!= null){
                   lastLoginTime = userDetailModel.getLastLoginTime();
                }else{
                    lastLoginTime = now;
                }


                model.addAttribute(SessionAttributeKeys.CurrentUser, userDetailModel);
                request.getSession().setAttribute("last_login_time", lastLoginTime);
                //update lastLoginTime
                userDetailModel.setLastLoginTime(now);
                url="/user";
                MultiValueMap<String, String> conditionMap = new LinkedMultiValueMap<>();
                conditionMap.add("user_json_datas", toJson(userDetailModel));
                conditionMap.add("inputStream", "");
                conditionMap.add("imageName", "");
                RestTemplates templates = new RestTemplates();
                resultStr = templates.doPost(comUrl + url,conditionMap);
                return "redirect:/index";
            }else{

                model.addAttribute("userName", userName);
                model.addAttribute("successFlg", false);
                model.addAttribute("failMsg", "用户名或密码错误，请重新输入。");
                model.addAttribute("contentPage","login/login");
                return "generalView";
            }
        } catch (Exception e) {
            model.addAttribute("userName", userName);
            model.addAttribute("successFlg", false);
            model.addAttribute("failMsg", e.getMessage());
            model.addAttribute("contentPage","login/login");
            return "generalView";
        }
    }

    //todo:暂时没用到
//    @RequestMapping(value = "activeValidateCode")
//    public String activeValidateCode(String loginCode, String validateCode, Model model) {
//        XUser user = userManager.getUserByLoginCode(loginCode);
//        Map<ErrorCode, String> message = new HashMap<>();
//
//        if (user != null) {
//            if (user.isActivated()) {
//                Date currentTime = new Date();
//                if (currentTime.before(user.getCreateDate())) {
//                    if (validateCode.equals(user.getValidateCode())) {
//                        model.addAttribute("successFlg", true);
//                        model.addAttribute("contentPage","login/login");
//                        return "generalView";
//                    } else {
//                        model.addAttribute("successFlg", false);
//                        message.put(ErrorCode.InvalidValidateCode, "验证码不正确");
//                        model.addAttribute("contentPage","login/login");
//                        return "generalView";
//                    }
//                } else {
//                    message.put(ErrorCode.ExpireValidateCode, "验证码已过期");
//                    model.addAttribute("contentPage","login/login");
//                    return "generalView";
//                }
//            } else {
//                message.put(ErrorCode.MailHasValidate, "邮箱已验证，请登录！");
//                model.addAttribute("contentPage","login/login");
//                return "generalView";
//            }
//        } else {
//            message.put(ErrorCode.InvalidMail, "该邮箱未注册（邮箱地址不存在）！");
//            model.addAttribute("contentPage","login/login");
//            return "generalView";
//        }
//    }

    //todo:暂时没用到
//    @RequestMapping(value = "sendActiveCode")
//    public String sendActiveCode(Model model, String loginCode, String email) {
//        Map<ErrorCode, String> message = new HashMap<>();
//        XUser user = userManager.getUserByCodeAndEmail(loginCode, email);
//
//        if (user == null || "0".equals(user.getActivated())) {
//            message.put(ErrorCode.InvalidUser, "用户不存在!");
//            model.addAttribute("message", message);
//            model.addAttribute("contentPage","login/login");
//            return "generalView";
//        }
//
//        user.setValidateCode("1234");
//        userManager.updateUser(user);
//
//        StringBuffer sb = new StringBuffer("点击下面链接激活账号，48小时生效，否则重新注册账号，链接只能使用一次，请尽快激活！</br>");
//        sb.append(user.getValidateCode());
//
//        MailUtil.send(email, sb.toString());
//        return "login/login";
//    }

    /**
     * 获取当前用户的ip地址
     *
     * @return
     */
    //todo:暂时没用到
//    public String requestGetAddress(HttpServletRequest request) {
//        String strRead = null;
//        String address = null;
//        String ip = getIp();
//
//        SystemDictId systemDict = new SystemDictId();
//        String addressAPI = systemDict.AddressAPI;
//        String apiKey = systemDict.Apikey;
//        LoginAddress loginAddress = conventionalDictEntry.getLoginAddress(addressAPI);
//        LoginAddress apiKeys = conventionalDictEntry.getLoginAddress(apiKey);
//        String httpUrl = loginAddress.getValue();
//        String httpArg = "ip=" + ip;
//
//        BufferedReader reader = null;
//        httpUrl = httpUrl + "?" + httpArg;
//
//        try {
//            URL url = new URL(httpUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setRequestMethod("GET");
//
//            connection.setRequestProperty("apikey", apiKeys.getValue());
//            connection.connect();
//            InputStream is = connection.getInputStream();
//            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//            strRead = reader.readLine();
//            reader.close();
//
//            address = getAddress(strRead);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return address;
//    }

    public String getAddress(String citys) {

        String[] st = citys.split(",");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < st.length; i++) {
            String addressEs = st[i];
            String[] address = addressEs.split(":");
            for (int j = 0; j < 1; j++) {
                String provinceCitys = address[1];

                if (provinceCitys.length() > 1) {
                    String ar = (provinceCitys.substring(1, provinceCitys.length() - 1));
                    String getCity = StringEscapeUtils.unescapeJava(ar);
                    list.add(getCity);
                }
            }
        }

        String province = list.get(3);
        String city = list.get(4);
        String provinceCity = null;
        if (province.equals("None") || province.equals("") || province == null && city.equals("None") || city.equals("") || city == null) {
            provinceCity = "未知";
        } else {
            provinceCity = province + "省" + city + "市";
        }

        return provinceCity;
    }

    /**
     * 多IP处理，可以得到最终ip
     *
     * @return
     */
    public String getIp() {
        String localIP = null;      // 本地IP，如果没有配置外网IP则返回它
        String publicIP = null;     // 外网IP
        try {
            Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            boolean finded = false;// 是否找到外网IP
            while (netInterfaces.hasMoreElements() && !finded) {
                NetworkInterface ni = netInterfaces.nextElement();
                Enumeration<InetAddress> address = ni.getInetAddresses();
                while (address.hasMoreElements()) {
                    ip = address.nextElement();
                    if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 外网IP
                        publicIP = ip.getHostAddress();
                        finded = true;
                        break;
                    } else if (ip.isSiteLocalAddress()
                            && !ip.isLoopbackAddress()
                            && ip.getHostAddress().indexOf(":") == -1) {
                        localIP = ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (publicIP != null && !"".equals(publicIP)) {
            return publicIP;
        } else {
            return localIP;
        }
    }

    @RequestMapping(value = "/userVerification")
    @ResponseBody
    public Object dataValidation(String userName, String password) {

        Map<String, Object> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        Envelop envelop = new Envelop();

        String url = "/users/verification/" + userName;
        String resultStr = "";

        params.put("psw", password);
        try {

            resultStr = HttpClientUtil.doGet(comUrl + url, params, username, this.password);
            envelop = mapper.readValue(resultStr,Envelop.class);
            if(!envelop.isSuccessFlg()){
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("密码错误，请重新输入！");
            }else {
                envelop.setSuccessFlg(true);
            }
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("密码错误，请重新输入！");
            return envelop;
        }
        return envelop;
    }

}