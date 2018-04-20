package com.yihu.ehr.users.controller;

import com.yihu.ehr.adapter.utils.ExtendController;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.common.ObjectResult;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.users.service.RoleReportRelationClient;
import com.yihu.ehr.util.FeignExceptionUtils;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by wxw on 2017/8/22.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0+"/admin")
@RestController
@Api(value = "roleReportRelation",description = "角色与资源报表的授权关系", tags = {"安全管理-角色与资源报表的授权关系"})
public class RoleReportRelationController extends ExtendController {

    @Autowired
    private RoleReportRelationClient roleReportRelationClient;

    @RequestMapping(value = ServiceApi.Roles.DeleteRoleReportRelationByRoleId, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除角色与资源报表的授权关系")
    public Envelop deleteByRoleId(
            @ApiParam(name = "roleId", value = "角色Id", defaultValue = "")
            @RequestParam(value = "roleId") Long roleId) {
        try {
            Result result = roleReportRelationClient.deleteByRoleId(roleId);
            if(result.getCode() == 200){
                return successMsg(result.getMessage());
            }else{
                return failed("角色与资源报表的授权关系删除失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }

    @RequestMapping(value = ServiceApi.Roles.BatchAddRoleReportRelation, method = RequestMethod.POST)
    @ApiOperation(value = "新增&修改角色与资源报表的授权关系")
    public Envelop batchAddRoleReportRelation(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestParam String model) {
        try {
            ObjectResult objectResult = roleReportRelationClient.batchAddRoleReportRelation(model);
            if (objectResult.getCode() == 200) {
                return successObj(objectResult.getData());
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return failed(FeignExceptionUtils.getErrorMsg(e));
        }
    }
}
