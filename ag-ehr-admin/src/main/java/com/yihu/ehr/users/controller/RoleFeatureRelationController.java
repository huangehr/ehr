package com.yihu.ehr.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.user.RoleFeatureRelationModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.user.MRoleFeatureRelation;
import com.yihu.ehr.users.service.RoleFeatureRelationClient;
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
@Api(value = "roleUser",description = "角色组应用权限配置",tags = "")
public class RoleFeatureRelationController extends BaseController {
    @Autowired
    private RoleFeatureRelationClient roleFeatureRelationClient;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = ServiceApi.Roles.RoleFeature,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为角色组添加权限")
    public Envelop createRoleFeature(
            @ApiParam(name = "data_json",value = "角色组-权限关系Json串")
            @RequestBody String dataJson){
        MRoleFeatureRelation mRoleFeatureRelation = roleFeatureRelationClient.createRoleFeature(dataJson);
        if(null == mRoleFeatureRelation){
            return failed("添加应用权限失败！");
        }
        return success(null);
    }
    @RequestMapping(value = ServiceApi.Roles.RoleFeatureId,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除角色组的权限")
    public Envelop deleteRoleFeature(
            @ApiParam(name = "id",value = "角色组-权限关系id")
            @PathVariable long id){
        boolean bo = roleFeatureRelationClient.deleteRoleFeature(id);
        if(bo){
            return success(null);
        }
        return failed("删除应用权限失败！");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleFeatures,method = RequestMethod.GET)
    @ApiOperation(value = "查询用户角色组-权限关系列表---分页")
    public Envelop searchRoleFeature(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,roleId,featureId")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception{
        List<RoleFeatureRelationModel> relationModels = new ArrayList<>();
        ResponseEntity<Collection<MRoleFeatureRelation>> responseEntity = roleFeatureRelationClient.searchRoleFeature(fields, filters, sorts, size, page);
        Collection<MRoleFeatureRelation> mRoleFeatureRelations  = responseEntity.getBody();
        for (MRoleFeatureRelation m : mRoleFeatureRelations){
            RoleFeatureRelationModel roleFeatureRelationModel = changeToModel(m);
            relationModels.add(roleFeatureRelationModel);
        }
        Integer totalCount = getTotalCount(responseEntity);
        return getResult(relationModels,totalCount,page,size);
    }
    @RequestMapping(value = ServiceApi.Roles.RoleFeaturesNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询用户角色组-应用权限关系列表---不分页")
    public Envelop searchRoleFeatureNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        Envelop envelop = new Envelop();
        List<RoleFeatureRelationModel> relationModels = new ArrayList<>();
        Collection<MRoleFeatureRelation> mRoleFeatureRelations = roleFeatureRelationClient.searchRoleFeatureNoPaging(filters);
        for (MRoleFeatureRelation m : mRoleFeatureRelations){
            RoleFeatureRelationModel roleFeatureRelationModel = changeToModel(m);
            relationModels.add(roleFeatureRelationModel);
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(relationModels);
        return envelop;
    }

    private RoleFeatureRelationModel changeToModel(MRoleFeatureRelation m) {
        RoleFeatureRelationModel model = convertToModel(m, RoleFeatureRelationModel.class);
        //获取应用权限名称
        return model;
    }
}
