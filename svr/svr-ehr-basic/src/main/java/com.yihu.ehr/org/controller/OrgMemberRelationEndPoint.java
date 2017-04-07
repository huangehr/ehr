package com.yihu.ehr.org.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.org.MOrgMemberRelation;
import com.yihu.ehr.org.model.OrgMemberRelation;
import com.yihu.ehr.org.service.OrgMemberRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HZY
 * @vsrsion 1.0
 * Created at 2017/2/16.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "orgDeptMember", description = "组织机构部门成员管理服务", tags = {"部门成员管理"})
public class OrgMemberRelationEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private OrgMemberRelationService relationService;

    @RequestMapping(value = "/orgDeptMember/getAllOrgDeptMember", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "查询所有成员列表")
    public List<MOrgMemberRelation> getAllOrgDeptMember() throws Exception {
        List<OrgMemberRelation> orgMemberRelations = relationService.search(null);
        return (List<MOrgMemberRelation>) convertToModels(orgMemberRelations, new ArrayList<MOrgMemberRelation>(orgMemberRelations.size()), MOrgMemberRelation.class, null);
    }


    @RequestMapping(value = "/orgDeptMember/list", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "根据条件 查询部门下成员列表")
    public List<MOrgMemberRelation> searchOrgDeptMembers(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,deptId,deptName,dutyName,userName")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "+userName,+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        List<OrgMemberRelation> orgMemRelations = relationService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, relationService.getCount(filters), page, size);
        return (List<MOrgMemberRelation>) convertToModels(orgMemRelations, new ArrayList<MOrgMemberRelation>(orgMemRelations.size()), MOrgMemberRelation.class, fields);
    }


    @RequestMapping(value ="orgDeptMember/admin/{orgDept_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取部门成员信息")
    public MOrgMemberRelation getMessageRemindInfo(
            @ApiParam(name = "orgDept_id", value = "", defaultValue = "")
            @PathVariable(value = "orgDept_id") Long orgDeptId) {
        OrgMemberRelation orgMemberRelation = relationService.getOrgMemberRelation(orgDeptId);
        MOrgMemberRelation mOrgMemberRelation   = convertToModel(orgMemberRelation, MOrgMemberRelation.class);
        return mOrgMemberRelation;
    }


    @RequestMapping(value = "/orgDeptMember/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增部门成员")
    public MOrgMemberRelation saveOrgDeptMember(
            @ApiParam(name = "memberRelationJsonData", value = "新增部门成员信息")
            @RequestBody String memberRelationJsonData
    ) throws Exception {
        OrgMemberRelation memberRelation = toEntity(memberRelationJsonData, OrgMemberRelation.class);
        memberRelation.setStatus(0);
        relationService.save(memberRelation);
        return convertToModel(memberRelation, MOrgMemberRelation.class);
    }

    @RequestMapping(value = "/orgDeptMember/update", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改部门成员")
    public MOrgMemberRelation updateOrgDeptMember(
            @ApiParam(name = "memberRelationJsonData", value = "修改部门成员信息")
            @RequestBody String memberRelationJsonData
    ) throws Exception {
        OrgMemberRelation memberRelation = toEntity(memberRelationJsonData, OrgMemberRelation.class);
        relationService.save(memberRelation);
        return convertToModel(memberRelation, MOrgMemberRelation.class);
    }

    @RequestMapping(value = "/orgDeptMember/updateStatus", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改部门成员状态" )
    public boolean updateStatusOrgDeptMember(
            @ApiParam(name = "memberRelationId", value = "部门成员ID")
            @RequestParam(value = "memberRelationId", required = true) Integer memberRelationId,
            @ApiParam(name = "status", value = "状态", defaultValue = "")
            @RequestParam(value = "status") int status
    ) throws Exception {
        relationService.updateStatusDeptMember(memberRelationId, status);
        return true;
    }

    @RequestMapping(value = "/orgDeptMember/delete", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "删除部门成员" )
    public boolean deleteOrgDeptMember(
            @ApiParam(name = "memberRelationId", value = "部门成员ID")
            @RequestParam(value = "memberRelationId", required = true) Integer memberRelationId
    ) throws Exception {
        relationService.deleteDeptMember(memberRelationId);
        return true;
    }



}
