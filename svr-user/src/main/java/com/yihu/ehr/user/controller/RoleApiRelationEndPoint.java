package com.yihu.ehr.user.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.user.MRoleApiRelation;
import com.yihu.ehr.user.service.RoleApiRelation;
import com.yihu.ehr.user.service.RoleApiRelationService;
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
 * Created by yww on 2016/7/8.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "roleApi",description ="角色组Api关系管理",tags = "")
public class RoleApiRelationEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private RoleApiRelationService roleApiRelationService;

    @RequestMapping(value = ServiceApi.Roles.RoleApi,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为角色组配置api权限")
    public MRoleApiRelation createRoleApiRelation(
            @ApiParam(name = "data_json",value = "角色组-api关系对象json串")
            @RequestBody String dataJson){
        RoleApiRelation roleApiRelation = toEntity(dataJson,RoleApiRelation.class);
        RoleApiRelation roleApiRelationNew = roleApiRelationService.save(roleApiRelation);
        return convertToModel(roleApiRelationNew,MRoleApiRelation.class);
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApi,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据apiId,角色组id删除角色组-api关系")
    public boolean deleteRoleApiRelation(
            @ApiParam(name = "role_id",value = "角色组id")
            @RequestParam(value = "role_id") String roleId,
            @ApiParam(name = "api_id",value = "应用接口id" )
            @RequestParam(value = "api_id") String apiId){
        RoleApiRelation roleApiRelation = roleApiRelationService.findRelation(Long.parseLong(apiId),Long.parseLong(roleId));
        if(roleApiRelation != null){
            roleApiRelationService.delete(roleApiRelation.getId());
        }
        return true;
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApis,method = RequestMethod.PUT)
    @ApiOperation(value = "批量修改角色组-api关系,一对多")
    public boolean batchUpdateRoleApiRelation(
            @ApiParam(name = "role_id",value = "角色组Id")
            @RequestParam(value = "role_id") Long roleId,
            @ApiParam(name = "api_ids_add",value = "要新增的apiIds",defaultValue = "")
            @RequestParam(name = "api_ids_add",required = false) long[] addApiIds,
            @ApiParam(name = "api_ids_delete",value = "要删除的apiIds",defaultValue = "")
            @RequestParam(value = "api_ids_delete",required = false) String deleteApiIds) throws Exception{
        roleApiRelationService.batchUpdateRoleApiRelation(roleId,addApiIds,deleteApiIds);
        return true;
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApis,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组-api关系列表---分页")
    public Collection<MRoleApiRelation> searchRoleApiRelations(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,roleId,apiId")
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
        List<RoleApiRelation> roleApiRelationList = roleApiRelationService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, roleApiRelationService.getCount(filters), page, size);
        return convertToModels(roleApiRelationList, new ArrayList<>(roleApiRelationList.size()), MRoleApiRelation.class, fields);
    }
    @RequestMapping(value = ServiceApi.Roles.RoleApisNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组与-api关系列表---不分页")
    public Collection<MRoleApiRelation> searchRoleApiRelationNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        List<RoleApiRelation> roleApiRelationList = roleApiRelationService.search(filters);
        return convertToModels(roleApiRelationList,new ArrayList<MRoleApiRelation>(roleApiRelationList.size()),MRoleApiRelation.class,"");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApisExistence, method = RequestMethod.GET)
    @ApiOperation(value = "通用根据过滤条件，判断是否存在")
    public boolean getAppApiFilter(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        Long count = roleApiRelationService.getCount(filters);
        return count>0?true:false;
    }
}
