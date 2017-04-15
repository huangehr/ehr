package com.yihu.ehr.user.controller;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.user.MRoleAppRelation;
import com.yihu.ehr.user.entity.RoleAppRelation;
import com.yihu.ehr.user.service.RoleAppRelationService;
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
 * Created by yww on 2016/7/7.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "roleApp",description = "角色组-应用关系管理", tags = {"安全管理-角色组-应用关系管理"})
public class RoleAppRelationEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private RoleAppRelationService roleAppRelationService;

    @RequestMapping(value = ServiceApi.Roles.RoleApp,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
     @ApiOperation(value = "为角色组配置应用，单个")
     public MRoleAppRelation createRoleAppRelation(
            @ApiParam(name = "data_json",value = "角色组-应用关系对象Json字符串")
            @RequestBody String dataJson){
        RoleAppRelation roleAppRelation = toEntity(dataJson,RoleAppRelation.class);
        String[] fields = {"appId","roleId"};
        String[] values = {roleAppRelation.getAppId(),roleAppRelation.getRoleId()+""};
        List<RoleAppRelation> roleAppRelations = roleAppRelationService.findByFields(fields, values);
        if(roleAppRelations != null && roleAppRelations.size() > 0){
            return convertToModel(roleAppRelations.get(0),MRoleAppRelation.class,null);
        }
        RoleAppRelation roleAppRelationNew = roleAppRelationService.save(roleAppRelation);
        return convertToModel(roleAppRelationNew,MRoleAppRelation.class,null);
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApp,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据角色组id,应用Id删除角色组-应用关系")
    public boolean deleteRoleApp(
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId,
            @ApiParam(name = "role_id",value = "角色组id")
            @RequestParam(value = "role_id") String roleId){
        RoleAppRelation relation = roleAppRelationService.findRelation(appId, Long.parseLong(roleId));
        if(null != relation){
            roleAppRelationService.delete(relation.getId());
        }
        return true;
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApps,method = RequestMethod.POST)
    @ApiOperation(value = "批量新增应用-角色组关系，一对多")
    public boolean batchCreateRoleAppRelation(
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId,
            @ApiParam(name = "role_ids",value = "应用角色组ids,多个用逗号隔开")
            @RequestParam(value = "role_ids") String roleIds) throws Exception{
        roleAppRelationService.batchCreateRoleAppRelation(appId,roleIds);
        return true;
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApps,method = RequestMethod.PUT)
    @ApiOperation(value = "批量修改应用-角色组关系，一对多")
    public boolean batchUpdateRoleAppRelation(
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId,
            @ApiParam(name = "role_ids",value = "应用角色组ids,多个用逗号隔开")
            @RequestParam(value = "role_ids") String roleIds) throws Exception{
        roleAppRelationService.batchUpdateRoleAppRelation(appId,roleIds);
        return true;
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApps,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组-应用关系列表---分页")
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
        List<RoleAppRelation> roleAppRelations = roleAppRelationService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, roleAppRelationService.getCount(filters), page, size);
        return convertToModels(roleAppRelations, new ArrayList<MRoleAppRelation>(roleAppRelations.size()), MRoleAppRelation.class, fields);
    }
    @RequestMapping(value = ServiceApi.Roles.RoleAppsNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组-应用关系列表---不分页")
    public Collection<MRoleAppRelation> searchRoleAppNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        List<RoleAppRelation> roleAppRelations = roleAppRelationService.search(filters);
        return convertToModels(roleAppRelations,new ArrayList<MRoleAppRelation>(roleAppRelations.size()),MRoleAppRelation.class,"");
    }
}
