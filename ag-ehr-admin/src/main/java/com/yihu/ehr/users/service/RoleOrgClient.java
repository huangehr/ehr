package com.yihu.ehr.users.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.user.MRoleOrg;
import com.yihu.ehr.model.user.MRoleUser;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

/**
 * Created by yww on 2016/7/8.
 */
@FeignClient(name = MicroServices.User)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface RoleOrgClient {

    @RequestMapping(value = ServiceApi.Roles.RoleOrg,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为角色组配置机构，单个")
    MRoleUser createRoleOrg(
            @ApiParam(name = "data_json",value = "角色组-机构关系Json串")
            @RequestBody String dataJson);

    @RequestMapping(value = ServiceApi.Roles.RoleOrg,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据角色组id,删除角色组机构")
    boolean deleteRoleOrg(
            @ApiParam(name = "org_code",value = "机构Code")
            @RequestParam(value = "org_code") String orgCode,
            @ApiParam(name = "role_id",value = "角色组id")
            @RequestParam(value = "role_id") String roleId);


    @RequestMapping(value = ServiceApi.Roles.RoleOrgs,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组机构列表---分页")
    ResponseEntity<Collection<MRoleOrg>> searchRoleOrg(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,roleId,orgId")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.Roles.RoleOrgsNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组机构列表---不分页")
    Collection<MRoleOrg> searchRoleOrgNoPaging(
            @ApiParam(name = "filters", value = "过滤条件，为空检索全部", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters);
}
