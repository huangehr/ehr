package com.yihu.ehr.basic.portal.controller;

import com.yihu.ehr.basic.portal.model.PortalAccountRepresentation;
import com.yihu.ehr.basic.portal.service.PortalAccountRepresentationService;
import com.yihu.ehr.basic.util.CaptchaUtil;
import com.yihu.ehr.basic.util.IdcardValidator;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.util.http.RandomValidateCode;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.util.validate.IdCardValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by zhangdan on 2018/4/19.
 */

@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "account", description = "账号相关业务", tags = {"医生工作平台-账号相关业务"})
public class PortalAccountRepresentationController extends EnvelopRestEndPoint {

    @Autowired
    private PortalAccountRepresentationService portalAccountRepresentationService;

    @PostMapping(value = ServiceApi.AccountRepresentation.SaveAccontRepresenetation)
    @ApiOperation(value = "保存新增账号申诉",notes = "保存新增账号申诉")
    public Result saveAccountRepresentation(
            @ApiParam(name = "type",value = "申诉类型 1手机号码变更 2重置密码 3其他")@RequestParam(value = "type",required = true)String type,
            @ApiParam(name = "content",value = "申诉说明")@RequestParam(value = "content",required = true)String content,
            @ApiParam(name = "photo",value = "申诉照片，多个逗号隔开")@RequestParam(value = "photo",required = false)String photo,
            @ApiParam(name = "createName",value = "申诉人姓名")@RequestParam(value = "createName",required = true)String createName,
            @ApiParam(name = "idCard",value = "申诉人身份证")@RequestParam(value = "idCard",required = true)String idCard,
            @ApiParam(name = "mobile",value = "申诉人电话")@RequestParam(value = "mobile",required = true)String mobile){
        IdcardValidator idcardValidator = new IdcardValidator();
        if (!idcardValidator.isValidatedAllIdcard(idCard)){
            return Result.error("身份证号码不正确！");
        }

        boolean flag = portalAccountRepresentationService.saveAccountRepresentation(type,content,photo,createName,idCard,mobile);
        if (flag){
            return Result.success("申诉成功！");
        }else {
            return Result.error("申诉失败！");
        }
    }

    @GetMapping(value = ServiceApi.AccountRepresentation.GetRandomImageCode)
    @ApiOperation(value = "修改密码时生成图形验证码",notes = "修改密码时生成图形验证码")
    public void getImageCode (HttpServletRequest request, HttpServletResponse response)throws Exception{
        try {
            response.setContentType("image/jpeg");//设置相应类型,告诉浏览器输出的内容为图片
            response.setHeader("Pragma", "No-cache");//设置响应头信息，告诉浏览器不要缓存此内容
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);
            RandomValidateCode randomValidateCode = new RandomValidateCode();
            randomValidateCode.getRandcode(request, response);//输出验证码图片方法
        } catch (Exception e) {
            logger.error("获取验证码失败>>>>   ", e);
        }

    }

    @PostMapping(value = ServiceApi.AccountRepresentation.CheckRandomImageCode)
    @ApiOperation(value = "检验验证码",notes = "检验验证码")
    public Result checkImageCode(@ApiParam(name = "code",value = "输入的验证码")@RequestParam(value = "code",required = true)String code,
                                 HttpServletRequest request){
        if (StringUtils.isEmpty(code)){
            return Result.error("请输入验证码！");
        }
        String codeRescource = String.valueOf(request.getSession().getAttribute(RandomValidateCode.RANDOMCODEKEY));
        if (code.toLowerCase().equals(codeRescource.toLowerCase())){
            request.getSession().removeAttribute(RandomValidateCode.RANDOMCODEKEY);
            return Result.success("验证码正确！");
        }else {
            return Result.error("验证码错误！");
        }
    }
}
