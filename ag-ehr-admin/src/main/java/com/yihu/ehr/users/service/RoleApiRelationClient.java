package com.yihu.ehr.users.service;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.user.MRoleApiRelation;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

/**
 * Created by yww on 2016/7/8.
 */
@FeignClient(name = MicroServices.User)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface RoleApiRelationClient {

    @RequestMapping(value = ServiceApi.Roles.RoleApi,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为角色组添加应用api")
    MRoleApiRelation createRoleApiRelation(
            @ApiParam(name = "data_json",value = "角色组-应用api关系对象json串")
            @RequestBody String dataJson);

    @RequestMapping(value = ServiceApi.Roles.RoleApi,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据apiId,角色组id删除角色组-api关系")
    boolean deleteRoleApiRelation(
            @ApiParam(name = "api_id",value = "应用接口id" )
            @RequestParam(value = "api_id") String apiId,
            @ApiParam(name = "role_id",value = "角色组id")
            @RequestParam(value = "role_id") String roleId);

    @RequestMapping(value = ServiceApi.Roles.RoleApiByRoleId,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据角色组id删除所配置的api")
    boolean deleteRoleApiRelationByRoleId(
            @ApiParam(name = "role_id",value = "角色组id")
            @RequestParam(value = "role_id") Long roleId);

    @RequestMapping(value = ServiceApi.Roles.RoleApis,method = RequestMethod.PUT)
    @ApiOperation(value = "批量修改角色组-api关系,一对多")
    boolean batchUpdateRoleApiRelation(
            @ApiParam(name = "role_id",value = "角色组Id")
            @RequestParam(value = "role_id") Long roleId,
            @ApiParam(name = "api_ids_add",value = "要新增的apiIds",defaultValue = "")
            @RequestParam(name = "api_ids_add",required = false) Long[] addApiIds,
            @ApiParam(name = "api_ids_delete",value = "要删除的apiIds",defaultValue = "")
            @RequestParam(value = "api_ids_delete",required = false) String deleteApiIds);


    @RequestMapping(value = ServiceApi.Roles.RoleApis,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组-api关系列表---分页")
    ResponseEntity<Collection<MRoleApiRelation>> searchRoleApiRelations(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,roleId,apiId")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.Roles.RoleApisNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组与-api关系列表---不分页")
    Collection<MRoleApiRelation> searchRoleApiRelationNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters);

    @RequestMapping(value = ServiceApi.Roles.RoleApisExistence, method = RequestMethod.GET)
    @ApiOperation(value = "通用根据过滤条件，判断是否存在")
    boolean getAppApiFilter(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters);
}
