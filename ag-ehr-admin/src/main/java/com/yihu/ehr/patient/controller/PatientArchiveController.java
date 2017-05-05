package com.yihu.ehr.patient.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.patient.ArchiveApply;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.patient.service.PatientArchiveClient;
import com.yihu.ehr.systemdict.service.SystemDictClient;
import com.yihu.ehr.users.service.UserClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(protocols = "https", value = "archive_apply", description = "居民档案申领关联接口" , tags = {"人口管理-居民档案申领关联接口"})
public class PatientArchiveController extends ExtendController<ArchiveApply> {
    String appId = "ag-admin";

    @Autowired
    PatientArchiveClient patientArchiveClient;
    @Autowired
    SystemDictClient systemDictClient;
    @Autowired
    UserClient userClient;

    @RequestMapping(value = ServiceApi.Patients.GetArchiveList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询档案列表(arApply)")
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

        ListResult listResult = patientArchiveClient.getApplyList(fields, filters, sorts, size, page);
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> list = listResult.getDetailModelList();
            list = convertArApplyModels(list);
            return getResult(list, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else{
            Envelop envelop = new Envelop();
            return envelop;
        }
    }

    @RequestMapping(value = ServiceApi.Patients.GetArchiveRelationList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询档案关联列表(arRelation)")
    public Envelop getArRelationList(
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

        ListResult listResult = patientArchiveClient.getArRelationList(fields, filters, sorts, size, page);
        if(listResult.getTotalCount() != 0){
            List<Map<String,Object>> list = listResult.getDetailModelList();
            list = convertArRelaModels(list);
            return getResult(list, listResult.getTotalCount(), listResult.getCurrPage(), listResult.getPageSize());
        }else{
            Envelop envelop = new Envelop();
            return envelop;
        }
    }

    @RequestMapping(value = ServiceApi.Patients.GetArchiveRelation,method = RequestMethod.GET)
    @ApiOperation(value = "档案关联详情")
    public Envelop getArRelation(
            @ApiParam(name = "applyId", value = "applyId", defaultValue = "")
            @PathVariable(value = "applyId") Long applyId) throws Exception{
        ObjectResult objectResult = patientArchiveClient.getArRelation(applyId);
        if(objectResult.getData() != null){
            Map<String,Object> info = (HashMap)objectResult.getData();
            info = convertArRelaModel(info);
            return successObj(info);
        }
        return null;
    }


    @RequestMapping(value = ServiceApi.Patients.ArchiveApplyList,method = RequestMethod.GET)
    @ApiOperation(value = "居民查看提交的档案申请列表")
    public Envelop archiveApplyList(
            @ApiParam(name = "userId", value = "用户ID", defaultValue = "0dae00035715c8906db7084cdb79f56b")
            @RequestParam String userId,
            @ApiParam(name = "status", value = "审核状态 -1审核不通过 0待审核 1审核通过", defaultValue = "1")
            @RequestParam(required = false) String status,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "0")
            @RequestParam Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "15")
            @RequestParam Integer rows) {
        ListResult result = patientArchiveClient.archiveApplyList(userId,status,page,rows);
        List<Map<String,Object>> list = result.getDetailModelList();
        list = convertArApplyModels(list);
        return getResult(list, result.getTotalCount(), result.getCurrPage(), result.getPageSize());
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveApply,method = RequestMethod.GET)
    @ApiOperation(value = "档案认领信息详情")
    public Envelop getArchiveApply(
            @ApiParam(name = "id", value = "id", defaultValue = "1")
            @RequestParam(value = "id", required = false) Long id) throws Exception{
        ObjectResult objectResult = patientArchiveClient.getArchiveApply(id);
        if(objectResult.getData() != null){
            Map<String,Object> info = (HashMap)objectResult.getData();
            info = convertArApplyModel(info);
            return successObj(info);
        }
        return null;
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveApply,method = RequestMethod.POST)
    @ApiOperation(value = "档案认领申请（临时卡）新增/修改")
    public Envelop archiveApply(
            @ApiParam(name = "data", value = "json数据", defaultValue = "")
            @RequestBody String data) throws Exception{
        ObjectResult objectResult = patientArchiveClient.archiveApply(data);
        if(objectResult.getCode() == 200){
            return successObj(objectResult.getData());
        }else{
            return null;
        }
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveApply,method = RequestMethod.DELETE)
    @ApiOperation(value = "档案认领删除")
    public Envelop archiveApplyDelete(
            @ApiParam(name = "id", value = "档案认领ID", defaultValue = "")
            @RequestParam(value = "id", required = false) Long id) throws Exception{

        Result result = patientArchiveClient.archiveApplyDelete(id);
        if(result.getCode() == 200){
            return successMsg(result.getMessage());
        }else{
            return failed("档案认领申请删除失败！");
        }
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveApplyListManager,method = RequestMethod.GET)
    @ApiOperation(value = "管理员--档案认领列表")
    public Envelop archiveApplyListManager(
            @ApiParam(name = "status", value = "审核状态 -1审核不通过 0待审核 1审核通过", defaultValue = "0")
            @RequestParam(value = "status", required = false) String status,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows", required = false) Integer rows) throws Exception{

        ListResult result = patientArchiveClient.archiveApplyListManager(status, page, rows);
        List<Map<String,Object>> list = result.getDetailModelList();
        list = convertArApplyModels(list);
        return getResult(list, result.getTotalCount(), result.getCurrPage(), result.getPageSize());
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveVerifyManager,method = RequestMethod.GET)
    @ApiOperation(value = "管理员--档案认领审核操作")
    public Envelop archiveVerifyManager(
            @ApiParam(name = "id", value = "档案认领ID", defaultValue = "")
            @RequestParam(value = "id", required = false) Long id,
            @ApiParam(name = "status", value = "审核状态 2 审核不通过 0待审核 1审核通过", defaultValue = "")
            @RequestParam(value = "status", required = false) String status,
            @ApiParam(name = "auditor", value = "审核者", defaultValue = "")
            @RequestParam(value = "auditor", required = false) String auditor,
            @ApiParam(name = "auditReason", value = "审核不通过原因", defaultValue = "")
            @RequestParam(value = "auditReason",required = false) String auditReason,
            @ApiParam(name = "archiveRelationIds", value = "档案关联ID，多条用逗号分隔", defaultValue = "")
            @RequestParam(value = "archiveRelationIds", required = false) String archiveRelationIds) throws Exception{
        Result result = patientArchiveClient.archiveVerifyManager(id,status, auditor,auditReason,archiveRelationIds);
        if(result.getCode() == 200){
            return successMsg(result.getMessage());
        }else{
            return failed(result.getMessage());
        }
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveList,method = RequestMethod.GET)
    @ApiOperation(value = "个人档案列表")
    public Envelop archiveList(
            @ApiParam(name = "idCardNo", value = "身份证号码", defaultValue = "")
            @RequestParam(value = "idCardNo", required = false) String idCardNo,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows", required = false) Integer rows) throws Exception{

        ListResult result = patientArchiveClient.archiveList(idCardNo, page, rows);
        List<Map<String,Object>> list = result.getDetailModelList();
        list = convertArRelaModels(list);
        return getResult(list, result.getTotalCount(), result.getCurrPage(), result.getPageSize());
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveUnbind,method = RequestMethod.GET)
    @ApiOperation(value = "管理员--通过卡号获取未认领档案")
    public Envelop archiveUnbind(
            @ApiParam(name = "cardNo", value = "就诊卡号", defaultValue = "")
            @RequestParam(value = "cardNo",required = false) String cardNo,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam(value = "page",required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows",required = false) Integer rows) throws Exception{

        ListResult result = patientArchiveClient.archiveUnbind(cardNo, page, rows);
        List<Map<String,Object>> list = result.getDetailModelList();
        list = convertArRelaModels(list);
        return getResult(list, result.getTotalCount(), result.getCurrPage(), result.getPageSize());
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveRelation,method = RequestMethod.POST)
    @ApiOperation(value = "新建档案关联")
    public Envelop archiveRelation(
            @ApiParam(name = "data", value = "json数据", defaultValue = "")
            @RequestBody String data) throws Exception{
        ObjectResult objectResult = patientArchiveClient.archiveRelation(data);
        if(objectResult.getCode() == 200){
            return successObj(objectResult.getData());
        }else{
            return null;
        }
    }

    private Map<String,Object>  convertArRelaModel(Map<String,Object> ArRela){
        List<MDictionaryEntry>  eventTypeDicts = systemDictClient.getDictEntries("", "dictId=64", "", 10, 1).getBody();
        Map<String, String>  eventTypeMap = new HashMap<>();
        for(MDictionaryEntry entry : eventTypeDicts){
            eventTypeMap.put(entry.getCode(), entry.getValue());
        }

        List<MDictionaryEntry>  relaStatusDicts = systemDictClient.getDictEntries("", "dictId=65", "", 10, 1).getBody();
        Map<String, String> relaStatusMap = new HashMap<>();
        for(MDictionaryEntry entry : relaStatusDicts){
            relaStatusMap.put(entry.getCode(), entry.getValue());
        }

        ArRela.put("eventTypeName",eventTypeMap.get(ArRela.get("eventType")));
        ArRela.put("statusName",relaStatusMap.get(ArRela.get("status")));

        return ArRela;
    }
    private List convertArRelaModels(List<Map<String,Object>> ArRelaL){
        List<MDictionaryEntry>  eventTypeDicts = systemDictClient.getDictEntries("", "dictId=64", "", 10, 1).getBody();
        Map<String, String>  eventTypeMap = new HashMap<>();
        for(MDictionaryEntry entry : eventTypeDicts){
            eventTypeMap.put(entry.getCode(), entry.getValue());
        }

        List<MDictionaryEntry>  relaStatusDicts = systemDictClient.getDictEntries("", "dictId=65", "", 10, 1).getBody();
        Map<String, String> relaStatusMap = new HashMap<>();
        for(MDictionaryEntry entry : relaStatusDicts){
            relaStatusMap.put(entry.getCode(), entry.getValue());
        }

        List<MDictionaryEntry>  cardTypeDicts = systemDictClient.getDictEntries("", "dictId=66", "", 10, 1).getBody();
        Map<String, String>  cardTypeMap = new HashMap<>();
        for(MDictionaryEntry entry : cardTypeDicts){
            cardTypeMap.put(entry.getCode(), entry.getValue());
        }

        for(Map<String,Object> ArRela : ArRelaL){
            ArRela.put("eventTypeName",eventTypeMap.get(ArRela.get("eventType")));
            ArRela.put("statusName",relaStatusMap.get(ArRela.get("status")));
            ArRela.put("cardTypeName",cardTypeMap.get(ArRela.get("cardType")));
        }

        return ArRelaL;
    }
    private Map<String,Object>  convertArApplyModel(Map<String,Object> arApp){
        List<MDictionaryEntry> statusDicts = systemDictClient.getDictEntries("", "dictId=36", "", 10, 1).getBody();
        Map<String, String> statusMap = new HashMap<>();
        for(MDictionaryEntry entry : statusDicts){
            statusMap.put(entry.getCode(), entry.getValue());
        }
        arApp.put("statusName",statusMap.get(arApp.get("status")));

        return arApp;
    }
    private List convertArApplyModels(List<Map<String,Object>> arApplies){
        List<MDictionaryEntry> statusDicts = systemDictClient.getDictEntries("", "dictId=36", "", 10, 1).getBody();
        Map<String, String> statusMap = new HashMap<>();
        for(MDictionaryEntry entry : statusDicts){
            statusMap.put(entry.getCode(), entry.getValue());
        }
        for(Map<String,Object> info : arApplies){
            if(info.get("auditor") != null){
                MUser user = userClient.getUser(info.get("auditor").toString());
                if(user!=null){
                    info.put("auditor",user.getRealName());
                }
            }
            info.put("statusName",statusMap.get(info.get("status")));
        }
        return arApplies;
    }

}
