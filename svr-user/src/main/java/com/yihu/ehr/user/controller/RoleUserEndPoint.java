package com.yihu.ehr.user.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.user.MRoleUser;
import com.yihu.ehr.user.service.RoleUser;
import com.yihu.ehr.user.service.RoleUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by yww on 2016/7/7.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "roleUser",description = "角色组人员配置")
public class RoleUserEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private RoleUserService roleUserService;

    @RequestMapping(value = ServiceApi.Roles.RoleUser,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为角色组配置人员，单个")
    public MRoleUser createRoleUser(
            @ApiParam(name = "data_json",value = "角色组-人员关系Json串")
            @RequestBody String dataJson){
        RoleUser roleUser = toEntity(dataJson,RoleUser.class);
        RoleUser roleUserNew = roleUserService.save(roleUser);
        return convertToModel(roleUserNew,MRoleUser.class);
    }

    @RequestMapping(value = ServiceApi.Roles.RoleUser,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据角色组id,人员Id删除角色组人员")
    public boolean deleteRoleUser(
            @ApiParam(name = "user_id",value = "人员id")
            @RequestParam(value = "user_id") String userId,
            @ApiParam(name = "role_id",value = "角色组id")
            @RequestParam(value = "role_id") String roleId){
        RoleUser roleUser = roleUserService.findRelation(userId,Long.parseLong(roleId));
        if(null != roleUser){
            roleUserService.delete(roleUser.getId());
        }
        return true;
    }

    @RequestMapping(value = ServiceApi.Roles.RoleUserByUserId,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据人员id，删除其与所有角色组关系")
    public boolean deleteRoleUserBuUserId(
            @ApiParam(name = "user_id",value = "人员id")
            @RequestParam(value = "user_id") String userId)throws Exception{
        Collection<RoleUser> roleUsers = roleUserService.search("userId=" + userId);
        List<Long> ids = new ArrayList<>();
        for(RoleUser roleUser : roleUsers){
            ids.add(roleUser.getId());
        }
        roleUserService.delete(ids);
        return true;
    }

    @RequestMapping(value = ServiceApi.Roles.RoleUsers,method = RequestMethod.DELETE)
    @ApiOperation(value = "人员id,角色组ids，批量删除人员-角色组关系")
    public boolean batchDeleteRoleUserRelation(
            @ApiParam(name = "user_id",value = "人员id")
            @RequestParam(value = "user_id") String userId,
            @ApiParam(name = "role_ids",value = "角色组ids")
            @RequestParam(value = "role_ids") String roleIds) throws Exception{
        return roleUserService.batchDeleteRoleUserRelation(userId, roleIds);
    }

    @RequestMapping(value = ServiceApi.Roles.RoleUsers,method = RequestMethod.POST)
    @ApiOperation(value = "批量新增人员所属角色组，一对多")
    public boolean batchCreateRolUsersRelation(
            @ApiParam(name = "user_id",value = "人员id")
            @RequestParam(value = "user_id") String userId,
            @ApiParam(name = "role_ids",value = "角色组ids,多个用逗号隔开")
            @RequestParam(value = "role_ids") String roleIds) throws Exception{
        roleUserService.batchCreateRoleUsersRelation(userId,roleIds);
        return true;
    }

    @RequestMapping(value = ServiceApi.Roles.RoleUsers,method = RequestMethod.PUT)
    @ApiOperation(value = "批量修改人员所属角色组关系，一对多")
    public boolean batchUpdateRoleUsersRelation(
            @ApiParam(name = "user_id",value = "人员id")
            @RequestParam(value = "user_id") String userId,
            @ApiParam(name = "role_ids",value = "角色组ids,多个用逗号隔开")
            @RequestParam(value = "role_ids") String roleIds) throws Exception{
        roleUserService.batchUpdateRoleUsersRelation(userId,roleIds);
        return true;
    }

    @RequestMapping(value = ServiceApi.Roles.RoleUsers,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组人员关系列表---分页")
    public Collection<MRoleUser> searchRoleUser(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,roleId,userId")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        List<RoleUser> roleUserList = roleUserService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, roleUserService.getCount(filters), page, size);
        return convertToModels(roleUserList, new ArrayList<>(roleUserList.size()), MRoleUser.class, fields);
    }
    @RequestMapping(value = ServiceApi.Roles.RoleUsersNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组人员关系列表---不分页")
    public Collection<MRoleUser> searchRoleUserNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        List<RoleUser> roleUserList = roleUserService.search(filters);
        return convertToModels(roleUserList,new ArrayList<MRoleUser>(roleUserList.size()),MRoleUser.class,"");
    }
}
