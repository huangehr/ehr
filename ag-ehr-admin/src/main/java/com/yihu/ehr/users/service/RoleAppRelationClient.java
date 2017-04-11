package com.yihu.ehr.users.service;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.user.MRoleAppRelation;
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
public interface RoleAppRelationClient {
    @RequestMapping(value = ServiceApi.Roles.RoleApp,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为角色组配置应用，单个")
    MRoleAppRelation createRoleAppRelation(
            @ApiParam(name = "data_json",value = "角色组-应用关系对象Json字符串")
            @RequestBody String dataJson);

    @RequestMapping(value = ServiceApi.Roles.RoleApp,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据角色组id,应用Id删除角色组-应用关系")
    boolean deleteRoleApp(
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId,
            @ApiParam(name = "role_id",value = "角色组id")
            @RequestParam(value = "role_id") String roleId);

    @RequestMapping(value = ServiceApi.Roles.RoleApps,method = RequestMethod.POST)
    @ApiOperation(value = "批量新增应用-角色组关系，一对多")
    boolean batchCreateRoleAppRelation(
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId,
            @ApiParam(name = "role_ids",value = "应用角色组ids,多个用逗号隔开")
            @RequestParam(value = "role_ids") String roleIds);

    @RequestMapping(value = ServiceApi.Roles.RoleApps,method = RequestMethod.PUT)
    @ApiOperation(value = "批量修改应用-角色组关系，一对多")
    boolean batchUpdateRoleAppRelation(
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId,
            @ApiParam(name = "role_ids",value = "应用角色组ids,多个用逗号隔开")
            @RequestParam(value = "role_ids") String roleIds);

    @RequestMapping(value = ServiceApi.Roles.RoleApps,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组-应用关系列表---分页")
    ResponseEntity<Collection<MRoleAppRelation>> searchRoleApp(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,roleId,appId")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.Roles.RoleAppsNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组-应用关系列表---不分页")
    Collection<MRoleAppRelation> searchRoleAppNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters);
}
