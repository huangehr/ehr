package com.yihu.ehr.patient.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

@FeignClient(name=MicroServices.Patient)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface PatientArchiveClient {

    @RequestMapping(value = ServiceApi.Patients.ArchiveApplyList,method = RequestMethod.GET)
    @ApiOperation(value = "个人档案认领列表")
    ListResult archiveApplyList(
            @ApiParam(name = "userId", value = "用户ID", defaultValue = "")
            @RequestParam(value = "userId", required = false) String userId,
            @ApiParam(name = "status", value = "审核状态 -1审核不通过 0待审核 1审核通过", defaultValue = "0")
            @RequestParam(value = "status",required = false) String status,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows", required = false) Integer rows);

    @RequestMapping(value = ServiceApi.Patients.ArchiveApply,method = RequestMethod.GET)
    @ApiOperation(value = "档案认领详情")
    ObjectResult getArchiveApply(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id", required = false) Long id);

    @RequestMapping(value = ServiceApi.Patients.ArchiveApply,method = RequestMethod.POST)
    @ApiOperation(value = "档案认领申请（临时卡）新增/修改")
    ObjectResult archiveApply(
            @ApiParam(name = "data", value = "json数据", defaultValue = "")
            @RequestBody String data);

    @RequestMapping(value = ServiceApi.Patients.ArchiveApply,method = RequestMethod.DELETE)
    @ApiOperation(value = "档案认领删除")
    Result archiveApplyDelete(
            @ApiParam(name = "id", value = "档案认领ID", defaultValue = "")
            @RequestParam(value = "id", required = false) Long id);

    @RequestMapping(value = ServiceApi.Patients.ArchiveApplyListManager,method = RequestMethod.GET)
    @ApiOperation(value = "管理员--档案认领列表")
    ListResult archiveApplyListManager(
            @ApiParam(name = "status", value = "审核状态 -1审核不通过 0待审核 1审核通过", defaultValue = "0")
            @RequestParam(value = "status", required = false) String status,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows", required = false) Integer rows);

    @RequestMapping(value = ServiceApi.Patients.ArchiveVerifyManager,method = RequestMethod.GET)
    @ApiOperation(value = "管理员--档案认领审核操作")
    Result archiveVerifyManager(
            @ApiParam(name = "id", value = "档案认领ID", defaultValue = "")
            @RequestParam(value = "id", required = false) Long id,
            @ApiParam(name = "status", value = "审核状态 -1审核不通过 0待审核 1审核通过", defaultValue = "")
            @RequestParam(value = "status", required = false) String status,
            @ApiParam(name = "auditor", value = "审核者", defaultValue = "")
            @RequestParam(value = "auditor", required = false) String auditor,
            @ApiParam(name = "auditReason", value = "审核不通过原因", defaultValue = "")
            @RequestParam(value = "auditReason",required = false) String auditReason,
            @ApiParam(name = "archiveRelationIds", value = "档案关联ID，多条用逗号分隔", defaultValue = "")
            @RequestParam(value = "archiveRelationIds", required = false) String archiveRelationIds);



    @RequestMapping(value = ServiceApi.Patients.ArchiveList,method = RequestMethod.GET)
    @ApiOperation(value = "个人档案列表")
    ListResult archiveList(
            @ApiParam(name = "idCardNo", value = "身份证号码", defaultValue = "")
            @RequestParam(value = "idCardNo", required = false) String idCardNo,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows", required = false) Integer rows);

    @RequestMapping(value = ServiceApi.Patients.ArchiveUnbind,method = RequestMethod.GET)
    @ApiOperation(value = "管理员--通过卡号获取未认领档案")
    ListResult archiveUnbind(
            @ApiParam(name = "cardNo", value = "就诊卡号", defaultValue = "")
            @RequestParam(value = "cardNo",required = false) String cardNo,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam(value = "page",required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows",required = false) Integer rows);

    @RequestMapping(value = ServiceApi.Patients.ArchiveRelation,method = RequestMethod.POST)
    @ApiOperation(value = "新建档案关联")
    ObjectResult archiveRelation(
            @ApiParam(name = "data", value = "json数据", defaultValue = "")
            @RequestBody String data);
}
