package com.yihu.ehr.users.service;

import com.yihu.ehr.api.ServiceApi;
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
    @RequestMapping(value = ServiceApi.Roles.RoleApp,method = RequestMethod.POST)
    @ApiOperation(value = "新增应用-应用角色组关系")
    boolean createRoleAppRelation(
            @ApiParam(name = "role_ids",value = "应用角色组ids,多个已逗号隔开")
            @RequestParam(value = "role_ids") String roleIds,
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId);

    @RequestMapping(value = ServiceApi.Roles.RoleApp,method = RequestMethod.PUT)
    @ApiOperation(value = "修改应用-应用角色组关系")
    boolean updateRoleAppRelation(
            @ApiParam(name = "role_ids",value = "应用角色组ids,多个已逗号隔开")
            @RequestParam(value = "role_ids") String roleIds,
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId);

    @RequestMapping(value = ServiceApi.Roles.RoleAppId,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除角色组-应用关系")
    boolean deleteRoleApp(
            @ApiParam(name = "id",value = "角色组-应用关系id")
            @PathVariable(value = "id") long id);

    @RequestMapping(value = ServiceApi.Roles.RoleApps,method = RequestMethod.GET)
    @ApiOperation(value = "查询用户角色组-应用关系列表---分页")
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

    @RequestMapping(value = ServiceApi.Roles.RoleAppsNopage,method = RequestMethod.GET)
    @ApiOperation(value = "查询用户角色组-应用权限关系列表---不分页")
    Collection<MRoleAppRelation> searchRoleAppNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters);
}
