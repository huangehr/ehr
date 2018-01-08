package com.yihu.ehr.basic.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.basic.patient.service.arapply.ArchiveApplyService;
import com.yihu.ehr.basic.patient.service.arapply.ArchiveRelationService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.patient.ArchiveApply;
import com.yihu.ehr.entity.patient.ArchiveRelation;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;


/**
 * Created by hzp on 2017/04/01.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(description = "用户档案认领管理", tags = {"人口管理-用户档案认领管理"})
public class PatientArchiveEndPoint extends EnvelopRestEndPoint {

    @Autowired
    ArchiveApplyService archiveApplyService;

    @Autowired
    ArchiveRelationService archiveRelationService;

    @Autowired
    ObjectMapper objectMapper;

    @RequestMapping(value = ServiceApi.Patients.GetArchiveList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询档案列表（arApply）")
    public ListResult getApplyList(
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

        ListResult listResult = new ListResult();
        List<ArchiveApply> applyList = archiveApplyService.search(fields, filters, sorts, page, size);
        if(applyList != null){
            listResult.setDetailModelList(applyList);
            listResult.setTotalCount((int)archiveApplyService.getCount(filters));
            listResult.setCode(200);
            listResult.setCurrPage(page);
            listResult.setPageSize(size);
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }

    @RequestMapping(value = ServiceApi.Patients.GetArchiveRelationList, method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询档案关联列表(arRelation)")
    public ListResult getArRelationList(
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

        ListResult listResult = new ListResult();
        List<ArchiveRelation> arRelationList = archiveRelationService.search(fields, filters, sorts, page, size);
        if(arRelationList != null){
            listResult.setDetailModelList(arRelationList);
            listResult.setTotalCount((int)archiveRelationService.getCount(filters));
            listResult.setCode(200);
            listResult.setCurrPage(page);
            listResult.setPageSize(size);
        }else{
            listResult.setCode(200);
            listResult.setMessage("查询无数据");
            listResult.setTotalCount(0);
        }
        return listResult;
    }

    @RequestMapping(value = ServiceApi.Patients.GetArchiveRelation,method = RequestMethod.GET)
    @ApiOperation(value = "档案关联详情")
    public ObjectResult getArRelation(
            @ApiParam(name = "applyId", value = "applyId", defaultValue = "")
            @PathVariable(value = "applyId") long applyId) throws Exception{
        ObjectResult obj = archiveRelationService.getArRelation(applyId);
        return Result.success("获取档案关联详情成功！",obj);
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveApplyList,method = RequestMethod.GET)
    @ApiOperation(value = "个人档案认领列表")
    public ListResult archiveApplyList(
            @ApiParam(name = "userId", value = "用户ID", defaultValue = "")
            @RequestParam(value = "userId", required = false) String userId,
            @ApiParam(name = "status", value = "审核状态 -1审核不通过 0待审核 1审核通过", defaultValue = "0")
            @RequestParam(value = "status",required = false) String status,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows", required = false) Integer rows) throws Exception{

        return archiveApplyService.archiveApplyList(userId, status, page, rows);
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveApply,method = RequestMethod.GET)
    @ApiOperation(value = "档案认领详情")
    public ObjectResult getArchiveApply(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @RequestParam(value = "id", required = false) Long id) throws Exception{
        ArchiveApply obj = archiveApplyService.getArchiveApply(id);
        return Result.success("获取档案认领详情成功！",obj);
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveApply,method = RequestMethod.POST)
    @ApiOperation(value = "档案认领申请（临时卡）新增/修改")
    public ObjectResult archiveApply(
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
            @RequestParam(value = "id", required = false) Long id) throws Exception{

        archiveApplyService.archiveApplyDelete(id);
        return Result.success("档案认领删除成功！");
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveApplyListManager,method = RequestMethod.GET)
    @ApiOperation(value = "管理员--档案认领列表")
    public ListResult archiveApplyListManager(
            @ApiParam(name = "status", value = "审核状态 -1审核不通过 0待审核 1审核通过", defaultValue = "0")
            @RequestParam(value = "status", required = false) String status,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows", required = false) Integer rows) throws Exception{

        return archiveApplyService.archiveApplyListManager(status, page, rows);
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveVerifyManager,method = RequestMethod.GET)
    @ApiOperation(value = "管理员--档案认领审核操作")
    public Result archiveVerifyManager(
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

        return archiveApplyService.archiveVerifyManager(id,status, auditor,auditReason,archiveRelationIds);
    }



    @RequestMapping(value = ServiceApi.Patients.ArchiveList,method = RequestMethod.GET)
    @ApiOperation(value = "个人档案列表")
    public ListResult archiveList(
            @ApiParam(name = "idCardNo", value = "身份证号码", defaultValue = "")
            @RequestParam(value = "idCardNo", required = false) String idCardNo,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows", required = false) Integer rows) throws Exception{

        return archiveRelationService.archiveList(idCardNo, page, rows);
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveUnbind,method = RequestMethod.GET)
    @ApiOperation(value = "管理员--通过卡号获取未认领档案")
    public ListResult archiveUnbind(
            @ApiParam(name = "cardNo", value = "就诊卡号", defaultValue = "")
            @RequestParam(value = "cardNo",required = false) String cardNo,
            @ApiParam(name = "page", value = "当前页（从0开始）", defaultValue = "")
            @RequestParam(value = "page",required = false) Integer page,
            @ApiParam(name = "rows", value = "行数", defaultValue = "")
            @RequestParam(value = "rows",required = false) Integer rows) throws Exception{

        return archiveRelationService.archiveUnbind(cardNo, page, rows);
    }

    @RequestMapping(value = ServiceApi.Patients.ArchiveRelation,method = RequestMethod.POST)
    @ApiOperation(value = "新建档案关联")
    public ObjectResult archiveRelation(
            @ApiParam(name = "data", value = "json数据", defaultValue = "")
            @RequestBody String data) throws Exception{

        ArchiveRelation relation = objectMapper.readValue(data,ArchiveRelation.class);

        archiveRelationService.archiveRelation(relation);

        return ObjectResult.success("新建档案关联成功！", relation);
    }

}
