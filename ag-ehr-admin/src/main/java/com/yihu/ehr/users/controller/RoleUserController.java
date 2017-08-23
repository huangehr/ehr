package com.yihu.ehr.users.controller;

import com.yihu.ehr.agModel.user.RoleUserModel;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.resource.MRsReport;
import com.yihu.ehr.model.resource.MRsReportCategory;
import com.yihu.ehr.model.resource.MRsReportCategoryInfo;
import com.yihu.ehr.model.user.MRoleReportRelation;
import com.yihu.ehr.model.user.MRoleUser;
import com.yihu.ehr.model.user.MRoles;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.resource.client.RsReportCategoryClient;
import com.yihu.ehr.resource.client.RsReportClient;
import com.yihu.ehr.users.service.RoleReportRelationClient;
import com.yihu.ehr.users.service.RoleUserClient;
import com.yihu.ehr.users.service.RolesClient;
import com.yihu.ehr.users.service.UserClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
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
@Api(value = "roleUser",description = "角色组人员配置", tags = {"安全管理-角色组人员配置"})
public class RoleUserController extends BaseController {
    @Autowired
    private RoleUserClient roleUserClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private RolesClient rolesClient;

    @Autowired
    private RsReportCategoryClient rsReportCategoryClient;

    @Autowired
    private RsReportClient rsReportClient;

    @Autowired
    private RoleReportRelationClient roleReportRelationClient;

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

    @RequestMapping(value = ServiceApi.Roles.RoleUserByUserId,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据人员id，删除其与所有角色组关系")
    public Envelop deleteRoleUserBuUserId(
            @ApiParam(name = "user_id",value = "人员id")
            @RequestParam(value = "user_id") String userId){
        if(StringUtils.isEmpty(userId)){
            return failed("人员id不能为空！");
        }
        boolean bo = roleUserClient.deleteRoleUserBuUserId(userId);
        if(bo){
            return success(null);
        }
        return failed("角色组删除人员失败！");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleUsers,method = RequestMethod.DELETE)
    @ApiOperation(value = "人员id,角色组ids，批量删除人员-角色组关系")
    public Envelop batchDeleteRoleUserRelation(
            @ApiParam(name = "user_id",value = "人员id")
            @RequestParam(value = "user_id") String userId,
            @ApiParam(name = "role_ids",value = "角色组ids")
            @RequestParam(value = "role_ids") String roleIds){
        if(StringUtils.isEmpty(userId)){
            return failed("人员id不能为空！");
        }
        if(StringUtils.isEmpty(roleIds)) {
            return failed("角色组ids不能为空！");
        }
        boolean bo = roleUserClient.batchDeleteRoleUserRelation(userId, roleIds);
        if(bo){
            return success(null);
        }
        return failed("删除失败！");
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

    @RequestMapping(value = "/roles/role_user/userRolesIds",method = RequestMethod.GET)
    @ApiOperation(value = "获取用户所属角色组ids")
    public Envelop getUserRolesIds(
            @ApiParam(name = "user_id",value = "用户id")
            @RequestParam(value = "user_id") String userId){
        Collection<MRoleUser> mRoleUsers = roleUserClient.searchRoleUserNoPaging("userId="+userId);
        String roleIds = "";
        for (MRoleUser m : mRoleUsers){
            roleIds += m.getRoleId()+",";
        }
        if(!StringUtils.isEmpty(roleIds)){
            roleIds = roleIds.substring(0,roleIds.length()-1);
        }
        return success(roleIds);
    }

    private RoleUserModel changeToModel(MRoleUser m) {
        RoleUserModel model = convertToModel(m, RoleUserModel.class);
        //获取用户名
        MUser user = userClient.getUser(m.getUserId());
        model.setUserName(user == null?"":user.getRealName());
        model.setLoginCode(user == null?"":user.getLoginCode());
        model.setUserType(user == null?"":user.getUserType());
        //获取角色名
        MRoles roles = rolesClient.getRolesById(m.getRoleId());
        model.setRoleName(roles == null?"":roles.getName());
        return model;
    }

    @RequestMapping(value = ServiceApi.Roles.NoPageCategoriesAndReport, method = RequestMethod.GET)
    @ApiOperation("获取资源报表类别及报表树")
    public Envelop getAllCategoriesAndReport(
            @ApiParam(name="filters",value="过滤",defaultValue = "")
            @RequestParam(value="filters",required = false)String filters,
            @ApiParam(name="roleId",value="角色Id",defaultValue = "")
            @RequestParam(value="roleId",required = false)String roleId) throws  Exception {
        Envelop envelop = new Envelop();
        try {
            List<MRsReportCategory> categories = rsReportCategoryClient.getAllCategories("");
            List<MRsReportCategoryInfo> mRsReportCategoryInfos = (List<MRsReportCategoryInfo>) convertToModels(categories, new ArrayList<>(categories.size()), MRsReportCategoryInfo.class, null);
            for (MRsReportCategoryInfo category : mRsReportCategoryInfos) {
                String condition = "reportCategoryId=" + category.getId();
                if (!StringUtils.isEmpty(filters)) {
                    condition += ";" + filters;
                }
                List<MRsReport> mRsResources = rsReportClient.queryNoPageResources(condition);
                if (null != mRsResources && mRsResources.size() > 0) {
                    for (MRsReport rp : mRsResources) {
                        setFlag(rp, roleId);
                    }
                }
                category.setReportList(mRsResources);
            }
            //获取已配置的资源报表信息
            List<MRsReportCategoryInfo> mRsReportCategoryInfoList = getReportConfigInfo(roleId);

            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(mRsReportCategoryInfos);
            envelop.setObj(mRsReportCategoryInfoList);
        }catch (Exception e){
            e.printStackTrace();
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    public void setFlag(MRsReport mRsReport, String roleId) {
        ResponseEntity<Collection<MRoleReportRelation>> responseEntity = roleReportRelationClient.searchRoleReportRelation("", "rsReportId=" + mRsReport.getId() + ";roleId=" + roleId, "", 1, 1);
        Collection<MRoleReportRelation> roleReportRelations = responseEntity.getBody();
        if (roleReportRelations != null && roleReportRelations.size() > 0) {
            mRsReport.setFlag(true);
        }
    }

    public List<MRsReportCategoryInfo> getReportConfigInfo(String roleId) {
        List<MRsReportCategory> mRsReportCategories = new ArrayList<>();
        List<MRoleReportRelation> roleReportRelations = roleReportRelationClient.searchRoleReportRelationNoPage("roleId=" + roleId);
        if (null != roleReportRelations && roleReportRelations.size() > 0) {
            for (MRoleReportRelation roleReportRelation : roleReportRelations) {
                ResponseEntity<List<MRsReport>> listResponseEntity = rsReportClient.search("", "id=" + roleReportRelation.getRsReportId(), "", 1, 1);
                List<MRsReport> rsReportList = listResponseEntity.getBody();
                if (null != rsReportList && rsReportList.size() > 0) {
                    MRsReportCategory rsReportCategory = rsReportCategoryClient.getById(rsReportList.get(0).getReportCategoryId());
                    rsReportCategory.setReportList(rsReportList);
                    mRsReportCategories.add(rsReportCategory);
                }
            }
        }
        List<MRsReportCategoryInfo> mRsReportCategoryInfosList = (List<MRsReportCategoryInfo>) convertToModels(mRsReportCategories, new ArrayList<>(mRsReportCategories.size()), MRsReportCategoryInfo.class, null);
        return mRsReportCategoryInfosList;
    }
}
