package com.yihu.ehr.users.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

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
}
