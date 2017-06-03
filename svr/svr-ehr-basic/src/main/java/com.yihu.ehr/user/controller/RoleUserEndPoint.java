package com.yihu.ehr.user.controller;

import com.yihu.ehr.apps.model.UserApp;
import com.yihu.ehr.apps.service.UserAppService;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.user.MRoleUser;
import com.yihu.ehr.user.entity.RoleAppRelation;
import com.yihu.ehr.user.entity.RoleUser;
import com.yihu.ehr.user.entity.Roles;
import com.yihu.ehr.user.service.RoleAppRelationService;
import com.yihu.ehr.user.service.RoleUserService;
import com.yihu.ehr.user.service.RolesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
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
@Api(value = "roles",description = "角色管理", tags = {"安全管理-角色管理"})
public class RoleUserEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private RoleUserService roleUserService;
    @Autowired
    private RolesService rolesService;
    @Autowired
    private UserAppService userAppService;

    @RequestMapping(value = ServiceApi.Roles.RoleUser,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为角色组配置人员，单个")
    public MRoleUser createRoleUser(
            @ApiParam(name = "data_json",value = "角色组-人员关系Json串")
            @RequestBody String dataJson){
        RoleUser roleUser = toEntity(dataJson,RoleUser.class);
        String[] fields = {"userId","roleId"};
        String[] values = {roleUser.getUserId(),roleUser.getRoleId()+""};
        List<RoleUser> roleUserList = roleUserService.findByFields(fields, values);
        if(roleUserList != null && roleUserList.size() > 0){
            return convertToModel(roleUserList.get(0), MRoleUser.class);
        }
//        RoleUser roleUserNew = roleUserService.save(roleUser);
        RoleUser roleUserNew = roleUserService.createRoleUser(roleUser);
        return convertToModel(roleUserNew,MRoleUser.class);
    }

    @RequestMapping(value = ServiceApi.Roles.RoleUser,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据角色组id,人员Id删除角色组人员")
    public boolean deleteRoleUser(
            @ApiParam(name = "user_id",value = "人员id")
            @RequestParam(value = "user_id") String userId,
            @ApiParam(name = "role_id",value = "角色组id")
            @RequestParam(value = "role_id") String roleId){
        //删除角色组内成员
        RoleUser roleUser = roleUserService.findRelation(userId,Long.parseLong(roleId));
        if(null != roleUser){
            roleUserService.delete(roleUser.getId());
            //roleUserService.deleteRoleUser(roleUser);
        }

        //根据角色、应用、用户三者，判断该用户是否在该应用的其他角色组中存在授权：
        //若存在，则不做任何操作，若不存在则需要变更USERAPP表中的状态 0 -> 1
        String appId = "";
        boolean result = false;
        Roles roles = rolesService.getRoleByRoleId(Long.parseLong(roleId));
        if(roles != null){
            appId = roles.getAppId();
            List<Roles> rolesRe = rolesService.getRoleByAppId(appId);
            if(rolesRe != null && rolesRe.size() > 0){
                for(Roles ra : rolesRe ){
                    long roleIdRe = ra.getId();
                    RoleUser roleUserRe = roleUserService.findRelation(userId, roleIdRe);
                    if(roleUserRe == null){
                        continue;
                    }else{
                        result = true;
                        break;
                    }
                }
            }
            //当该用户确实无授权的情况，删除数据
            if(!result){
                UserApp userApp = userAppService.findByAppIdAndUserId(appId, userId);
                if(userApp != null){
                    userAppService.delete(userApp);
                    return true;
                }
            }
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
