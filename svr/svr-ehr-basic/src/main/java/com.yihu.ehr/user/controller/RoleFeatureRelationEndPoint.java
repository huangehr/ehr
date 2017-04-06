package com.yihu.ehr.user.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.user.MRoleFeatureRelation;
import com.yihu.ehr.user.entity.RoleFeatureRelation;
import com.yihu.ehr.user.service.RoleFeatureRelationService;
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
@Api(value = "roleFeature",description = "角色组功能权限配置")
public class RoleFeatureRelationEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private RoleFeatureRelationService roleFeatureRelationService;

    @RequestMapping(value = ServiceApi.Roles.RoleFeature,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为角色组配置功能权限，单个")
    public MRoleFeatureRelation createRoleFeature(
            @ApiParam(name = "data_json",value = "角色组-功能权限关系Json串")
            @RequestBody String dataJson){
        RoleFeatureRelation roleFeatureRelation = toEntity(dataJson,RoleFeatureRelation.class);
        RoleFeatureRelation roleFeatureRelationNew = roleFeatureRelationService.save(roleFeatureRelation);
        return convertToModel(roleFeatureRelationNew,MRoleFeatureRelation.class);
    }
    @RequestMapping(value = ServiceApi.Roles.RoleFeature,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据角色组id,权限Id删除角色组-功能权限关系")
    public boolean deleteRoleFeature(
            @ApiParam(name = "feature_id",value = "功能权限id")
            @RequestParam(value = "feature_id") String featureId,
            @ApiParam(name = "role_id",value = "角色组id")
            @RequestParam(value = "role_id") String roleId){
        RoleFeatureRelation relation = roleFeatureRelationService.findRelation(Long.parseLong(featureId), Long.parseLong(roleId));
        if(null != relation){
            roleFeatureRelationService.delete(relation.getId());
        }
        return true;
    }

    @RequestMapping(value = ServiceApi.Roles.RoleFeatureByRoleId,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据角色组id删除所配置的应用权限")
    public boolean deleteRoleFeatureRelationByRoleId(
            @ApiParam(name = "role_id",value = "角色组id")
            @RequestParam(value = "role_id") Long roleId){
        return  roleFeatureRelationService.deleteRoleFeatureRelationByRoleId(roleId);
    }

    @RequestMapping(value = ServiceApi.Roles.RoleFeatures,method = RequestMethod.PUT)
    @ApiOperation(value = "批量修改角色组-应用权限关系，一对多")
    public boolean batchUpdateRoleFeatureRelation(
            @ApiParam(name = "role_id",value = "角色组Id")
            @RequestParam(value = "role_id") Long roleId,
            @ApiParam(name = "feature_ids_add",value = "要新增的featureIds",defaultValue = "")
            @RequestParam(name = "feature_ids_add",required = false) Long[] addFeatureIds,
            @ApiParam(name = "feature_ids_delete",value = "要删除的featureIds",defaultValue = "")
            @RequestParam(value = "feature_ids_delete",required = false) String deleteFeatureIds) throws Exception{
        roleFeatureRelationService.batchUpdateRoleFeatureRelation(roleId,addFeatureIds,deleteFeatureIds);
        return true;
    }

    @RequestMapping(value = ServiceApi.Roles.RoleFeatures,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组-功能权限关系列表---分页")
    public Collection<MRoleFeatureRelation> searchRoleFeature(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,roleId,featureId")
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
        List<RoleFeatureRelation> roleFeatureRelations = roleFeatureRelationService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, roleFeatureRelationService.getCount(filters), page, size);
        return convertToModels(roleFeatureRelations, new ArrayList<MRoleFeatureRelation>(roleFeatureRelations.size()), MRoleFeatureRelation.class, fields);
    }
    @RequestMapping(value = ServiceApi.Roles.RoleFeaturesNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组-功能权限关系列表---不分页")
    public Collection<MRoleFeatureRelation> searchRoleFeatureNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        List<RoleFeatureRelation> roleFeatureRelations = roleFeatureRelationService.search(filters);
        return convertToModels(roleFeatureRelations,new ArrayList<MRoleFeatureRelation>(roleFeatureRelations.size()),MRoleFeatureRelation.class,"");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleFeatureExistence, method = RequestMethod.GET)
    @ApiOperation(value = "通用根据过滤条件，判断是否存在")
    public boolean getAppFeaturesFilter(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        Long count = roleFeatureRelationService.getCount(filters);
        return count>0?true:false;
    }
}
