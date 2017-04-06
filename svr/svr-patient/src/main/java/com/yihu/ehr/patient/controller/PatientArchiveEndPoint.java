package com.yihu.ehr.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.patient.ArchiveApply;
import com.yihu.ehr.model.patient.UserCards;
import com.yihu.ehr.patient.service.ArchiveApplyService;
import com.yihu.ehr.patient.service.ArchiveRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;


/**
 * Created by hzp on 2017/04/01.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api("用户档案认领管理")
public class PatientArchiveEndPoint extends EnvelopRestEndPoint {

    @Autowired
    ArchiveApplyService archiveApplyService;

    @Autowired
    ArchiveRelationService archiveRelationService;

    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping(value = ServiceApi.Patients.ArchiveApplyList,method = RequestMethod.GET)
    @ApiOperation(value = "个人档案认领列表")
    public Result archiveApplyList(
            @ApiParam(name = "userId", value = "用户ID", defaultValue = "")
            @RequestParam String userId,
            @ApiParam(name = "status", value = "审核状态 -1审核不通过 0待审核 1审核通过", defaultValue = "0")
            @RequestParam(required = false) String status,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam Integer rows) throws Exception{

        return archiveApplyService.archiveApplyList(userId, status, page, rows);
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveApply,method = RequestMethod.GET)
    @ApiOperation(value = "档案认领详情")
    public Result getArchiveApply(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam Long id) throws Exception{
        ArchiveApply obj = archiveApplyService.getArchiveApply(id);
        return Result.success("获取档案认领详情成功！",obj);
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveApply,method = RequestMethod.POST)
    @ApiOperation(value = "档案认领申请（临时卡）新增/修改")
    public Result archiveApply(
            @ApiParam(name = "data", value = "json数据", defaultValue = "")
            @RequestBody String data) throws Exception{
        ArchiveApply obj = objectMapper.readValue(data,ArchiveApply.class);
        obj.setStatus("0");
        obj.setApplyDate(new Date());
        obj = archiveApplyService.archiveApply(obj);

        return Result.success("档案认领申请成功！",obj);
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveApply,method = RequestMethod.DELETE)
    @ApiOperation(value = "档案认领删除")
    public Result archiveApplyDelete(
            @ApiParam(name = "id", value = "档案认领ID", defaultValue = "")
            @RequestParam Long id) throws Exception{

        archiveApplyService.archiveApplyDelete(id);
        return Result.success("档案认领删除成功！");
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveApplyListManager,method = RequestMethod.GET)
    @ApiOperation(value = "管理员--档案认领列表")
    public Result archiveApplyListManager(
            @ApiParam(name = "status", value = "审核状态 -1审核不通过 0待审核 1审核通过", defaultValue = "0")
            @RequestParam(required = false) String status,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam Integer rows) throws Exception{

        return archiveApplyService.archiveApplyListManager(status, page, rows);
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveVerifyManager,method = RequestMethod.GET)
    @ApiOperation(value = "管理员--档案认领审核操作")
    public Result archiveVerifyManager(
            @ApiParam(name = "id", value = "档案认领ID", defaultValue = "")
            @RequestParam Long id,
            @ApiParam(name = "status", value = "审核状态 -1审核不通过 0待审核 1审核通过", defaultValue = "")
            @RequestParam String status,
            @ApiParam(name = "auditor", value = "审核者", defaultValue = "")
            @RequestParam String auditor,
            @ApiParam(name = "auditReason", value = "审核不通过原因", defaultValue = "")
            @RequestParam(required = false) String auditReason,
            @ApiParam(name = "archiveRelationId", value = "档案关联ID", defaultValue = "")
            @RequestParam Long archiveRelationId) throws Exception{


        return archiveApplyService.archiveVerifyManager(id,status, auditor,auditReason,archiveRelationId);
    }



    @RequestMapping(value = ServiceApi.Patients.ArchiveList,method = RequestMethod.GET)
    @ApiOperation(value = "个人档案列表")
    public Result archiveList(
            @ApiParam(name = "idCardNo", value = "身份证号码", defaultValue = "")
            @RequestParam String idCardNo,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam Integer rows) throws Exception{

        return archiveRelationService.archiveList(idCardNo,page, rows);
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveUnbind,method = RequestMethod.GET)
    @ApiOperation(value = "管理员--通过卡号获取未认领档案")
    public Result archiveUnbind(
            @ApiParam(name = "cardNo", value = "就诊卡号", defaultValue = "")
            @RequestParam(required = false) String cardNo,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam Integer rows) throws Exception{

        return archiveRelationService.archiveUnbind(cardNo,page, rows);
    }



}
