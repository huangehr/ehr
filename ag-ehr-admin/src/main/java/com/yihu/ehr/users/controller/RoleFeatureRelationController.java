package com.yihu.ehr.users.controller;

import com.yihu.ehr.agModel.user.RoleFeatureRelationModel;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.apps.service.AppFeatureClient;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.app.MAppFeature;
import com.yihu.ehr.model.user.MRoleFeatureRelation;
import com.yihu.ehr.users.service.RoleFeatureRelationClient;
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
@Api(value = "roleFeature",description = "角色组功能权限配置",tags = "")
public class RoleFeatureRelationController extends BaseController {
    @Autowired
    private RoleFeatureRelationClient roleFeatureRelationClient;

    @Autowired
    AppFeatureClient appFeatureClient;

    @RequestMapping(value = ServiceApi.Roles.RoleFeature,method = RequestMethod.POST)
    @ApiOperation(value = "为角色组配置功能权限，单个")
    public Envelop createRoleFeature(
            @ApiParam(name = "data_json",value = "角色组-功能权限关系Json串")
            @RequestParam(value = "data_json") String dataJson){
        MRoleFeatureRelation mRoleFeatureRelation = roleFeatureRelationClient.createRoleFeature(dataJson);
        if(null == mRoleFeatureRelation){
            return failed("添加功能权限失败！");
        }
        return success(null);
    }
    @RequestMapping(value = ServiceApi.Roles.RoleFeature,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据角色组id,权限Id删除角色组-权限关系")
    public Envelop deleteRoleFeature(
            @ApiParam(name = "feature_id",value = "功能权限id")
            @RequestParam(value = "feature_id") String featureId,
            @ApiParam(name = "role_id",value = "角色组id")
            @RequestParam(value = "role_id") String roleId){
        if(StringUtils.isEmpty(featureId)){
            return failed("权限id不能为空！");
        }
        if(StringUtils.isEmpty(roleId)){
            return failed("角色组id不能为空！");
        }
        boolean bo = roleFeatureRelationClient.deleteRoleFeature(featureId, roleId);
        if(bo){
            return success(null);
        }
        return failed("删除权限失败！");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleFeatureByRoleId,method = RequestMethod.DELETE)
    @ApiOperation(value = "根据角色组id删除所配置的应用权限")
    public Envelop deleteRoleFeatureRelationByRoleId(
            @ApiParam(name = "role_id",value = "角色组id")
            @RequestParam(value = "role_id") Long roleId){
        boolean bo = roleFeatureRelationClient.deleteRoleFeatureRelationByRoleId(roleId);
        if(bo){
            return success(null);
        }
        return failed("删除权限失败！");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleFeatures,method = RequestMethod.PUT)
    @ApiOperation(value = "根据角色组、新增应用权限id、删除应用权限id批量修改,一对多")
    public Envelop batchUpdateRoleFeatureRelation(
            @ApiParam(name = "role_id",value = "角色组Id")
            @RequestParam(value = "role_id") Long roleId,
            @ApiParam(name = "feature_ids_add",value = "要新增的featureIds",defaultValue = "")
            @RequestParam(name = "feature_ids_add",required = false) Long[] addFeatureIds,
            @ApiParam(name = "feature_ids_delete",value = "要删除的featureIds",defaultValue = "")
            @RequestParam(value = "feature_ids_delete",required = false) String deleteFeatureIds){
        if(roleId == null){
            return failed("角色组id不能为空！");
        }
        boolean bo = roleFeatureRelationClient.batchUpdateRoleFeatureRelation(roleId,addFeatureIds,deleteFeatureIds);
        if(bo){
            return success(null);
        }
        return failed("");
    }

    @RequestMapping(value = ServiceApi.Roles.RoleFeatures,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组-功能权限关系列表---分页")
    public Envelop searchRoleFeature(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,roleId,featureId")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception{
        List<RoleFeatureRelationModel> relationModels = new ArrayList<>();
        ResponseEntity<Collection<MRoleFeatureRelation>> responseEntity = roleFeatureRelationClient.searchRoleFeature(fields, filters, sorts, size, page);
        Collection<MRoleFeatureRelation> mRoleFeatureRelations  = responseEntity.getBody();
        for (MRoleFeatureRelation m : mRoleFeatureRelations){
            RoleFeatureRelationModel roleFeatureRelationModel = changeToModel(m);
            relationModels.add(roleFeatureRelationModel);
        }
        Integer totalCount = getTotalCount(responseEntity);
        return getResult(relationModels,totalCount,page,size);
    }
    @RequestMapping(value = ServiceApi.Roles.RoleFeaturesNoPage,method = RequestMethod.GET)
    @ApiOperation(value = "查询角色组-功能权限关系列表---不分页")
    public Envelop searchRoleFeatureNoPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空检索全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters) throws  Exception{
        Envelop envelop = new Envelop();
        List<RoleFeatureRelationModel> relationModels = new ArrayList<>();
        Collection<MRoleFeatureRelation> mRoleFeatureRelations = roleFeatureRelationClient.searchRoleFeatureNoPaging(filters);
        for (MRoleFeatureRelation m : mRoleFeatureRelations){
            RoleFeatureRelationModel roleFeatureRelationModel = changeToModel(m);
            relationModels.add(roleFeatureRelationModel);
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(relationModels);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Roles.RoleFeatureExistence, method = RequestMethod.GET)
    @ApiOperation(value = "通用根据过滤条件，判断是否存在")
    public Envelop getAppFeaturesFilter(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters){
        boolean bo = roleFeatureRelationClient.getAppFeaturesFilter(filters);
        if(bo){
            return success(null);
        }
        return failed("");
    }

    private RoleFeatureRelationModel changeToModel(MRoleFeatureRelation m) {
        RoleFeatureRelationModel model = convertToModel(m, RoleFeatureRelationModel.class);
        //获取应用权限名称
        MAppFeature appFeature = appFeatureClient.getAppFeature(m.getFeatureId()+"");
        model.setFeatureName(appFeature == null?"":appFeature.getName());
        return model;
    }

    @RequestMapping(value = "/roles/role_features_update",method = RequestMethod.PUT)
    @ApiOperation(value = "根据角色组、应用权限id批量修改,一对多")
    public Envelop batchUpdateRoleFeatureRelation(
            @ApiParam(name = "role_id",value = "角色组Id")
            @RequestParam(value = "role_id") Long roleId,
            @ApiParam(name = "feature_ids",value = "选择的应用权限ids",defaultValue = "")
            @RequestParam(name = "feature_ids",required = false) Long[] featureIds){
        boolean bo = false;
        //根据传入的参数获取新增、删除ids
        if(featureIds == null){
            //删除角色组下配置的所有应用权限
            bo = roleFeatureRelationClient.deleteRoleFeatureRelationByRoleId(roleId);
            if(bo)
                return success(null);
            return failed("");
        }
        Collection<MRoleFeatureRelation> mRoleFeatureRelations = roleFeatureRelationClient.searchRoleFeatureNoPaging("roleId=" + roleId);
        if(mRoleFeatureRelations == null || mRoleFeatureRelations.size() == 0){
            //原角色组不存在配置权限则直接新增
            bo = roleFeatureRelationClient.batchUpdateRoleFeatureRelation(roleId, featureIds, "");
            if(bo)
                return success(null);
            return failed("");
        }
        //获取新增和删除ids
        List<Long> newFeatureIds = new ArrayList<>();
        for(int i =0;i<featureIds.length;i++){
            newFeatureIds.add(featureIds[i]);
        }
        String deleteFeatureIds = "";
        StringBuffer deleteBuffer = new StringBuffer();
        for(MRoleFeatureRelation m:mRoleFeatureRelations){
            Long featureId = m.getFeatureId();
            if(newFeatureIds.contains(featureId)){
                newFeatureIds.remove(featureId);
                continue;
            }
            deleteBuffer.append(featureId);
            deleteBuffer.append(",");
        }
        if(deleteBuffer.length()>0){
            deleteFeatureIds = deleteBuffer.substring(0,deleteBuffer.length()-1);
        }
        Long[] addFeatureIds = newFeatureIds.toArray(new Long[newFeatureIds.size()]);
        bo = roleFeatureRelationClient.batchUpdateRoleFeatureRelation(roleId, addFeatureIds, deleteFeatureIds);
        if(bo)
            return success(null);
        return failed("");
    }
}
