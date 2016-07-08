package com.yihu.ehr.user.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.user.MRoleFeatureRelation;
import com.yihu.ehr.user.service.RoleFeatureRelation;
import com.yihu.ehr.user.service.RoleFeatureRelationService;
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
@Api(value = "roleFeature",description = "角色组权限配置")
public class RoleFeatureRelationEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private RoleFeatureRelationService roleFeatureRelationService;

    @RequestMapping(value = ServiceApi.Roles.RoleFeature,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为角色组添加权限")
    public MRoleFeatureRelation createRoleFeature(
            @ApiParam(name = "data_json",value = "角色组-权限关系Json串")
            @RequestBody String dataJson){
        RoleFeatureRelation roleFeatureRelation = toEntity(dataJson,RoleFeatureRelation.class);
        RoleFeatureRelation roleFeatureRelationNew = roleFeatureRelationService.save(roleFeatureRelation);
        return convertToModel(roleFeatureRelationNew,MRoleFeatureRelation.class);
    }
    @RequestMapping(value = ServiceApi.Roles.RoleFeatureId,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除角色组的权限")
    public boolean deleteRoleFeature(
            @ApiParam(name = "id",value = "角色组-权限关系id")
            @PathVariable(value = "id") long id){
        roleFeatureRelationService.delete(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.Roles.RoleFeatures,method = RequestMethod.GET)
    @ApiOperation(value = "查询用户角色组-权限关系列表---分页")
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
        page = reducePage(page);
        if (StringUtils.isEmpty(filters)) {
            Page<RoleFeatureRelation> roleUserPage = roleFeatureRelationService.getRoleUserList(sorts, page, size);
            pagedResponse(request, response, roleUserPage.getTotalElements(), page, size);
            return convertToModels(roleUserPage.getContent(), new ArrayList<>(roleUserPage.getNumber()), MRoleFeatureRelation.class, fields);
        } else {
            List<RoleFeatureRelation> roleFeatureRelations = roleFeatureRelationService.search(fields, filters, sorts, page, size);
            pagedResponse(request, response, roleFeatureRelationService.getCount(filters), page, size);
            return convertToModels(roleFeatureRelations, new ArrayList<MRoleFeatureRelation>(roleFeatureRelations.size()), MRoleFeatureRelation.class, fields);
        }
    }
    @RequestMapping(value = ServiceApi.Roles.RoleFeaturesNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询用户角色组-应用权限关系列表---不分页")
    public Collection<MRoleFeatureRelation> searchRoleFeatureNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        List<RoleFeatureRelation> roleFeatureRelations = roleFeatureRelationService.search(filters);
        return convertToModels(roleFeatureRelations,new ArrayList<MRoleFeatureRelation>(roleFeatureRelations.size()),MRoleFeatureRelation.class,"");
    }
}
