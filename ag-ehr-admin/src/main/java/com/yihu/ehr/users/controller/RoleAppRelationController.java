package com.yihu.ehr.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.user.RoleAppRelationModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.user.MRoleAppRelation;
import com.yihu.ehr.users.service.RoleAppRelationClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.MediaType;
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
@Api(value = "roleApp",description = "应用角色-应用关系管理",tags = "")
public class RoleAppRelationController extends BaseController{
    @Autowired
    private RoleAppRelationClient roleAppRelationClient;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = ServiceApi.Roles.RoleApp,method = RequestMethod.POST)
    @ApiOperation(value = "新增应用-应用角色组关系")
    public  Envelop createRoleAppRelation(
            @ApiParam(name = "role_ids",value = "应用角色组ids,多个已逗号隔开")
            @RequestParam(value = "role_ids") String roleIds,
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId){
        boolean bo = roleAppRelationClient.createRoleAppRelation(roleIds, appId);
        if(bo){
            return success(null);
        }
        return failed("应用添加应用角色组失败！");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApp,method = RequestMethod.PUT)
    @ApiOperation(value = "修改应用-应用角色组关系")
    public Envelop updateRoleAppRelation(
            @ApiParam(name = "role_ids",value = "应用角色组ids,多个已逗号隔开")
            @RequestParam(value = "role_ids") String roleIds,
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId){
        boolean bo = roleAppRelationClient.updateRoleAppRelation(roleIds, appId);
        if(bo){
            return success(null);
        }
        return failed("修改应用的应用角色组失败！");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleAppId,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除角色组-应用关系")
    public Envelop deleteRoleApp(
            @ApiParam(name = "id",value = "角色组-应用关系id")
            @PathVariable(value = "id") long id){
        boolean bo = roleAppRelationClient.deleteRoleApp(id);
        if(bo){
            return success(null);
        }
        return failed("删除应用角色组失败！");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApps,method = RequestMethod.GET)
    @ApiOperation(value = "查询用户角色组-应用关系列表---分页")
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
            RoleAppRelationModel roleAppRelationModel = convertToModel(m, RoleAppRelationModel.class);
            roleAppRelationModels.add(roleAppRelationModel);
        }
        Integer totalCount = getTotalCount(responseEntity);
        return getResult(roleAppRelationModels,totalCount,page,size);
    }
    @RequestMapping(value = ServiceApi.Roles.RoleAppsNopage,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组-应用权限关系列表---不分页")
    public Envelop searchRoleAppNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        Envelop envelop = new Envelop();
        List<RoleAppRelationModel> roleAppRelationModels = new ArrayList<>();
        Collection<MRoleAppRelation> mRoleAppRelations = roleAppRelationClient.searchRoleAppNoPaging(filters);
        for (MRoleAppRelation m : mRoleAppRelations){
            RoleAppRelationModel roleAppRelationModel = convertToModel(m,RoleAppRelationModel.class);
            roleAppRelationModels.add(roleAppRelationModel);
        }
        envelop.setDetailModelList(roleAppRelationModels);
        return envelop;
    }
}
