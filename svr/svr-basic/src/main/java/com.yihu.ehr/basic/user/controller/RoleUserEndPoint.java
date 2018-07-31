package com.yihu.ehr.basic.user.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yihu.ehr.basic.apps.model.UserApp;
import com.yihu.ehr.basic.apps.service.UserAppService;
import com.yihu.ehr.basic.org.model.OrgDept;
import com.yihu.ehr.basic.org.model.OrgMemberRelation;
import com.yihu.ehr.basic.org.model.Organization;
import com.yihu.ehr.basic.org.service.OrgMemberRelationService;
import com.yihu.ehr.basic.user.dao.XUserTypeRepository;
import com.yihu.ehr.basic.user.entity.*;
import com.yihu.ehr.basic.user.service.RoleOrgService;
import com.yihu.ehr.basic.user.service.RoleUserService;
import com.yihu.ehr.basic.user.service.RolesService;
import com.yihu.ehr.basic.user.service.UserService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.dict.SystemDictEntry;
import com.yihu.ehr.model.org.MOrgDeptJson;
import com.yihu.ehr.model.user.MRoleOrg;
import com.yihu.ehr.model.user.MRoleUser;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

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
    @Autowired
    private RoleOrgService roleOrgService;

    @Autowired
    private UserService userService;
    @Autowired
    private OrgMemberRelationService orgMemberRelationService;

    @Autowired
    private XUserTypeRepository xUserTypeRepository;


    @RequestMapping(value = ServiceApi.Roles.RoleUser,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为角色组配置人员，单个")
    public MRoleUser createRoleUser(
            @ApiParam(name = "data_json",value = "角色组-人员关系Json串")
            @RequestBody String dataJson) throws IOException {
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
    @RequestMapping(value = ServiceApi.Roles.RoleUsersNoPage, method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组人员关系列表---不分页")
    public Collection<MRoleUser> searchRoleUserNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        List<RoleUser> roleUserList = roleUserService.search(filters);
        return convertToModels(roleUserList, new ArrayList<MRoleUser>(roleUserList.size()), MRoleUser.class,"");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleOrg,method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为角色组配置机构，单个")
    public MRoleOrg createRoleOrg(
            @ApiParam(name = "data_json",value = "角色组-机构关系Json串")
            @RequestBody String dataJson) throws IOException {
        RoleOrg roleOrg = toEntity(dataJson,RoleOrg.class);
        roleOrg = roleOrgService.save(roleOrg);
        return convertToModel(roleOrg,MRoleOrg.class);
    }

    @RequestMapping(value = ServiceApi.Roles.RoleOrg,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据角色组id,删除角色组机构")
    public boolean deleteRoleOrg(
            @ApiParam(name = "role_id",value = "角色组id")
            @RequestParam(value = "role_id") String roleId,
            @ApiParam(name = "org_code",value = "角色组id")
            @RequestParam(value = "org_code") String orgCode){

        //删除角色组内机构
        RoleOrg roleOrg = roleOrgService.findRelation(roleId,orgCode);
        if(null != roleOrg){
            roleOrgService.deleteRoleOrg(roleOrg.getId());
        }
        return true;
    }


    @RequestMapping(value = ServiceApi.Roles.RoleOrgs,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组机构列表---分页")
    public Collection<MRoleOrg> searchRoleOrg(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,roleId,orgId")
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
        List<RoleOrg> roleOrgList = roleOrgService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, roleOrgService.getCount(filters), page, size);
        return convertToModels(roleOrgList, new ArrayList<>(roleOrgList.size()), MRoleOrg.class, fields);
    }

    @RequestMapping(value = ServiceApi.Roles.RoleOrgsNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组机构列表---不分页")
    public Collection<MRoleOrg> searchRoleOrgsNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        List<RoleOrg> roleOrgList = roleOrgService.search(filters);
        return convertToModels(roleOrgList, new ArrayList<MRoleOrg>(roleOrgList.size()), MRoleOrg.class,"");
    }

    @RequestMapping(value = ServiceApi.Roles.ClientRole, method = RequestMethod.GET)
    @ApiOperation(value = "查询应用角色ID列表")
    public List<String> clientRole(
            @ApiParam(name = "clientId", value = "应用ID", required = true)
            @RequestParam(value = "clientId") String clientId,
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam(value = "userId") String userId) throws  Exception{
        List<Integer> list = roleUserService.userClientRole(clientId, userId);
        List<String> roles = new ArrayList<>();
        list.forEach(item -> {
            roles.add(item.toString());
        });
        return roles;
    }

    /**
     * 基于用户ID及用户类型进行授权清理及授权更新
     * @param userId
     * @param userType
     * @return Envelop
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.Roles.RoleUserTypeUpdate, method = RequestMethod.POST)
    @ApiOperation(value = "基于用户ID&用户类型进行授权更新")
    public Envelop setUserRolesForUpdate(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "userType", value = "变更后用户类型", required = true)
            @RequestParam(value = "userType") int userType,
            @ApiParam(name = "flag", value = "更新类型，当0-删除所有的旧有授权，1-不删除旧有授权", required = true)
            @RequestParam(value = "flag") int flag,
            @ApiParam(name = "orgModel", value = "所属机构JSON串", required = false)
            @RequestParam(value = "orgModel") String orgModel ) throws  Exception{
        Envelop envelop = new Envelop();

        if(flag == 0){
            //当flag为0时，删除当前用户，所有的角色授权，并重新按新的用户类型进行授权
            Collection<RoleUser> roleUsers = roleUserService.search("userId=" + userId);
            List<Long> ids = new ArrayList<>();
            for(RoleUser roleUser : roleUsers){
                ids.add(roleUser.getId());
            }
            //删除该用户所有的角色授权
            roleUserService.delete(ids);
            //删除该用户所有的应用授权
            userAppService.delUserAppByUserId(userId);
        }

        //基于变更后的用户类型进行角色的初始化授权
        List<UserTypeRoles> userTypeRoles = new ArrayList<>();
        userTypeRoles = userService.getUserTypeRoles(userType);
        if(userTypeRoles != null && userTypeRoles.size() >0){
            envelop = setUserRoles(userTypeRoles, userId);
        }

        // 以上角色授权完毕，以下更新用户所属机构及部门的信息
        envelop = setOrgDeptRelation(orgModel,userId);
        return envelop;
    }

    /**
     * 基于用户ID及用户类型进行授权清理及授权更新
     */
    @RequestMapping(value = ServiceApi.Roles.UserOrgRela, method = RequestMethod.POST)
    @ApiOperation(value = "基于用户ID&用户所属机构进行机构及部门间的对应关系")
    public Envelop setUserRolesForUpdate(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "orgModel", value = "所属机构JSON串", required = false)
            @RequestParam(value = "orgModel") String orgModel ) throws  Exception{
        Envelop envelop = new Envelop();
        envelop = setOrgDeptRelation(orgModel,userId);
        return envelop;
    }

    /**
     * 用户新增初始化用户角色授权
     */
    public Envelop setUserRoles( List<UserTypeRoles> userTypeRoles, String userId) {
        long roleId = 0;
        String appId = "";
        Map<String ,String> result = new HashMap<>();
        RoleUser roleUser = new RoleUser();

        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(false);

        for (UserTypeRoles userTypeRoles1 : userTypeRoles) {
            roleId = userTypeRoles1.getRoleId();
            appId = userTypeRoles1.getClientId().toString();
            roleUser.setRoleId(roleId);
            roleUser.setUserId(userId);

            //更新用户角色授权（role_user表的维护）
            if (roleUserService.findRelation(userId, roleId) != null) {
                continue;
            }
            roleUser = roleUserService.createRoleUser(roleUser);
            if (roleUser == null) {
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("新增角色授权失败！");
                return envelop;
            }else{
                envelop.setSuccessFlg(true);
            }

            //更新用户应用授权（user_app表的维护）
            UserApp userApp = new UserApp();
            userApp = userAppService.findByAppIdAndUserId(appId, userId);
            if(userApp != null){
                continue;
            }
            userApp.setUserId(userId);
            userApp.setAppId(appId);
            userApp.setStatus(0);
            userApp.setShowFlag(1);
            userApp = userAppService.save(userApp);
            if(userApp == null){
                envelop.setSuccessFlg(false);
                envelop.setErrorMsg("新增用户应用授权失败！");
                return envelop;
            }else{
                envelop.setSuccessFlg(true);
            }
        }
        return envelop;
    }

    /**
     * 用户新增初始化用户所属机构（机构部门人员关系维护）
     */
    public Envelop setOrgDeptRelation(String orgModel , String userId)  throws  Exception{
        Envelop envelop = new Envelop();

        //删除该用户所有的机构授权信息，基于新传入的机构部门信息进行重新生成。
        orgMemberRelationService.deleteOrgMemberRelationByUserId(userId);
        String orgId = "";
        List<String> deptIds = new ArrayList<>();
        OrgMemberRelation orgMemberRelation = new OrgMemberRelation();

        List<MOrgDeptJson> orgDeptJsonList = objectMapper.readValue(orgModel, new TypeReference<List<MOrgDeptJson>>() {});
        if(orgDeptJsonList != null && orgDeptJsonList.size() > 0){
            for(MOrgDeptJson mOrgDeptJson :orgDeptJsonList ){
                orgId =  mOrgDeptJson.getOrgId().toString();
                deptIds = Arrays.asList(mOrgDeptJson.getDeptIds().split(","));
                if(deptIds.size() > 0){
                    for(String deptId:deptIds){
                        int deptIdInt = Integer.parseInt(deptId);
                        //验证用户机构关联是否已存在
                        int res = orgMemberRelationService.getCountByOrgIdAndUserId(orgId,userId,deptIdInt);
                        if(res == 0){
                            orgMemberRelation.setUserId(userId);
                            orgMemberRelation.setOrgId(orgId);
                            orgMemberRelation.setDeptId(deptIdInt);
                            orgMemberRelation = orgMemberRelationService.save(orgMemberRelation);

                            if(orgMemberRelation != null){
                                //新增成功，继续循环
                                continue;
                            }else{
                                envelop.setSuccessFlg(false);
                                envelop.setErrorMsg("新增用户的机构科室关联失败。");
                                return envelop;
                            }
                        }else{
                            //用户与机构关系已存在，继续循环
                            continue;
                        }
                    }
                }
            }
        }
        return envelop;
    }

    /**
     * 新增用户类型
     */
    @RequestMapping(value = ServiceApi.Roles.CreateUserType, method = RequestMethod.POST)
    @ApiOperation(value = "新增用户类别")
    public Envelop createUserType(
            @ApiParam(name = "code", value = "用户类别编码", required = true)
            @RequestParam(value = "code") String code,
            @ApiParam(name = "name", value = "用户类别名称", required = false)
            @RequestParam(value = "name") String name ) throws  Exception{
        Envelop envelop = new Envelop();
        UserType userType = new UserType();

        userType.setCode(code);
        userType.setName(name);
        userType.setActiveFlag("1");
        userType = xUserTypeRepository.save(userType);
        if(userType != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(userType);
        }else{
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("新增用户类别失败，请重试！");
        }
        return envelop;
    }
}
