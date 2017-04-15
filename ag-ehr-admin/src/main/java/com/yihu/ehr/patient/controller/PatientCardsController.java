package com.yihu.ehr.patient.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.model.patient.MedicalCards;
import com.yihu.ehr.model.patient.UserCards;
import com.yihu.ehr.patient.service.PatientCardsClient;
import com.yihu.ehr.systemdict.service.SystemDictClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by AndyCai on 2016/1/21.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "card", description = "就诊卡管理及卡认领接口", tags = {"人口管理-就诊卡管理及卡认领接口"})
public class PatientCardsController extends ExtendController<UserCards> {

    @Autowired
    PatientCardsClient patientCardsClient;
    @Autowired
    SystemDictClient systemDictClient;

    @RequestMapping(value = ServiceApi.Patients.CardList,method = RequestMethod.GET)
    @ApiOperation(value = "获取个人卡列表")
    public Envelop cardList(
            @ApiParam(name = "userId", value = "用户ID", defaultValue = "")
            @RequestParam(value = "userId",required = false) String userId,
            @ApiParam(name = "cardType", value = "卡类别", defaultValue = "")
            @RequestParam(value = "cardType",required = false) String cardType,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam(value = "page",required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows",required = false) Integer rows) throws Exception{

        ListResult result = patientCardsClient.cardList(userId, cardType, page, rows);
        List<Map<String,Object>> list = result.getDetailModelList();
        list = convertCardModels(list);
        return getResult(list, result.getTotalCount(), result.getCurrPage(), result.getPageSize());
    }

    @RequestMapping(value = ServiceApi.Patients.CardApply,method = RequestMethod.GET)
    @ApiOperation(value = "卡认证详情")
    public Envelop getCardApply(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id",required = false) Long id) throws Exception{
        ObjectResult objectResult = patientCardsClient.getCardApply(id);
        if(objectResult.getData() != null){
            Map<String,Object> info = (HashMap)objectResult.getData();
            info = convertCardModel(info);
            return successObj(info);
        }
        return null;
    }

    @RequestMapping(value = ServiceApi.Patients.CardApply,method = RequestMethod.POST)
    @ApiOperation(value = "卡认证申请新增/修改")
    public Envelop cardApply(
            @ApiParam(name = "data", value = "json数据", defaultValue = "")
            @RequestBody String data,
            @ApiParam(name = "operator", value = "操作者", defaultValue = "")
            @RequestParam(value = "operator",required = false) String operator) throws Exception{
        ObjectResult objectResult = patientCardsClient.cardApply(data,operator);
        if(objectResult.getCode() == 200){
            return successObj(objectResult.getData());
        }else{
            return null;
        }
    }

    @RequestMapping(value = ServiceApi.Patients.CardApply,method = RequestMethod.DELETE)
    @ApiOperation(value = "卡认证申请删除")
    public Envelop cardApplyDelete(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id",required = false) Long id) throws Exception{
        Result result = patientCardsClient.cardApplyDelete(id);
        if(result.getCode() == 200){
            return successMsg(result.getMessage());
        }else{
            return failed("档案认领申请删除失败！");
        }
    }

    @RequestMapping(value = ServiceApi.Patients.CardApplyListManager,method = RequestMethod.GET)
    @ApiOperation(value = "管理员--卡认证列表")
    public Envelop cardApplyListManager(
            @ApiParam(name = "status", value = "卡状态 -1审核不通过 0待审核 1审核通过", defaultValue = "0")
            @RequestParam(value = "status",required = false) String status,
            @ApiParam(name = "cardType", value = "卡类别", defaultValue = "")
            @RequestParam(value = "cardType",required = false) String cardType,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam(value = "page",required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows",required = false) Integer rows) throws Exception{
        ListResult result = patientCardsClient.cardApplyListManager(status, cardType, page, rows);
        List<Map<String,Object>> list = result.getDetailModelList();
        list = convertCardModels(list);
        return getResult(list, result.getTotalCount(), result.getCurrPage(), result.getPageSize());
    }

    @RequestMapping(value = ServiceApi.Patients.CardVerifyManager,method = RequestMethod.POST)
    @ApiOperation(value = "管理员--卡认证审核操作")
    public Envelop cardVerifyManager(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id",required = false) Long id,
            @ApiParam(name = "status", value = "status", defaultValue = "")
            @RequestParam(value = "status",required = false) String status,
            @ApiParam(name = "auditor", value = "审核者", defaultValue = "")
            @RequestParam(value = "auditor",required = false) String auditor,
            @ApiParam(name = "auditReason", value = "审核不通过原因", defaultValue = "")
            @RequestParam(value = "auditReason",required = false) String auditReason) throws Exception{

        Result result = patientCardsClient.cardVerifyManager(id, status, auditor, auditReason);
        if (result.getCode() == 200){
            return successMsg(result.getMessage());
        }else{
            return failed(result.getMessage());
        }
    }

    @RequestMapping(value = ServiceApi.Patients.CardBindManager,method = RequestMethod.POST)
    @ApiOperation(value = "管理员--后台绑卡操作")
    public Envelop cardBindManager(
            @ApiParam(name = "data", value = "json数据", defaultValue = "")
            @RequestBody String data,
            @ApiParam(name = "operator", value = "操作者", defaultValue = "")
            @RequestParam(value = "operator",required = false) String operator) throws Exception{

        ObjectResult objectResult = patientCardsClient.cardBindManager(data,operator);
        if(objectResult.getCode() == 200){
            Map<String,Object> info = (HashMap)objectResult.getData();
            info = convertCardModel(info);
            return successObj(info);
        }else{
            return failed(objectResult.getMessage());
        }
    }

    // ------------------------------ 就诊卡基础信息管理 --------------------------------------

    @RequestMapping(value = ServiceApi.Patients.GetMCards, method = RequestMethod.GET)
    @ApiOperation(value = "获取就诊卡列表信息")
    public Envelop getMCards(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {

        List<MedicalCards> medicalCards = new ArrayList<>();
        ResponseEntity<List<MedicalCards>> responseEntity = patientCardsClient.getMCards(fields, filters, sorts, size, page);
        Integer totalCount = getTotalCount(responseEntity);
        if(totalCount > 0){
            medicalCards = responseEntity.getBody();
            Envelop envelop = getResult(medicalCards, totalCount, page, size);
            return envelop;
        }
        return null;
    }


    @RequestMapping(value = ServiceApi.Patients.GetMCard,method = RequestMethod.GET)
    @ApiOperation(value = "就诊卡详情")
    public Envelop getMCard(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id",required = false) Long id) throws Exception{

        ObjectResult objectResult = patientCardsClient.getMCard(id);
        if(objectResult.getData() != null){
            Map<String,Object> info = (HashMap)objectResult.getData();
            info = convertCardModel(info);
            return successObj(info);
        }
        return null;
    }

    @RequestMapping(value = ServiceApi.Patients.MCardSave,method = RequestMethod.POST)
    @ApiOperation(value = "就诊卡新增/修改")
    public Envelop mCardSave(
            @ApiParam(name = "data", value = "json数据", defaultValue = "")
            @RequestBody String data,
            @ApiParam(name = "operator", value = "操作者", defaultValue = "")
            @RequestParam(value = "operator",required = false) String operator) throws Exception{
        ObjectResult objectResult = patientCardsClient.mCardSave(data,operator);
        if(objectResult.getCode() == 200){
            return successObj(objectResult.getData());
        }else{
            return null;
        }
    }

    @RequestMapping(value = ServiceApi.Patients.MCardDel,method = RequestMethod.DELETE)
    @ApiOperation(value = "卡认证申请删除")
    public Envelop mCardDel(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id",required = false) Long id) throws Exception{
        Result result = patientCardsClient.cardApplyDelete(id);
        if(result.getCode() == 200){
            return successMsg(result.getMessage());
        }else{
            return failed("档案认领申请删除失败！");
        }
    }

    private Map<String,Object> convertCardModel(Map<String,Object> userCard){
        List<MDictionaryEntry> statusDicts = systemDictClient.getDictEntries("", "dictId=43", "", 10, 1).getBody();
        Map<String, String> statusMap = new HashMap<>();
        for(MDictionaryEntry entry : statusDicts){
            statusMap.put(entry.getCode(), entry.getValue());
        }
        userCard.put("statusName",statusMap.get(userCard.get("status")));

        return userCard;
    }
    private List convertCardModels(List<Map<String,Object>> userCards){
        List<MDictionaryEntry> statusDicts = systemDictClient.getDictEntries("", "dictId=43", "", 10, 1).getBody();
        Map<String, String> statusMap = new HashMap<>();
        for(MDictionaryEntry entry : statusDicts){
            statusMap.put(entry.getCode(), entry.getValue());
        }
        for(Map<String,Object> info : userCards){
            info.put("statusName",statusMap.get(info.get("status")));
        }

        return userCards;
    }


}
