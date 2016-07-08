package com.yihu.ehr.user.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.user.MRoleAppRelation;
import com.yihu.ehr.user.service.RoleAppRelation;
import com.yihu.ehr.user.service.RoleAppRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by yww on 2016/7/7.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "roleAppRelation",description = "应用角色管理")
public class RoleAppRelationEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private RoleAppRelationService roleAppRelationService;

    @RequestMapping(value = ServiceApi.Roles.RoleApp,method = RequestMethod.POST)
    @ApiOperation(value = "新增应用-应用角色组关系")
    public boolean createRoleAppRelation(
            @ApiParam(name = "role_ids",value = "应用角色组ids,多个已逗号隔开")
            @RequestParam(value = "role_ids") String roleIds,
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId){
        for(String roleId : roleIds.split(",")){
            RoleAppRelation relation = new RoleAppRelation();
            relation.setAppId(appId);
            relation.setRoleId(Long.parseLong(roleId));
            roleAppRelationService.save(relation);
        }
        return true;
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApp,method = RequestMethod.PUT)
    @ApiOperation(value = "修改应用-应用角色组关系")
    public boolean updateRoleAppRelation(
            @ApiParam(name = "role_ids",value = "应用角色组ids,多个已逗号隔开")
            @RequestParam(value = "role_ids") String roleIds,
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId) throws Exception{
        List<RoleAppRelation> roleAppRelations = roleAppRelationService.search("appId="+appId+";roleId<>" + roleIds);
        for(RoleAppRelation relation : roleAppRelations){
            roleAppRelationService.delete(relation.getId());
        }
        List<RoleAppRelation> roleAppRelationList = roleAppRelationService.search("appId="+appId+";roleId=" + roleIds);
        List<Long> ids = new ArrayList<>();
        for(RoleAppRelation roleAppRelation : roleAppRelationList){
            ids.add(roleAppRelation.getRoleId());
        }
        for(String roleId : roleIds.split(",")){
            if(ids.contains(Long.parseLong(roleId))){
                continue;
            }
            RoleAppRelation relation = new RoleAppRelation();
            relation.setAppId(appId);
            relation.setRoleId(Long.parseLong(roleId));
            roleAppRelationService.save(relation);
        }
        return true;
    }

    @RequestMapping(value = ServiceApi.Roles.RoleAppId,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除角色组-应用关系")
    public boolean deleteRoleApp(
            @ApiParam(name = "id",value = "角色组-应用关系id")
            @PathVariable(value = "id") long id){
        roleAppRelationService.delete(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApps,method = RequestMethod.GET)
    @ApiOperation(value = "查询用户角色组-应用关系列表---分页")
    public Collection<MRoleAppRelation> searchRoleApp(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,roleId,appId")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        page = reducePage(page);
        if (StringUtils.isEmpty(filters)) {
            Page<RoleAppRelation> roleAppRelationPage = roleAppRelationService.getRoleUserList(sorts, page, size);
            pagedResponse(request, response, roleAppRelationPage.getTotalElements(), page, size);
            return convertToModels(roleAppRelationPage.getContent(), new ArrayList<>(roleAppRelationPage.getNumber()), MRoleAppRelation.class, fields);
        } else {
            List<RoleAppRelation> roleAppRelations = roleAppRelationService.search(fields, filters, sorts, page, size);
            pagedResponse(request, response, roleAppRelationService.getCount(filters), page, size);
            return convertToModels(roleAppRelations, new ArrayList<MRoleAppRelation>(roleAppRelations.size()), MRoleAppRelation.class, fields);
        }
    }
    @RequestMapping(value = ServiceApi.Roles.RoleAppsNopage,method = RequestMethod.GET)
    @ApiOperation(value = "查询用户角色组-应用权限关系列表---不分页")
    public Collection<MRoleAppRelation> searchRoleAppNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        List<RoleAppRelation> roleAppRelations = roleAppRelationService.search(filters);
        return convertToModels(roleAppRelations,new ArrayList<MRoleAppRelation>(roleAppRelations.size()),MRoleAppRelation.class,"");
    }
}
