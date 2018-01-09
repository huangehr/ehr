package com.yihu.ehr.basic.user.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yihu.ehr.basic.user.entity.RoleReportRelation;
import com.yihu.ehr.basic.user.service.RoleReportRelationService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.user.MRoleReportRelation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by wxw on 2017/8/22.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "roleFeature", description = "角色与资源报表的授权关系", tags = {"安全管理-角色与资源报表的授权关系"})
public class RoleReportRelationEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private RoleReportRelationService roleReportRelationService;

    @RequestMapping(value = ServiceApi.Roles.DeleteRoleReportRelationByRoleId, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除角色与资源报表的授权关系")
    public Result deleteByRoleId(
            @ApiParam(name = "roleId", value = "角色Id", defaultValue = "")
            @RequestParam(value = "roleId") Long roleId) {
        roleReportRelationService.deleteByRoleId(roleId);
        return Result.success("角色与资源报表的授权关系删除成功！");
    }

    @RequestMapping(value = ServiceApi.Roles.BatchAddRoleReportRelation, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增&修改角色与资源报表的授权关系")
    public ObjectResult batchAddRoleReportRelation(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model) throws Exception {
        List<RoleReportRelation> list = objectMapper.readValue(model, new TypeReference<List<RoleReportRelation>>() {
        });
        if (list != null && list.size() > 0) {
            roleReportRelationService.deleteByRoleId(list.get(0).getRoleId());
        }
        for (int i = 0; i < list.size(); i++) {
            roleReportRelationService.save(list.get(i));
        }
        return Result.success("资源视图-关联指标表更新成功！", list);
    }

    @RequestMapping(value = ServiceApi.Roles.SearchRoleReportRelation, method = RequestMethod.GET)
    @ApiOperation(value = "查询角色与资源报表的授权关系列表---分页")
    public Collection<MRoleReportRelation> searchRoleReportRelation(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<MRoleReportRelation> roleFeatureRelations = roleReportRelationService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, roleReportRelationService.getCount(filters), page, size);
        return convertToModels(roleFeatureRelations, new ArrayList<MRoleReportRelation>(roleFeatureRelations.size()), MRoleReportRelation.class, fields);
    }

    @RequestMapping(value = ServiceApi.Roles.SearchRoleReportRelationNoPage, method = RequestMethod.GET)
    @ApiOperation(value = "查询角色与资源报表的授权关系列表（未分页）")
    public List<MRoleReportRelation> searchRoleReportRelationNoPage(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        List<MRoleReportRelation> list = roleReportRelationService.search(filters);
        return list;
    }

    @RequestMapping(value = ServiceApi.Roles.SearchRoleReportRelationIsReportAccredited, method = RequestMethod.GET)
    @ApiOperation(value = "判断资源报表是否已被授权")
    public boolean isReportAccredited(
            @ApiParam(name = "rsReportId", value = "资源报表ID", required = true)
            @RequestParam(value = "rsReportId") Integer rsReportId) {
        List<RoleReportRelation> list = roleReportRelationService.findByRsReportId((long) rsReportId);
        return list.size() == 0 ? false : true;
    }

}
