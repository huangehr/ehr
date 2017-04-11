package com.yihu.ehr.users.controller;

import com.yihu.ehr.agModel.user.RoleApiRelationModel;
import com.yihu.ehr.agModel.user.RoleAppRelationModel;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.apps.service.AppApiClient;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.app.MAppApi;
import com.yihu.ehr.model.user.MRoleApiRelation;
import com.yihu.ehr.users.service.RoleApiRelationClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by yww on 2016/7/8.
 */
@EnableFeignClients
@RequestMapping(ApiVersion.Version1_0+"/admin")
@RestController
@Api(value = "roleApi",description = "角色组-Api关系管理",tags = "")
public class RoleApiRelationController extends BaseController {
    @Autowired
    private RoleApiRelationClient roleApiRelationClient;

    @Autowired
    private AppApiClient appApiClient;

    @RequestMapping(value = ServiceApi.Roles.RoleApi,method = RequestMethod.POST)
    @ApiOperation(value = "为角色组配置api权限")
    public Envelop createRoleApiRelation(
            @ApiParam(name = "data_json",value = "角色组-api关系对象json串")
            @RequestParam(value = "data_json") String dataJson){
        MRoleApiRelation mRoleApiRelation = roleApiRelationClient.createRoleApiRelation(dataJson);
        if(null == mRoleApiRelation){
            return failed("新增失败！");
        }
        return success(convertToModel(mRoleApiRelation, RoleAppRelationModel.class));
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApi,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据apiId,角色组id删除角色组-api关系")
    public Envelop deleteRoleApiRelation(
            @ApiParam(name = "api_id",value = "应用接口id" )
            @RequestParam(value = "api_id") String apiId,
            @ApiParam(name = "role_id",value = "角色组id")
            @RequestParam(value = "role_id") String roleId){
        boolean bo = roleApiRelationClient.deleteRoleApiRelation(apiId, roleId);
        if(bo){
            return success(null);
        }
        return failed("删除失败！");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApiByRoleId,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据角色组id删除所配置的api")
    public Envelop deleteRoleApiRelationByRoleId(
            @ApiParam(name = "role_id",value = "角色组id")
            @RequestParam(value = "role_id") Long roleId){
        boolean bo = roleApiRelationClient.deleteRoleApiRelationByRoleId(roleId);
        if(bo){
            return success(null);
        }
        return failed("删除失败！");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApis,method = RequestMethod.PUT)
    @ApiOperation(value = "根据角色组、新增应用接口id、删除应用接口id批量修改,一对多")
    public Envelop batchUpdateRoleApiRelation(
            @ApiParam(name = "role_id",value = "角色组Id")
            @RequestParam(value = "role_id") Long roleId,
            @ApiParam(name = "api_ids_add",value = "要新增的apiIds",defaultValue = "")
            @RequestParam(name = "api_ids_add",required = false) Long[] addApiIds,
            @ApiParam(name = "api_ids_delete",value = "要删除的apiIds",defaultValue = "")
            @RequestParam(value = "api_ids_delete",required = false) String deleteApiIds) throws Exception{
        if(roleId == null){
            return failed("角色组id不能为空！");
        }
        boolean bo = roleApiRelationClient.batchUpdateRoleApiRelation(roleId,addApiIds,deleteApiIds);
        if(bo){
            return success(null);
        }
        return failed("");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApis,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组-api关系列表---分页")
    public Envelop searchRoleApiRelations(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,roleId,apiId")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception{
        List<RoleApiRelationModel> relationModelList = new ArrayList<>();
        ResponseEntity<Collection<MRoleApiRelation>> responseEntity = roleApiRelationClient.searchRoleApiRelations(fields, filters, sorts, size, page);
        Collection<MRoleApiRelation> mRoleApiRelations  = responseEntity.getBody();
        for (MRoleApiRelation m : mRoleApiRelations){
            RoleApiRelationModel rolesModel = convertToModel(m, RoleApiRelationModel.class);
            relationModelList.add(rolesModel);
        }
        Integer totalCount = getTotalCount(responseEntity);
        return getResult(relationModelList,totalCount,page,size);

    }
    @RequestMapping(value = ServiceApi.Roles.RoleApisNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组与-api关系列表---不分页")
    public Envelop searchRoleApiRelationNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        Envelop envelop = new Envelop();
        List<RoleApiRelationModel> relationModelList = new ArrayList<>();
        Collection<MRoleApiRelation> mRoleApiRelations = roleApiRelationClient.searchRoleApiRelationNoPaging(filters);
        for (MRoleApiRelation m : mRoleApiRelations){
            RoleApiRelationModel roleApiRelationModel = convertToModel(m,RoleApiRelationModel.class);
            relationModelList.add(roleApiRelationModel);
        }
        envelop.setDetailModelList(relationModelList);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Roles.RoleApisExistence, method = RequestMethod.GET)
    @ApiOperation(value = "通用根据过滤条件，判断是否存在")
    public Envelop getAppApiFilter(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters){
        boolean bo = roleApiRelationClient.getAppApiFilter(filters);
        if(bo){
            return success(null);
        }
        return failed("");
    }

    private RoleApiRelationModel changeToModel(MRoleApiRelation m) {
        RoleApiRelationModel model = convertToModel(m, RoleApiRelationModel.class);
        //获取角色组类别字典
        MAppApi appApi = appApiClient.getAppApi(m.getId()+"");
        model.setApiName(appApi==null?"":appApi.getName());
        return model;
    }

    @RequestMapping(value = "/roles/role_apis_update",method = RequestMethod.PUT)
    @ApiOperation(value = "根据角色组、应用接口id批量修改,一对多")
    public Envelop batchUpdateRoleApiRelation(
            @ApiParam(name = "role_id",value = "角色组Id")
            @RequestParam(value = "role_id") Long roleId,
            @ApiParam(name = "api_ids",value = "选择的应用apiIds",defaultValue = "")
            @RequestParam(name = "api_ids",required = false) Long[] apiIds){
        boolean bo = false;
        //根据传入的参数获取新增、删除ids
        if(apiIds == null){
            //删除角色组下配置的所有应用api
            // bo = roleApiRelationClient.deleteByRoleId(roleId);
            bo = roleApiRelationClient.deleteRoleApiRelationByRoleId(roleId);
            if(bo)
                return success(null);
            return failed("");
        }
        Collection<MRoleApiRelation> mRoleApiRelations = roleApiRelationClient.searchRoleApiRelationNoPaging("roleId=" + roleId);
        if(mRoleApiRelations == null || mRoleApiRelations.size() == 0){
            //原角色组不存在配置api则直接新增
            bo = roleApiRelationClient.batchUpdateRoleApiRelation(roleId,apiIds,"");
            if(bo)
                return success(null);
            return failed("");
        }
        //获取新增和删除ids
        List<Long> newApiIds = new ArrayList<>();
        for(int i =0;i<apiIds.length;i++){
            newApiIds.add(apiIds[i]);
        }
        String deleteApiIds = "";
        StringBuffer deleteBuffer = new StringBuffer();
        for(MRoleApiRelation m:mRoleApiRelations){
            Long apiId = m.getApiId();
            if(newApiIds.contains(apiId)){
                newApiIds.remove(apiId);
                continue;
            }
            deleteBuffer.append(apiId);
            deleteBuffer.append(",");
        }
        if(deleteBuffer.length()>0){
            deleteApiIds = deleteBuffer.substring(0,deleteBuffer.length()-1);
        }
        Long[] addApiIds = newApiIds.toArray(new Long[newApiIds.size()]);
        bo = roleApiRelationClient.batchUpdateRoleApiRelation(roleId,addApiIds,deleteApiIds);
        if(bo)
            return success(null);
        return failed("");
    }

}
