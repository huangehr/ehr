package com.yihu.ehr.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.apps.model.App;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.patient.MedicalCards;
import com.yihu.ehr.model.patient.UserCards;
import com.yihu.ehr.patient.service.arapply.MedicalCardsService;
import com.yihu.ehr.patient.service.arapply.UserCardsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by hzp on 2017/04/01.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(description = "用户卡管理", tags = {"人口管理-就诊卡管理"})
public class PatientCardsEndPoint extends EnvelopRestEndPoint {

    @Autowired
    UserCardsService userCardsService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MedicalCardsService medicalCardsService;


    @RequestMapping(value = ServiceApi.Patients.CardList,method = RequestMethod.GET)
    @ApiOperation(value = "获取个人卡列表")
    public ListResult cardList(
            @ApiParam(name = "userId", value = "用户ID", defaultValue = "")
            @RequestParam(value = "userId",required = false) String userId,
            @ApiParam(name = "cardType", value = "卡类别", defaultValue = "")
            @RequestParam(value = "cardType",required = false) String cardType,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam(value = "page",required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows",required = false) Integer rows) throws Exception{

        return userCardsService.cardList(userId, cardType, page, rows);
    }

    @RequestMapping(value = ServiceApi.Patients.CardApply,method = RequestMethod.GET)
    @ApiOperation(value = "卡认证详情")
    public ObjectResult getCardApply(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id",required = false) Long id) throws Exception{
        UserCards card = userCardsService.getCardApply(id);
        return Result.success("获取卡认证详情成功！",card);
    }

    @RequestMapping(value = ServiceApi.Patients.CardApply,method = RequestMethod.POST)
    @ApiOperation(value = "卡认证申请新增/修改")
    public ObjectResult cardApply(
            @ApiParam(name = "data", value = "json数据", defaultValue = "")
            @RequestBody String data,
            @ApiParam(name = "operator", value = "操作者", defaultValue = "")
            @RequestParam(value = "operator",required = false) String operator) throws Exception{
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
            @RequestParam(value = "id",required = false) Long id) throws Exception{
        userCardsService.cardApplyDelete(id);
        return Result.success("卡认证申请删除成功！");
    }

    @RequestMapping(value = ServiceApi.Patients.CardApplyListManager,method = RequestMethod.GET)
    @ApiOperation(value = "管理员--卡认证列表")
    public ListResult cardApplyListManager(
            @ApiParam(name = "status", value = "卡状态 -1审核不通过 0待审核 1审核通过", defaultValue = "0")
            @RequestParam(value = "status",required = false) String status,
            @ApiParam(name = "cardType", value = "卡类别", defaultValue = "")
            @RequestParam(value = "cardType",required = false) String cardType,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam(value = "page",required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows",required = false) Integer rows) throws Exception{

        return userCardsService.cardApplyListManager(status, cardType, page, rows);
    }

    @RequestMapping(value = ServiceApi.Patients.CardVerifyManager,method = RequestMethod.POST)
    @ApiOperation(value = "管理员--卡认证审核操作")
    public Result cardVerifyManager(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id",required = false) Long id,
            @ApiParam(name = "status", value = "status", defaultValue = "")
            @RequestParam(value = "status",required = false) String status,
            @ApiParam(name = "auditor", value = "审核者", defaultValue = "")
            @RequestParam(value = "auditor",required = false) String auditor,
            @ApiParam(name = "auditReason", value = "审核不通过原因", defaultValue = "")
            @RequestParam(value = "auditReason",required = false) String auditReason) throws Exception{

        return userCardsService.cardVerifyManager(id,status,auditor,auditReason);
    }


    @RequestMapping(value = ServiceApi.Patients.CardBindManager,method = RequestMethod.POST)
    @ApiOperation(value = "管理员--后台绑卡操作")
    public ObjectResult cardBindManager(
            @ApiParam(name = "data", value = "json数据", defaultValue = "")
            @RequestBody String data,
            @ApiParam(name = "operator", value = "操作者", defaultValue = "")
            @RequestParam(value = "operator",required = false) String operator) throws Exception{
        UserCards card = objectMapper.readValue(data,UserCards.class);

        card.setStatus("1");
        card.setCreater(operator);
        card.setCreateDate(new Date());
        card.setAuditor(operator);
        card.setAuditDate(new Date());

        card = userCardsService.cardBindManager(card);
        return Result.success("后台绑卡成功！",card);
    }



    // ----------------------- 就诊卡基本信息管理 ------------------------------
    @RequestMapping(value = ServiceApi.Patients.GetMCards, method = RequestMethod.GET)
    @ApiOperation(value = "获取就诊卡列表信息")
    public Collection<MedicalCards> getMCards(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<MedicalCards> cards = medicalCardsService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, medicalCardsService.getCount(filters), page, size);
        return convertToModels(cards, new ArrayList<>(cards.size()), MedicalCards.class, fields);
    }

    @RequestMapping(value = ServiceApi.Patients.GetMCard,method = RequestMethod.GET)
    @ApiOperation(value = "就诊卡详情")
    public ObjectResult getMCard(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id",required = false) Long id) throws Exception{
        MedicalCards card = medicalCardsService.getMCard(id);
        return Result.success("获取卡认证详情成功！",card);
    }

    @RequestMapping(value = ServiceApi.Patients.MCardSave,method = RequestMethod.POST)
    @ApiOperation(value = "就诊卡新增/修改")
    public ObjectResult mCardSave(
            @ApiParam(name = "data", value = "json数据", defaultValue = "")
            @RequestBody String data,
            @ApiParam(name = "operator", value = "操作者", defaultValue = "")
            @RequestParam(value = "operator",required = false) String operator) throws Exception{
        MedicalCards card = objectMapper.readValue(data,MedicalCards.class);
        if(card.getId()==null || card.getId()==0 )
        {
            card.setCreater(operator);
            card.setCreateDate(new Date());
            card.setStatus("0");
        }else{
            card.setUpdater(operator);
            card.setUpdateDate(new Date());
        }
        card = medicalCardsService.save(card);

        return Result.success("保存就诊卡成功！",card);
    }

    @RequestMapping(value = ServiceApi.Patients.MCardDel,method = RequestMethod.DELETE)
    @ApiOperation(value = "就诊卡删除")
    public Result mCardDelete(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id",required = false) Long id) throws Exception{
        medicalCardsService.delete(id);
        return Result.success("就诊卡删除成功！");
    }

    @RequestMapping(value = ServiceApi.Patients.MCardCheckCardNo, method = RequestMethod.PUT)
    @ApiOperation(value = "校验卡是否唯一")
    int getCountByCardNo(
            @ApiParam(name = "cardNo", value = "卡号")
            @RequestParam(value = "cardNo", required = true) String cardNo){
        try {
            String filters = "cardNo="+cardNo;
            return (int)medicalCardsService.getCount(filters);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 1;
    };

}
