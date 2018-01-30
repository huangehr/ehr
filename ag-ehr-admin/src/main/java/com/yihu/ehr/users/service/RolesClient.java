package com.yihu.ehr.users.service;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.user.MRoles;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.List;

/**
 * Created by yww on 2016/7/8.
 */
@FeignClient(name = MicroServices.User)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface RolesClient {

    @RequestMapping(value = ServiceApi.Roles.RoleBatchAdd,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "批量新增角色组")
    Boolean roleBatchAdd(
            @ApiParam(name = "data_json",value = "批量新增角色组Json字符串")
            @RequestBody String dataJson,
            @ApiParam(name = "orgCodes", value = "多机构编码拼接字符串")
            @RequestParam(value = "orgCodes") String orgCodes);

    @RequestMapping(value = ServiceApi.Roles.Role,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增角色组")
    MRoles createRoles(
            @ApiParam(name = "data_json",value = "新增角色组Json字符串")
            @RequestBody String dataJson);

    @RequestMapping(value = ServiceApi.Roles.Role,method = RequestMethod.PUT,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改角色组")
    MRoles updateRoles(
            @ApiParam(name = "data_json",value = "修改角色组Json字符串")
            @RequestBody String dataJson);

    @RequestMapping(value = ServiceApi.Roles.RoleId,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据角色组id删除")
    boolean deleteRoles(
            @ApiParam(name = "id",value = "角色组id")
            @PathVariable(value = "id") long id);

    @RequestMapping(value = ServiceApi.Roles.RoleId,method = RequestMethod.GET)
    @ApiOperation(value = "根据角色组id查询")
    MRoles getRolesById(
            @ApiParam(name = "id",value = "角色组id")
            @PathVariable(value = "id") long id);

    @RequestMapping(value = ServiceApi.Roles.Roles,method = RequestMethod.GET)
    @ApiOperation(value = "查询用户角色列表---分页")
    ResponseEntity<Collection<MRoles>> searchRoles(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,code,name,description,appId,type")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+code,+name")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.Roles.RolesNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询用户角色列表---不分页")
    Collection<MRoles> searchRolesNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters);

    @RequestMapping(value = ServiceApi.Roles.RoleNameExistence,method = RequestMethod.GET)
    @ApiOperation(value = "角色组名称是否已存在" )
    boolean isNameExistence(
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId,
            @ApiParam(name = "name",value = "角色组名称")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "orgCode",value = "机构Code")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "type",value = "角色组类别")
            @RequestParam(value = "type",required = false) String type);

    @RequestMapping(value = ServiceApi.Roles.RoleCodeExistence,method = RequestMethod.GET)
    @ApiOperation(value = "角色组代码是否已存在" )
    boolean isCodeExistence(
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId,
            @ApiParam(name = "code",value = "角色组代码")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "orgCode",value = "机构Code")
            @RequestParam(value = "orgCode") String orgCode,
            @ApiParam(name = "type",value = "角色组类别")
            @RequestParam(value = "type",required = false) String type);

    @RequestMapping(value = ServiceApi.Roles.RoleFindByField, method = RequestMethod.POST)
    @ApiOperation(value = "通过字段获取角色" )
    Envelop findByFields(
            @ApiParam(name = "appId",value = "应用id")
            @RequestParam(value = "appId") String appId,
            @ApiParam(name = "code",value = "角色组代码")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "type",value = "角色组类别")
            @RequestParam(value = "type") String type);
}
