package com.yihu.ehr.msg.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.apps.service.AppClient;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.entity.user.UserTelVerification;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.msg.service.TelVerificationClient;
import com.yihu.ehr.users.service.UserClient;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.http.HttpClientUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by yeshijie on 2017/2/14.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "TelVerification", description = "手机验证码接口管理", tags = {"基础信息 - 手机短信验证"})
public class TelVerificationController extends BaseController {

    @Autowired
    private AppClient appClient;
    @Autowired
    private UserClient userClient;

    @Autowired
    private TelVerificationClient telVerificationClient;

    @Value("${service-gateway.url}")
    private String gatewayUrl;

    @Value("${service-gateway.handlerId}")
    private String handlerId;

    @Value("${service-gateway.clientId}")
    private String clientId;
    @Value("${service-gateway.clientVersion}")
    private String clientVersion;

    /*
     * 1.1验证APPID的有效性
     * 1.2生成随机6位验证码，并存储
     * 1.3调用短信发送接口，进行短信的发送
     */
    @RequestMapping(value = "/msg/sendMsg", method = RequestMethod.GET)
    @ApiOperation(value = "验证码获取接口", notes = "根据手机号及应用的ID获取短信验证码信息")
    public Envelop sendMsg(
            @ApiParam(name = "appId", value = "应用ID")
            @RequestParam(value = "appId", required = false) String appId,
            @ApiParam(name = "tel", value = "手机号码", defaultValue = "")
            @RequestParam(value = "tel", required = false) String tel) throws Exception{
        Envelop envelop = new Envelop();
        //1.1验证APPID的有效性
        MApp mApp = appClient.getApp(appId);
        if(mApp == null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("对不起，你所使用的应用尚未进行注册。");
            return envelop;
        }
        //1.2生成随机6位验证码，并存储验证信息
        Envelop result = telVerificationClient.createTelVerification(tel, appId);
        if(result.isSuccessFlg()&& null !=result.getObj()){
            String code ="";
            if(null != ((LinkedHashMap) result.getObj()).get("verificationCode")){
                code = ((LinkedHashMap) result.getObj()).get("verificationCode").toString();
            }
            String api= "MsgGW.Sms.send";
            String content = "您好，短信验证码为:【" + code + "】，请在10分钟内验证！" ;
            String resultStr;
            Map<String,String> apiParamMap = new HashMap<>();
            //手机号码
            apiParamMap.put("mobile",tel);
            //业务标签
            apiParamMap.put("handlerId",handlerId);
            //短信内容
            apiParamMap.put("content",content);
            //渠道号
            apiParamMap.put("clientId",clientId);
            String apiParam = objectMapper.writeValueAsString(apiParamMap);
            //健康之路医疗基础信息接入参数AuthInfo
            Map<String,Object> params  = jkzlGateway( api, apiParamMap);
            String url= gatewayUrl;
            resultStr = HttpClientUtil.doPost(url, params);
            if(resultStr != null){
                Map<String,Object> resultMap = objectMapper.readValue(resultStr,Map.class);
                Integer resultCode =0;
                if(null != resultMap.get("Code") && !"".equals(resultMap.get("Code"))){
                    resultCode =Integer.valueOf(resultMap.get("Code").toString()) ;
                }
                if(resultCode == 10000){
                    envelop.setSuccessFlg(true);
                    envelop.setErrorMsg("短信验证码发送成功！");
                }else{
                    envelop.setSuccessFlg(true);
                    envelop.setErrorCode(resultCode);
                    envelop.setErrorMsg("短信验证码发送失败！");
                }
            }
        }
        return envelop;
    }

    /*
     * 1.1直接验证验证码的有效性
     */
    @RequestMapping(value = "/msg/validate", method = RequestMethod.GET)
    @ApiOperation(value = "验证码有效性验证", notes = "根据手机号及应用的ID获取短信验证码信息，并校验")
    public Envelop validate(
            @ApiParam(name = "appId", value = "应用ID")
            @RequestParam(value = "appId", required = false) String appId,
            @ApiParam(name = "tel", value = "手机号码", defaultValue = "")
            @RequestParam(value = "tel", required = false) String tel,
            @ApiParam(name = "verificationCode", value = "手机号码", defaultValue = "")
            @RequestParam(value = "verificationCode", required = false) String verificationCode) throws Exception{
        Envelop envelop = new Envelop();
        envelop = telVerificationClient.validationTelVerification(tel, appId,verificationCode);
        if(envelop == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("验证失败，请联系管理员！");
        }
        return  envelop;
    }

    /*
    * 1.1直接验证验证码的有效性
    * 1.2根据手机号获取用户信息
    * 1.3修改该用户的密码信息
    * 1.4删除验证码信息
    */
    @RequestMapping(value = "/users/updatePasswordByVerificationCode", method = RequestMethod.GET)
    @ApiOperation(value = "忘记密码 - 修改密码", notes = "根据验证码修改用户密码信息")
    public Envelop updatePasswordByVerificationCode(
            @ApiParam(name = "appId", value = "应用ID")
            @RequestParam(value = "appId", required = false) String appId,
            @ApiParam(name = "tel", value = "手机号码", defaultValue = "")
            @RequestParam(value = "tel", required = false) String tel,
            @ApiParam(name = "verificationCode", value = "验证码", defaultValue = "")
            @RequestParam(value = "verificationCode", required = false) String verificationCode,
            @ApiParam(name = "password", value = "新密码", defaultValue = "")
            @RequestParam(value = "password", required = false) String password) throws Exception{
        Envelop envelop = new Envelop();
        envelop = telVerificationClient.validationTelVerification(tel, appId,verificationCode);
        if(envelop == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("验证失败，请联系管理员！");
            return  envelop;
        }

        if(envelop.isSuccessFlg()){
            //获取用户信息，根据手机号码
            MUser mUser = userClient.getUserByTel(tel);
            if(mUser == null){
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("对不起，手机号码无法验证，请确认！");
                return  envelop;
            }

            String userId = mUser.getId().toString();
            //在微服务中会将该密码信息进行MD5加密
            Boolean result = userClient.changePassWord(userId, password);
            if(result){
                envelop.setSuccessFlg(true);
                envelop.setErrorMsg("密码修改成功！");
            }else{
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("密码修改失败，请联系管理员！");
            }
        }
        return  envelop;
    }

    /*
   * 1.1直接验证验证码的有效性
   * 1.2根据手机号获取用户信息
   * 1.3修改该用户的密码信息
   * 1.4删除验证码信息
   */
    @RequestMapping(value = "/users/updatePasswordByOldPwd", method = RequestMethod.GET)
    @ApiOperation(value = "修改密码", notes = "根据旧密码修改用户的密码信息")
    public Envelop updatePasswordByOldPwd(
            @ApiParam(name = "appId", value = "应用ID")
            @RequestParam(value = "appId", required = false) String appId,
            @ApiParam(name = "userId", value = "手机号码", defaultValue = "")
            @RequestParam(value = "userId", required = false) String userId,
            @ApiParam(name = "passwordOld", value = "验证码", defaultValue = "")
            @RequestParam(value = "passwordOld", required = false) String passwordOld,
            @ApiParam(name = "passwordNew", value = "新密码", defaultValue = "")
            @RequestParam(value = "passwordNew", required = false) String passwordNew) throws Exception{
        Envelop envelop = new Envelop();
        //获取用户信息，根据用户ID
        MUser mUser = userClient.getUser(userId);
        if(mUser == null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("对不起，该用户不存在，请确认！");
            return  envelop;
        }

        //对旧密码进行MD5加密后，进行对比验证
        String hashPassWordOld = DigestUtils.md5Hex(passwordOld);
        if(StringUtils.equals(hashPassWordOld,mUser.getPassword().toString())){
            //当验证通过后，进行新密码的更新在微服务中会将该密码信息进行MD5加密
            Boolean result = userClient.changePassWord(userId, passwordNew);
            if(result){
                envelop.setSuccessFlg(true);
                envelop.setErrorMsg("密码修改成功！");
            }else{
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("密码修改失败，请联系管理员！");
            }
        }else{
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("对不起，密码验证失败，请确认！");
        }

        return  envelop;
    }

    //拼接总部统一网关的参数。
    public Map<String,Object> jkzlGateway(String api,Map apiParam) throws Exception{
        //统一接口授权信息
        Map<String,String> authInfo = new HashMap<>();
        authInfo.put("ClientId",clientId);
        //接入方系统版本号
        authInfo.put("ClientVersion",clientVersion);
        authInfo.put("Sign","");
        authInfo.put("SessionKey","");
        ObjectMapper objectMapper = new ObjectMapper();
        String authInfoS = objectMapper.writeValueAsString(authInfo);
        String apiParamS = objectMapper.writeValueAsString(apiParam);

        Map<String, Object> params = new HashMap<>();
        params.put("AuthInfo",authInfoS);
        params.put("SequenceNo", DateUtil.getNowDate().toString());
        params.put("Api", api);
        params.put("Param", apiParamS);
        params.put("ParamType", 0);
        params.put("OutType",0);
        params.put("V", 1);

        return params;
    }

}
