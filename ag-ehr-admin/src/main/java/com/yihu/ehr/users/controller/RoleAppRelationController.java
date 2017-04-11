package com.yihu.ehr.users.controller;

import com.yihu.ehr.agModel.user.RoleAppRelationModel;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.apps.service.AppClient;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.model.user.MRoleAppRelation;
import com.yihu.ehr.model.user.MRoles;
import com.yihu.ehr.users.service.RoleAppRelationClient;
import com.yihu.ehr.users.service.RolesClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by yww on 2016/7/7.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0+"/admin")
@RestController
@Api(value = "roleApp",description = "角色组-应用关系管理",tags = "")
public class RoleAppRelationController extends BaseController{
    @Autowired
    private RoleAppRelationClient roleAppRelationClient;

    @Autowired
    private AppClient appClient;

    @Autowired
    private RolesClient rolesClient;

    @RequestMapping(value = ServiceApi.Roles.RoleApp,method = RequestMethod.POST)
    @ApiOperation(value = "为角色组配置应用，单个--单个")
    public Envelop createRoleAppRelation(
            @ApiParam(name = "data_json",value = "角色组-应用关系对象Json字符串")
            @RequestParam(value = "data_json") String dataJson){
        MRoleAppRelation mRoleAppRelation = roleAppRelationClient.createRoleAppRelation(dataJson);
        if(mRoleAppRelation != null){
            return success(convertToModel(mRoleAppRelation,RoleAppRelationModel.class,null));
        }
        return failed("新增失败");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApp,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据角色组id,应用Id删除角色组-应用关系")
    public Envelop deleteRoleApp(
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId,
            @ApiParam(name = "role_id",value = "角色组id")
            @RequestParam(value = "role_id") String roleId){
        if(StringUtils.isEmpty(appId)){
            return failed("应用id不能为空！");
        }
        if(StringUtils.isEmpty(roleId)){
            return failed("角色组id不能为空！");
        }
        boolean bo = roleAppRelationClient.deleteRoleApp(appId, roleId);
        if(bo){
            return success(null);
        }
        return failed("删除角色组失败！");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApps,method = RequestMethod.POST)
    @ApiOperation(value = "批量新增应用-角色组关系，一对多")
    public Envelop batchCreateRoleAppRelation(
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId,
            @ApiParam(name = "role_ids",value = "角色组ids,多个用逗号隔开")
            @RequestParam(value = "role_ids") String roleIds) throws Exception{
        if(StringUtils.isEmpty(appId)){
            return failed("应用id不能为空！");
        }
        if(StringUtils.isEmpty(roleIds)){
            return failed("角色组id不能为空！");
        }
        boolean bo = roleAppRelationClient.batchCreateRoleAppRelation(appId,roleIds);
        if(bo){
            return success(null);
        }
        return failed("新增失败！");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApps,method = RequestMethod.PUT)
    @ApiOperation(value = "批量修改应用-角色组关系，一对多")
    public Envelop batchUpdateRoleAppRelation(
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId,
            @ApiParam(name = "role_ids",value = "角色组ids,多个用逗号隔开")
            @RequestParam(value = "role_ids") String roleIds) throws Exception{
        if(StringUtils.isEmpty(appId)){
            return failed("应用id不能为空！");
        }
        if(StringUtils.isEmpty(roleIds)){
            return failed("角色组id不能为空！");
        }
        boolean bo = roleAppRelationClient.batchUpdateRoleAppRelation(appId,roleIds);
        if(bo){
            return success(null);
        }
        return failed("修改失败！");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApps,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组-应用关系列表---分页")
    public Envelop searchRoleApp(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,roleId,appId")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception{
        List<RoleAppRelationModel> roleAppRelationModels = new ArrayList<>();
        ResponseEntity<Collection<MRoleAppRelation>> responseEntity = roleAppRelationClient.searchRoleApp(fields, filters, sorts, size, page);
        Collection<MRoleAppRelation> mRoleAppRelations  = responseEntity.getBody();
        for (MRoleAppRelation m : mRoleAppRelations){
            RoleAppRelationModel roleAppRelationModel = changeToModel(m);
            roleAppRelationModels.add(roleAppRelationModel);
        }
        Integer totalCount = getTotalCount(responseEntity);
        return getResult(roleAppRelationModels,totalCount,page,size);
    }
    @RequestMapping(value = ServiceApi.Roles.RoleAppsNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组-应用关系列表---不分页")
    public Envelop searchRoleAppNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        Envelop envelop = new Envelop();
        List<RoleAppRelationModel> roleAppRelationModels = new ArrayList<>();
        Collection<MRoleAppRelation> mRoleAppRelations = roleAppRelationClient.searchRoleAppNoPaging(filters);
        for (MRoleAppRelation m : mRoleAppRelations){
            RoleAppRelationModel roleAppRelationModel = changeToModel(m);
            roleAppRelationModels.add(roleAppRelationModel);
        }
        envelop.setDetailModelList(roleAppRelationModels);
        return envelop;
    }
    private RoleAppRelationModel changeToModel(MRoleAppRelation m) {
        RoleAppRelationModel model = convertToModel(m, RoleAppRelationModel.class);
        //获取应用权限名称
        MApp app = appClient.getApp(m.getAppId());
        model.setAppName(app == null ? "" : app.getName());
        //获取角色名称
        MRoles roles = rolesClient.getRolesById(m.getRoleId());
        model.setRoleName(roles == null?"":roles.getName());
        return model;
    }
}
