package com.yihu.ehr.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.user.RolesModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.user.MRoleAppRelation;
import com.yihu.ehr.model.user.MRoleFeatureRelation;
import com.yihu.ehr.model.user.MRoleUser;
import com.yihu.ehr.model.user.MRoles;
import com.yihu.ehr.users.service.RoleAppRelationClient;
import com.yihu.ehr.users.service.RoleFeatureRelationClient;
import com.yihu.ehr.users.service.RoleUserClient;
import com.yihu.ehr.users.service.RolesClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(value = "roles",description = "角色管理",tags = "")
public class RolesController extends BaseController {
    @Autowired
    private RolesClient rolesClient;

    @Autowired
    private RoleUserClient roleUserClient;

    @Autowired
    private RoleFeatureRelationClient roleFeatureRelationClient;

    @Autowired
    private RoleAppRelationClient roleAppRelationClient;

    @RequestMapping(value = ServiceApi.Roles.Role,method = RequestMethod.POST)
    @ApiOperation(value = "新增角色组")
    public Envelop createRoles(
            @ApiParam(name = "data_json",value = "新增角色组Json字符串")
            @RequestParam(value = "data_json") String dataJson){
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
        Collection<MRoleFeatureRelation> mRelation = roleFeatureRelationClient.searchRoleFeatureNoPaging("roleId"+id);
        if(mRelation != null && mRelation.size()>0){
            return failed("已配置应用权限角色组不能删除！");
        }
        //判断是否已配置接入应用
        Collection<MRoleAppRelation> mRoleAppRelation = roleAppRelationClient.searchRoleAppNoPaging("roleId"+id);
        if(mRoleAppRelation != null && mRoleAppRelation.size()>0){
            return failed("已配置接入应用角色组不能删除！");
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
            @ApiParam(name = "name",value = "角色组名")
            @RequestParam(value = "name") String name){
        boolean bo = rolesClient.isNameExistence(name);
        if(bo){
            return success(null);
        }
        return failed("");
    }
    @RequestMapping(value = ServiceApi.Roles.RoleCodeExistence,method = RequestMethod.GET)
    @ApiOperation(value = "角色组代码是否已存在" )
    public Envelop isCodeExistence(
            @ApiParam(name = "code",value = "角色组代码")
            @RequestParam(value = "code") String code){
        boolean  bo = rolesClient.isCodeExistence(code);
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
}
