package com.yihu.ehr.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.patient.MAbstractCard;
import com.yihu.ehr.model.patient.UserCards;
import com.yihu.ehr.patient.service.UserCardsService;
import com.yihu.ehr.patient.service.card.AbstractCard;
import com.yihu.ehr.patient.service.card.CardManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by hzp on 2017/04/01.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api("用户卡管理")
public class PatientCardsEndPoint extends EnvelopRestEndPoint {

    @Autowired
    UserCardsService userCardsService;

    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping(value = ServiceApi.Patients.CardList,method = RequestMethod.GET)
    @ApiOperation(value = "获取个人卡列表")
    public Result cardList(
            @ApiParam(name = "userId", value = "用户ID", defaultValue = "")
            @RequestParam String userId,
            @ApiParam(name = "cardType", value = "卡类别", defaultValue = "")
            @RequestParam(value = "cardType",required = false) String cardType,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam Integer rows) throws Exception{

        return userCardsService.cardList(userId, cardType, page, rows);
    }

    @RequestMapping(value = ServiceApi.Patients.CardApply,method = RequestMethod.GET)
    @ApiOperation(value = "卡认证详情")
    public Result getCardApply(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam Long id) throws Exception{
        UserCards card = userCardsService.getCardApply(id);
        return Result.success("获取卡认证详情成功！",card);
    }

    @RequestMapping(value = ServiceApi.Patients.CardApply,method = RequestMethod.POST)
    @ApiOperation(value = "卡认证申请新增/修改")
    public Result cardApply(
            @ApiParam(name = "data", value = "json数据", defaultValue = "")
            @RequestBody String data,
            @ApiParam(name = "operator", value = "操作者", defaultValue = "")
            @RequestParam String operator) throws Exception{
        UserCards card = objectMapper.readValue(data,UserCards.class);
        //新增
        if(card.getId()==null)
        {
            card.setCreater(operator);
            card.setCreateDate(new Date());
        }
        else{
            card.setUpdater(operator);
            card.setUpdateDate(new Date());
        }
        card.setStatus("0");

        card = userCardsService.cardApply(card);

        return Result.success("保存卡认证申请成功！",card);
    }

    @RequestMapping(value = ServiceApi.Patients.CardApply,method = RequestMethod.DELETE)
    @ApiOperation(value = "卡认证申请删除")
    public Result cardApplyDelete(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam Long id) throws Exception{
        userCardsService.cardApplyDelete(id);
        return Result.success("卡认证申请删除成功！");
    }

    @RequestMapping(value = ServiceApi.Patients.CardApplyListManager,method = RequestMethod.GET)
    @ApiOperation(value = "管理员--卡认证列表")
    public Result cardApplyListManager(
            @ApiParam(name = "status", value = "卡状态 -1审核不通过 0待审核 1审核通过", defaultValue = "0")
            @RequestParam(required = false) String status,
            @ApiParam(name = "cardType", value = "卡类别", defaultValue = "")
            @RequestParam(required = false) String cardType,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam Integer rows) throws Exception{

        return userCardsService.cardApplyListManager(status, cardType, page, rows);
    }

    @RequestMapping(value = ServiceApi.Patients.CardVerifyManager,method = RequestMethod.POST)
    @ApiOperation(value = "管理员--卡认证审核操作")
    public Result cardVerifyManager(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam Long id,
            @ApiParam(name = "status", value = "status", defaultValue = "")
            @RequestParam String status,
            @ApiParam(name = "auditor", value = "审核者", defaultValue = "")
            @RequestParam String auditor,
            @ApiParam(name = "auditReason", value = "审核不通过原因", defaultValue = "")
            @RequestParam(required = false) String auditReason) throws Exception{

        return userCardsService.cardVerifyManager(id,status,auditor,auditReason);
    }


    @RequestMapping(value = ServiceApi.Patients.CardBindManager,method = RequestMethod.POST)
    @ApiOperation(value = "管理员--后台绑卡操作")
    public Result cardBindManager(
            @ApiParam(name = "data", value = "json数据", defaultValue = "")
            @RequestBody String data,
            @ApiParam(name = "operator", value = "操作者", defaultValue = "")
            @RequestParam String operator) throws Exception{
        UserCards card = objectMapper.readValue(data,UserCards.class);

        card.setStatus("1");
        card.setCreater(operator);
        card.setCreateDate(new Date());
        card.setAuditor(operator);
        card.setAuditDate(new Date());

        card = userCardsService.cardBindManager(card);
        return Result.success("后台绑卡成功！",card);
    }

}
