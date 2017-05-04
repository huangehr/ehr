package com.yihu.ehr.patient.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.patient.MedicalCards;
import com.yihu.ehr.model.patient.UserCards;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@FeignClient(name=MicroServices.Patient)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface PatientCardsClient {

    @RequestMapping(value = ServiceApi.Patients.GetUserCards, method = RequestMethod.GET)
    @ApiOperation(value = "获取用户卡关联列表信息")
    ResponseEntity<List<UserCards>> getUserCardList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);


    @RequestMapping(value = ServiceApi.Patients.CardList,method = RequestMethod.GET)
    @ApiOperation(value = "获取个人卡列表")
    ListResult cardList(
            @ApiParam(name = "userId", value = "用户ID", defaultValue = "")
            @RequestParam(value = "userId",required = false) String userId,
            @ApiParam(name = "cardType", value = "卡类别", defaultValue = "")
            @RequestParam(value = "cardType",required = false) String cardType,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam(value = "page",required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows",required = false) Integer rows);

    @RequestMapping(value = ServiceApi.Patients.CardApply,method = RequestMethod.GET)
    @ApiOperation(value = "卡认证详情")
    ObjectResult getCardApply(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id",required = false) Long id);

    @RequestMapping(value = ServiceApi.Patients.CardApply,method = RequestMethod.POST)
    @ApiOperation(value = "卡认证申请新增/修改")
    ObjectResult cardApply(
            @ApiParam(name = "data", value = "json数据", defaultValue = "")
            @RequestBody String data,
            @ApiParam(name = "operator", value = "操作者", defaultValue = "")
            @RequestParam(value = "operator",required = false) String operator);

    @RequestMapping(value = ServiceApi.Patients.CardApply,method = RequestMethod.DELETE)
    @ApiOperation(value = "卡认证申请删除")
    Result cardApplyDelete(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id",required = false) Long id);

    @RequestMapping(value = ServiceApi.Patients.CardApplyListManager,method = RequestMethod.GET)
    @ApiOperation(value = "管理员--卡认证列表")
    ListResult cardApplyListManager(
            @ApiParam(name = "status", value = "卡状态 -1审核不通过 0待审核 1审核通过", defaultValue = "0")
            @RequestParam(value = "status",required = false) String status,
            @ApiParam(name = "cardType", value = "卡类别", defaultValue = "")
            @RequestParam(value = "cardType",required = false) String cardType,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam(value = "page",required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows",required = false) Integer rows);

    @RequestMapping(value = ServiceApi.Patients.CardVerifyManager,method = RequestMethod.POST)
    @ApiOperation(value = "管理员--卡认证审核操作")
    ObjectResult cardVerifyManager(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id",required = false) Long id,
            @ApiParam(name = "status", value = "status", defaultValue = "")
            @RequestParam(value = "status",required = false) String status,
            @ApiParam(name = "auditor", value = "审核者", defaultValue = "")
            @RequestParam(value = "auditor",required = false) String auditor,
            @ApiParam(name = "auditReason", value = "审核不通过原因", defaultValue = "")
            @RequestParam(value = "auditReason",required = false) String auditReason);

    @RequestMapping(value = ServiceApi.Patients.ArchiveRelationManager,method = RequestMethod.GET)
    @ApiOperation(value = "管理员--卡与档案关联审核操作")
    Result archiveRelationManager(
            @ApiParam(name = "cardId", value = "cardId", defaultValue = "")
            @RequestParam(value = "cardId",required = false) Long cardId,
            @ApiParam(name = "archiveRelationIds", value = "档案关联ID，多条用逗号分隔", defaultValue = "")
            @RequestParam(value = "archiveRelationIds", required = false) String archiveRelationIds);



    @RequestMapping(value = ServiceApi.Patients.CardBindManager,method = RequestMethod.POST)
    @ApiOperation(value = "管理员--后台绑卡操作")
    ObjectResult cardBindManager(
            @ApiParam(name = "data", value = "json数据", defaultValue = "")
            @RequestBody String data,
            @ApiParam(name = "operator", value = "操作者", defaultValue = "")
            @RequestParam(value = "operator",required = false) String operator);


    @RequestMapping(value = ServiceApi.Patients.GetMCards, method = RequestMethod.GET)
    @ApiOperation(value = "获取就诊卡列表信息")
    ResponseEntity<List<MedicalCards>> getMCards(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);


    @RequestMapping(value = ServiceApi.Patients.GetMCard,method = RequestMethod.GET)
    @ApiOperation(value = "就诊卡详情")
    ObjectResult getMCard(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id",required = false) Long id);

    @RequestMapping(value = ServiceApi.Patients.MCardSave,method = RequestMethod.POST)
    @ApiOperation(value = "就诊卡新增/修改")
    ObjectResult mCardSave(
            @ApiParam(name = "data", value = "json数据", defaultValue = "")
            @RequestBody String data,
            @ApiParam(name = "operator", value = "操作者", defaultValue = "")
            @RequestParam(value = "operator",required = false) String operator);

    @RequestMapping(value = ServiceApi.Patients.MCardDel,method = RequestMethod.DELETE)
    @ApiOperation(value = "就诊卡删除")
    Result mCardDelete(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id",required = false) Long id);


    @RequestMapping(value = ServiceApi.Patients.MCardCheckCardNo, method = RequestMethod.PUT)
    @ApiOperation(value = "校验卡是否唯一")
    int getCountByCardNo(
            @ApiParam(name = "cardNo", value = "卡号")
            @RequestParam(value = "cardNo", required = true) String cardNo
    );

    @RequestMapping(value = ServiceApi.Patients.MCardGetMutiCardNo, method = RequestMethod.PUT)
    @ApiOperation(value = "查询导入时重复卡列表")
    List<MedicalCards> getBycardNoStr(
            @ApiParam(name = "cardNoStr", value = "卡号字符串")
            @RequestParam(value = "cardNoStr", required = true) String cardNoStr
    );


    @RequestMapping(value = ServiceApi.Patients.MCarddataBatch, method = RequestMethod.POST)
    @ApiOperation("批量创建就诊卡")
    boolean createPatientCardsPatch(
            @RequestBody String medicalCars,
            @ApiParam(name = "operator", value = "操作者", defaultValue = "")
            @RequestParam(value = "operator",required = false) String operator);




    @RequestMapping(value = ServiceApi.Patients.FindArchiveRelation,method = RequestMethod.POST)
    @ApiOperation(value = "档案关联详情")
    ObjectResult findArchiveRelation(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id",required = false) Long id);

    @RequestMapping(value = ServiceApi.Patients.UpdateArchiveRelation,method = RequestMethod.POST)
    @ApiOperation(value = "档案关联新增/修改")
    ObjectResult updateArchiveRelation(
            @ApiParam(name = "data", value = "", defaultValue = "")
            @RequestBody String portalNoticeJsonData);

    @RequestMapping(value =  ServiceApi.Patients.DelArchiveRelation, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除档案关联", notes = "根据档案关联id")
    Result delArchiveRelation(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id",required = false) Long id);
    
    
}
