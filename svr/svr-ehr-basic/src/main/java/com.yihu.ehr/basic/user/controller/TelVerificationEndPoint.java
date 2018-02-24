package com.yihu.ehr.basic.user.controller;

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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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

    @RequestMapping(value = ServiceApi.TelVerification.TelVerification, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建手机验证信息", notes = "创建手机验证信息")
    public Envelop createTelVerification(
            @ApiParam(name = "telNo", value = "", defaultValue = "")
            @RequestParam(value = "telNo", required = false) String telNo,
            @ApiParam(name = "appId", value = "", defaultValue = "")
            @RequestParam(value = "appId", required = false) String appId) throws Exception {
        Envelop envelop = new Envelop();
        RandomUtil randomUtil = new RandomUtil();
        String random = randomUtil.getRandomString(6);
        //当前日期加上10分钟，做为当前验证码的有效时间
        Date currentDate = DateUtil.getSysDate();
        Date effectivePeriod = new Date(currentDate.getTime() + 600000);
        UserTelVerification userTelVerification = new UserTelVerification();

        userTelVerification.setAppId(appId);
        userTelVerification.setTelNo(telNo);
        userTelVerification.setEffectivePeriod(effectivePeriod);
        userTelVerification.setVerificationCode(random);

        userTelVerification = userTelVerificationService.save(userTelVerification);

        if(userTelVerification != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(userTelVerification);

            return envelop;
        }
        return null;
    }

    @RequestMapping(value = ServiceApi.TelVerification.TelVerification, method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "验证手机验证信息", notes = "验证手机验证信息")
    public Envelop validationTelVerification(
            @ApiParam(name = "telNo", value = "", defaultValue = "")
            @RequestParam(value = "telNo", required = false) String telNo,
            @ApiParam(name = "appId", value = "", defaultValue = "")
            @RequestParam(value = "appId", required = false) String appId,
            @ApiParam(name = "verificationCode", value = "", defaultValue = "")
            @RequestParam(value = "verificationCode", required = false) String verificationCode) throws Exception {

        Envelop envelop = new Envelop();
        Boolean result = userTelVerificationService.telValidation(telNo,verificationCode,appId);
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