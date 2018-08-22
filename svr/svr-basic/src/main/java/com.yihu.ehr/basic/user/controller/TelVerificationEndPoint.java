package com.yihu.ehr.basic.user.controller;

import com.yihu.ehr.basic.apps.model.App;
import com.yihu.ehr.basic.apps.service.AppService;
import com.yihu.ehr.basic.fzopen.service.OpenService;
import com.yihu.ehr.basic.user.service.UserTelVerificationService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.user.UserTelVerification;
import com.yihu.ehr.util.datetime.DateUtil;
import com.yihu.ehr.util.id.RandomUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zlf
 * @version 1.0
 * @created 2015.08.10 17:57
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "TelVerification", description = "用户手机验证码管理", tags = {"用户管理"})
public class TelVerificationEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private UserTelVerificationService userTelVerificationService;
    @Autowired
    private AppService appService;
    @Autowired
    private OpenService fzOpenService;

    @Value("${fz-gateway.clientId}")
    private String fzClientId;
    @Value("${fz-gateway.handlerId}")
    private String fzHandlerId;

    @RequestMapping(value = ServiceApi.TelVerification.TelVerificationMsgSendMsg, method = RequestMethod.POST)
    @ApiOperation(value = "创建手机验证信息", notes = "创建手机验证信息")
    public Envelop createTelVerification(
            @ApiParam(name = "tel", value = "", defaultValue = "")
            @RequestParam(value = "tel", required = false) String tel,
            @ApiParam(name = "appId", value = "", defaultValue = "")
            @RequestParam(value = "appId", required = false) String appId) throws Exception {
        Envelop envelop = new Envelop();
        //1.1验证APPID的有效性
        App app = appService.retrieve(appId);
        if (app == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("对不起，你所使用的应用尚未进行注册。");
            return envelop;
        }
        RandomUtil randomUtil = new RandomUtil();
        String random = randomUtil.getRandomString(6);
        //当前日期加上10分钟，做为当前验证码的有效时间
        Date currentDate = DateUtil.getSysDate();
        Date effectivePeriod = new Date(currentDate.getTime() + 600000);
        UserTelVerification userTelVerification = new UserTelVerification();
        userTelVerification.setAppId(appId);
        userTelVerification.setTelNo(tel);
        userTelVerification.setEffectivePeriod(effectivePeriod);
        userTelVerification.setVerificationCode(random);
        userTelVerification = userTelVerificationService.save(userTelVerification);
        if (null != userTelVerification) {
            String code = random;
            String api = "MsgGW.Sms.send";
            String content = "尊敬的用户：欢迎使用健康上饶，您的验证码为:【" + code + "】,有效期10分钟，请尽快完成注册。若非本人操作，请忽略。";
            Map<String, Object> apiParamMap = new HashMap<>();
            //发送短信
            //手机号码
            apiParamMap.put("mobile", tel);
            //业务标签
            apiParamMap.put("handlerId", fzHandlerId);
            //短信内容
            apiParamMap.put("content", content);
            //渠道号
            apiParamMap.put("clientId", fzClientId);
            String resultStr = fzOpenService.callFzInnerApi(api,apiParamMap, 1);
            if (resultStr != null) {
                Map<String, Object> resultMap = objectMapper.readValue(resultStr, Map.class);
                Integer resultCode = 0;
                String msg="";
                if (null != resultMap.get("Code") && !"".equals(resultMap.get("Code"))) {
                    resultCode = Integer.valueOf(resultMap.get("Code").toString());
                }
                if (null != resultMap.get("Message") && !"".equals(resultMap.get("Message"))) {
                    msg = resultMap.get("Message").toString();
                }
                if (resultCode == 10000) {
                    envelop.setSuccessFlg(true);
                    envelop.setErrorMsg("短信验证码发送成功！");
                } else if(resultCode == -201){
                    envelop.setSuccessFlg(false);
                    envelop.setErrorCode(resultCode);
                    envelop.setErrorMsg("短信已达每天限制的次数（10次）！");
                }else if(resultCode == -200){
                    envelop.setSuccessFlg(false);
                    envelop.setErrorCode(resultCode);
                    envelop.setErrorMsg("短信发送频率太快（不能低于60s）！");
                }else{
                    envelop.setSuccessFlg(false);
                    envelop.setErrorCode(resultCode);
                    envelop.setErrorMsg("短信发送失败！" + msg);
                }
            }else{
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("短信验证码发送失败！");
            }
        }else{
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("短信验证码获取失败！");
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.TelVerification.TelVerificationMsgValidate, method = RequestMethod.GET)
    @ApiOperation(value = "验证手机验证信息", notes = "验证手机验证信息")
    public Envelop validationTelVerification(
            @ApiParam(name = "tel", value = "", defaultValue = "")
            @RequestParam(value = "tel", required = false) String tel,
            @ApiParam(name = "appId", value = "", defaultValue = "")
            @RequestParam(value = "appId", required = false) String appId,
            @ApiParam(name = "verificationCode", value = "", defaultValue = "")
            @RequestParam(value = "verificationCode", required = false) String verificationCode) throws Exception {

        Envelop envelop = new Envelop();
        Boolean result = userTelVerificationService.telValidation(tel,verificationCode,appId);
        if(result == null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("对不起，查不到匹配的验证码信息，请确认！");
        }else{
            if(result){
                envelop.setSuccessFlg(true);
                envelop.setErrorMsg("验证通过！");
            }else {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("对不起，验证未通过或者验证码已超时，请确认！");
            }
        }
        return envelop;
    }
}