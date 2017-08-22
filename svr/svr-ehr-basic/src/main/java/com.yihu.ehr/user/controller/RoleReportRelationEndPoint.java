package com.yihu.ehr.user.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.user.entity.RoleReportRelation;
import com.yihu.ehr.user.service.RoleReportRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by wxw on 2017/8/22.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "roleFeature",description = "角色与资源报表的授权关系", tags = {"安全管理-角色与资源报表的授权关系"})
public class RoleReportRelationEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private RoleReportRelationService roleReportRelationService;

    @RequestMapping(value = ServiceApi.Roles.DeleteRoleReportRelationByRoleId, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除角色与资源报表的授权关系")
    public Result deleteByRoleId(
            @ApiParam(name = "roleId", value = "角色Id", defaultValue = "")
            @RequestParam(value = "roleId") Long roleId) {
        roleReportRelationService.deleteByRoleId(roleId);
        return Result.success("角色与资源报表的授权关系删除成功！");
    }

    @RequestMapping(value = ServiceApi.Roles.BatchAddRoleReportRelation, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增&修改角色与资源报表的授权关系")
    public ObjectResult batchAddRoleReportRelation(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model) throws Exception {
        List<RoleReportRelation> list = objectMapper.readValue(model, new TypeReference<List<RoleReportRelation>>(){});
        if (list != null && list.size() > 0) {
            roleReportRelationService.deleteByRoleId(list.get(0).getRoleId());
        }
        for (int i=0; i<list.size(); i++) {
            roleReportRelationService.save(list.get(i));
        }
        return Result.success("资源视图-关联指标表更新成功！", list);
    }
}
