package com.yihu.ehr.users.controller;

import com.fasterxml.jackson.databind.JavaType;
import com.yihu.ehr.agModel.user.PlatformAppRolesModel;
import com.yihu.ehr.agModel.user.PlatformAppRolesTreeModel;
import com.yihu.ehr.agModel.user.RolesModel;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.apps.service.AppClient;
import com.yihu.ehr.apps.service.AppFeatureClient;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.entity.organizations.OrgSaas;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.model.app.MAppFeature;
import com.yihu.ehr.model.user.*;
import com.yihu.ehr.users.service.*;
import com.yihu.ehr.util.array.ListUtils;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
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
@Api(value = "roles",description = "角色管理", tags = {"安全管理-角色管理"})
public class RolesController extends BaseController {
    @Autowired
    private RolesClient rolesClient;
    @Autowired
    private RoleUserClient roleUserClient;
    @Autowired
    private RoleFeatureRelationClient roleFeatureRelationClient;
    @Autowired
    private RoleAppRelationClient roleAppRelationClient;
    @Autowired
    private RoleApiRelationClient roleApiRelationClient;
    @Autowired
    private AppClient appClient;
    @Autowired
    private AppFeatureClient appFeatureClient;

    @RequestMapping(value = ServiceApi.Roles.Role,method = RequestMethod.POST)
    @ApiOperation(value = "新增角色组")
    public Envelop createRoles(
            @ApiParam(name = "data_json",value = "新增角色组Json字符串")
            @RequestParam(value = "data_json") String dataJson){
        MRoles model=new MRoles();
        try {
            model= objectMapper.readValue(dataJson, MRoles.class);
            if(null!=model&&model.getDescription().length()>250){
                return failed("角色组描述不能大于250字");
            }
        }catch (Exception e){

        }
        MRoles mRoles = rolesClient.createRoles(dataJson);
        if(null == mRoles){
            return failed("新增角色组失败");
        }
        return success(convertToModel(mRoles, RolesModel.class));
    }
    @RequestMapping(value = ServiceApi.Roles.Role,method = RequestMethod.PUT)
    @ApiOperation(value = "修改角色组")
    public Envelop updateRoles(
            @ApiParam(name = "data_json",value = "修改角色组Json字符串")
            @RequestParam(value = "data_json") String dataJson){
        MRoles model=new MRoles();
        try {
            model= objectMapper.readValue(dataJson, MRoles.class);
            if(null!=model&&model.getDescription().length()>250){
                return failed("角色组描述不能大于250字");
            }
        }catch (Exception e){

        }
        MRoles mRoles = rolesClient.updateRoles(dataJson);
        if(null == mRoles){
            return failed("修改角色组失败");
        }
        return success(convertToModel(mRoles, RolesModel.class));
    }
    @RequestMapping(value = ServiceApi.Roles.RoleId,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据角色组id删除")
    public Envelop deleteRoles(
            @ApiParam(name = "id",value = "角色组id")
            @PathVariable(value = "id") long id){
        //判断是否已配置人员
        Collection<MRoleUser> mRoleUsers = roleUserClient.searchRoleUserNoPaging("roleId=" + id);
        if(mRoleUsers != null&&mRoleUsers.size()>0){
            return failed("已配置人员角色组不能删除！");
        }
        //判断是否已配置应用权限
        Collection<MRoleFeatureRelation> mRelation = roleFeatureRelationClient.searchRoleFeatureNoPaging("roleId="+id);
        if(mRelation != null && mRelation.size()>0){
            return failed("已配置应用权限角色组不能删除！");
        }
        //判断是否已配置接入应用
        Collection<MRoleAppRelation> mRoleAppRelation = roleAppRelationClient.searchRoleAppNoPaging("roleId="+id);
        if(mRoleAppRelation != null && mRoleAppRelation.size()>0){
            return failed("已配置接入应用角色组不能删除！");
        }
        //判断是否已配应用api
        Collection<MRoleApiRelation> mRoleApiRelations = roleApiRelationClient.searchRoleApiRelationNoPaging("roleId="+id);
        if(mRoleApiRelations != null && mRoleApiRelations.size()>0){
            return failed("已配置应用api的角色组不能删除！");
        }

        boolean bo = rolesClient.deleteRoles(id);
        if(bo){
            return success(null);
        }
        return failed("角色组删除失败！");
    }
    @RequestMapping(value = ServiceApi.Roles.RoleId,method = RequestMethod.GET)
    @ApiOperation(value = "根据角色组id查询")
    public Envelop getRolesById(
            @ApiParam(name = "id",value = "角色组id")
            @PathVariable(value = "id") long id){
        MRoles mRoles = rolesClient.getRolesById(id);
        if(null ==mRoles){
            return failed("获取角色组失败！");
        }
        return success(convertToModel(mRoles,RolesModel.class));
    }
    @RequestMapping(value = ServiceApi.Roles.Roles,method = RequestMethod.GET)
    @ApiOperation(value = "查询用户角色列表---分页")
    public Envelop searchRoles(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,code,name,description,appId,type")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+code,+name")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception{
        List<RolesModel> rolesModelList = new ArrayList<>();
        ResponseEntity<Collection<MRoles>> responseEntity = rolesClient.searchRoles(fields, filters, sorts, size, page);
        Collection<MRoles> mRoles  = responseEntity.getBody();
        for (MRoles m : mRoles){
            RolesModel rolesModel = convertToModel(m, RolesModel.class);
            rolesModelList.add(rolesModel);
        }
        Integer totalCount = getTotalCount(responseEntity);
        return getResult(rolesModelList,totalCount,page,size);
    }

    @RequestMapping(value = ServiceApi.Roles.RolesNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询用户角色列表---不分页")
    public Envelop searchRolesNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        Envelop envelop = new Envelop();
        Collection<MRoles> mRoles = rolesClient.searchRolesNoPaging(filters);
        List<RolesModel> rolesModelList = (List<RolesModel>)convertToModels(mRoles,
                new ArrayList<RolesModel>(),RolesModel.class,null);
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(rolesModelList);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Roles.RoleNameExistence,method = RequestMethod.GET)
    @ApiOperation(value = "角色组名称是否已存在" )
    public Envelop isNameExistence(
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId,
            @ApiParam(name = "name",value = "角色组名")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "type",value = "角色组类别")
            @RequestParam(value = "type") String type){
        boolean bo = rolesClient.isNameExistence(appId,name,type);
        if(bo){
            return success(null);
        }
        return failed("");
    }
    @RequestMapping(value = ServiceApi.Roles.RoleCodeExistence,method = RequestMethod.GET)
    @ApiOperation(value = "角色组代码是否已存在" )
    public Envelop isCodeExistence(
            @ApiParam(name = "app_id",value = "应用id")
            @RequestParam(value = "app_id") String appId,
            @ApiParam(name = "code",value = "角色组代码")
            @RequestParam(value = "code") String code,
            @ApiParam(name = "type",value = "角色组类别")
            @RequestParam(value = "type") String type){
        boolean  bo = rolesClient.isCodeExistence(appId,code,type);
        if(bo){
            return success(null);
        }
        return failed("");
    }
//    private RolesModel changeToModel(MRoles m) {
//        RolesModel rolesModel = convertToModel(m, RolesModel.class);
//        //获取角色组类别字典
//        return rolesModel;
//    }

    @RequestMapping(value = "/roles/platformAppRolesTree",method = RequestMethod.GET)
    @ApiOperation(value = "获取平台应用角色组列表,tree" )
    public Envelop getPlatformAppRolesTree(
            @ApiParam(name = "type",value = "角色组类型，应用角色/用户角色字典值")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "source_type",value = "平台应用sourceType字典值")
            @RequestParam(value = "source_type") String sourceType){
        if(StringUtils.isEmpty(type)){
            return failed("角色组类型不能为空！");
        }
        if(StringUtils.isEmpty(sourceType)){
            return failed("平台应用类型不能为空！！");
        }
        Envelop envelop = new Envelop();
        //平台应用-应用表中source_type为1
        Collection<MApp> mApps =  appClient.getAppsNoPage("sourceType="+sourceType);
        List<PlatformAppRolesTreeModel> appRolesTreeModelList = new ArrayList<>();

        //平台应用-角色组对象模型列表
        for(MApp mApp : mApps){
            Collection<MRoles> mRoles = rolesClient.searchRolesNoPaging("appId=" + mApp.getId() + ";type=" + type);
            List<PlatformAppRolesTreeModel> roleTreeModelList = new ArrayList<>();
            for(MRoles m : mRoles){
                PlatformAppRolesTreeModel modelTree = new PlatformAppRolesTreeModel();
                modelTree.setId(m.getId()+"");
                modelTree.setName(m.getName());
                modelTree.setType("1");
                modelTree.setPid(mApp.getId());
                modelTree.setChildren(null);
                roleTreeModelList.add(modelTree);
            }
            if(roleTreeModelList.size()==0){
                continue;
            }
            PlatformAppRolesTreeModel app = new PlatformAppRolesTreeModel();
            app.setId(mApp.getId());
            app.setName(mApp.getName());
            app.setType("0");
            app.setPid(null);
            app.setChildren(roleTreeModelList);
            appRolesTreeModelList.add(app);
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(appRolesTreeModelList);
        return envelop;
    }

    @RequestMapping(value = "/roles/app_user_roles",method = RequestMethod.GET)
    @ApiOperation(value = "获取平台应用与所属用户角色组ids,names组成的对象集合，不分页" )
    public Envelop getPlatformAppRolesView(
            @ApiParam(name = "type",value = "用户角色组的字典值")
            @RequestParam(value = "type") String type,
            @ApiParam(name = "source_type",value = "平台应用sourceType字典值")
            @RequestParam(value = "source_type") String sourceType,
            @ApiParam(name = "user_id",value = "用户id")
            @RequestParam(value = "user_id") String userId){
        if(StringUtils.isEmpty(type)){
            return failed("角色组类型不能为空！");
        }
        if(StringUtils.isEmpty(sourceType)){
            return failed("平台应用类型不能为空！！");
        }
        if(StringUtils.isEmpty(userId)){
            return failed("用户id不能为空！");
        }
        Collection<MRoleUser> mRoleUsers = roleUserClient.searchRoleUserNoPaging("userId=" + userId);
        if(mRoleUsers == null){
            return success(null);
        }
        StringBuffer buffer = new StringBuffer();
        for(MRoleUser m : mRoleUsers){
            buffer.append(m.getRoleId());
            buffer.append(",");
        }
        String rolesIds = buffer.substring(0,buffer.length()-1);
        Envelop envelop = new Envelop();
        Collection<MApp> mApps =  appClient.getAppsNoPage("sourceType="+sourceType);
        //平台应用-角色组对象模型列表
        List<PlatformAppRolesModel> appRolesModelList = new ArrayList<>();
        for(MApp mApp : mApps){
            //查找用户所属角色组
            Collection<MRoles> mRoles = rolesClient.searchRolesNoPaging("appId=" + mApp.getId()+";type="+type+";id="+rolesIds);
            if(mRoles == null || mRoles.size() <= 0){
                continue;
            }
            PlatformAppRolesModel model = new PlatformAppRolesModel();
            String roleIds = "";
            String roleNames = "";
            for(MRoles m : mRoles){
                roleIds += m.getId()+",";
                roleNames += m.getName()+",";
            }
            if(!StringUtils.isEmpty(roleIds)){
                roleIds = roleIds.substring(0,roleIds.length()-1);
            }
            if(!StringUtils.isEmpty(roleNames)){
                roleNames = roleNames.substring(0,roleNames.length()-1);
            }
            model.setAppId(mApp.getId());
            model.setAppName(mApp.getName());
            model.setRoleId(roleIds);
            model.setRoleName(roleNames);
            appRolesModelList.add(model);
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(appRolesModelList);
        return envelop;
    }

    @RequestMapping(value = "/roles/user/features",method = RequestMethod.GET)
    @ApiOperation(value = "获取用户的权限信息" )
    public Envelop getPlatformAppRolesView(
            @ApiParam(name = "user_id",value = "用户编号")
            @RequestParam(value = "user_id") String userId){

        try {
            Envelop envelop = success(null);
            Collection<MRoleUser> roles = roleUserClient.searchRoleUserNoPaging("userId=" + userId);
            if(roles!=null){
                String ids = ListUtils.toString(roles, MRoleUser.class, "getRoleId");
                if(ids.length()>0){
                    Collection<MRoleFeatureRelation> featureRelations = roleFeatureRelationClient.searchRoleFeatureNoPaging("roleId=" + ids);
                    ids = ListUtils.toString(featureRelations, MRoleFeatureRelation.class, "getFeatureId");
                    if(ids.length()>0){
                        Collection<MAppFeature> features = appFeatureClient.getAppFeatureNoPage("id=" + ids);
                        envelop.setDetailModelList((List<MAppFeature>) convertToModels(features,
                                new ArrayList<>(), MAppFeature.class, null));
                    }
                }
            }
            return envelop;
        } catch (Exception e) {
            e.printStackTrace();
            return failed("查询用户权限出错！");
        }
    }
}
