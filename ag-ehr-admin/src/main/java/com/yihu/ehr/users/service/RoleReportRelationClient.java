package com.yihu.ehr.users.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.ListResult;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.user.MRoleReportRelation;
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
import java.util.List;

/**
 * Created by wxw on 2017/8/22.
 */
@FeignClient(name = MicroServices.User)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface RoleReportRelationClient {

    @RequestMapping(value = ServiceApi.Roles.DeleteRoleReportRelationByRoleId, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除角色与资源报表的授权关系")
    Result deleteByRoleId(
            @ApiParam(name = "roleId", value = "角色Id", defaultValue = "")
            @RequestParam(value = "roleId") Long roleId);

    @RequestMapping(value = ServiceApi.Roles.BatchAddRoleReportRelation, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增&修改角色与资源报表的授权关系")
    ObjectResult batchAddRoleReportRelation(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model);

    @RequestMapping(value = ServiceApi.Roles.SearchRoleReportRelation, method = RequestMethod.GET)
    @ApiOperation(value = "查询角色与资源报表的授权关系列表---分页")
    ResponseEntity<Collection<MRoleReportRelation>> searchRoleReportRelation(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ServiceApi.Roles.SearchRoleReportRelationNoPage, method = RequestMethod.GET)
    @ApiOperation(value = "查询角色与资源报表的授权关系列表---分页")
    List<MRoleReportRelation> searchRoleReportRelationNoPage(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters);
}
