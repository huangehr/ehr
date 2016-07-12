package com.yihu.ehr.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.user.RoleUserModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.user.MRoleUser;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.users.service.RoleUserClient;
import com.yihu.ehr.users.service.UserClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by yww on 2016/7/7.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0+"/admin")
@RestController
@Api(value = "roleUser",description = "角色组人员配置",tags = "")
public class RoleUserController extends BaseController {
    @Autowired
    private RoleUserClient roleUserClient;

    @Autowired
    private UserClient userClient;

    @RequestMapping(value = ServiceApi.Roles.RoleUser,method = RequestMethod.POST)
    @ApiOperation(value = "为角色组配置人员，单个")
    public Envelop createRoleUser(
            @ApiParam(name = "data_json",value = "角色组-人员关系Json串")
            @RequestParam(value = "data_json") String dataJson){
        MRoleUser mRoleUser = roleUserClient.createRoleUser(dataJson);
        if(null == mRoleUser){
            return failed("角色组添加人员失败！");
        }
        return success(null);
    }

    @RequestMapping(value = ServiceApi.Roles.RoleUser,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据角色组id,人员Id删除角色组人员")
    public Envelop deleteRoleUser(
            @ApiParam(name = "user_id",value = "人员id")
            @RequestParam(value = "user_id") String userId,
            @ApiParam(name = "role_id",value = "角色组id")
            @RequestParam(value = "role_id") String roleId){
        if(StringUtils.isEmpty(userId)){
            return failed("人员id不能为空！");
        }
        if(StringUtils.isEmpty(roleId)){
            return failed("角色组id不能为空！");
        }
        boolean bo = roleUserClient.deleteRoleUser(userId, roleId);
        if(bo){
            return success(null);
        }
        return failed("角色组删除人员失败！");
    }


    @RequestMapping(value = ServiceApi.Roles.RoleUsers,method = RequestMethod.POST)
    @ApiOperation(value = "批量新增人员所属角色组，一对多")
    public Envelop batchCreateRolUsersRelation(
            @ApiParam(name = "user_id",value = "人员id")
            @RequestParam(value = "user_id") String userId,
            @ApiParam(name = "role_ids",value = "角色组ids,多个用逗号隔开")
            @RequestParam(value = "role_ids") String roleIds) throws Exception{
        if(StringUtils.isEmpty(userId)){
            return failed("人员id不能为空！");
        }
        if(StringUtils.isEmpty(roleIds)){
            return failed("角色组id不能为空！");
        }
        boolean bo = roleUserClient.batchCreateRolUsersRelation(userId,roleIds);
        if(bo){
            return success(null);
        }
        return failed("新增失败");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleUsers,method = RequestMethod.PUT)
    @ApiOperation(value = "批量修改人员所属角色组关系，一对多")
    public Envelop batchUpdateRoleUsersRelation(
            @ApiParam(name = "user_id",value = "人员id")
            @RequestParam(value = "user_id") String userId,
            @ApiParam(name = "role_ids",value = "角色组ids,多个用逗号隔开")
            @RequestParam(value = "role_ids") String roleIds) throws Exception{
        if(StringUtils.isEmpty(userId)){
            return failed("人员id不能为空！");
        }
        if(StringUtils.isEmpty(roleIds)){
            return failed("角色组id不能为空！");
        }
        boolean bo = roleUserClient.batchUpdateRoleUsersRelation(userId,roleIds);
        if(bo){
            return success(null);
        }
        return failed("修改失败");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleUsers,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组人员关系列表---分页")
    public Envelop searchRoleUser(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,roleId,userId")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception{

        List<RoleUserModel> roleUserModelList = new ArrayList<>();
        ResponseEntity<Collection<MRoleUser>> responseEntity = roleUserClient.searchRoleUser(fields, filters, sorts, size, page);
        Collection<MRoleUser> mRoleUsers  = responseEntity.getBody();
        for (MRoleUser m : mRoleUsers){
            RoleUserModel roleUserModel = changeToModel(m);
            roleUserModelList.add(roleUserModel);
        }
        Integer totalCount = getTotalCount(responseEntity);
        return getResult(roleUserModelList,totalCount,page,size);
    }
    @RequestMapping(value = ServiceApi.Roles.RoleUsersNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组人员关系列表---不分页")
    public Envelop searchRoleUserNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        Envelop envelop = new Envelop();
        List<RoleUserModel> roleUserModelList = new ArrayList<>();
        Collection<MRoleUser> mRoleUsers = roleUserClient.searchRoleUserNoPaging(filters);
        for (MRoleUser m : mRoleUsers){
            RoleUserModel roleUserModel = changeToModel(m);
            roleUserModelList.add(roleUserModel);
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(roleUserModelList);
        return envelop;
    }

    private RoleUserModel changeToModel(MRoleUser m) {
        RoleUserModel model = convertToModel(m, RoleUserModel.class);
        //获取用户名
        MUser user = userClient.getUser(m.getUserId());
        model.setUserName(user == null?"":user.getRealName());
        return model;
    }
}
