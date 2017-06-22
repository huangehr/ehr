package com.yihu.ehr.apps.controller;

import com.yihu.ehr.agModel.app.AppFeatureModel;
import com.yihu.ehr.agModel.patient.PatientModel;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.apps.service.AppFeatureClient;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.app.MAppFeature;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.geography.MGeography;
import com.yihu.ehr.model.patient.MDemographicInfo;
import com.yihu.ehr.model.portal.MPortalNotice;
import com.yihu.ehr.model.user.MRoleFeatureRelation;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.users.service.RoleFeatureRelationClient;
import com.yihu.ehr.util.datetime.DateTimeUtil;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by linz on 2016年7月8日11:30:18.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "AppFeature", description = "应用功能列表维护", tags = {"应用管理-应用功能列表维护"})
public class AppFeatureController extends BaseController {

    @Autowired
    AppFeatureClient appFeatureClient;

    @Autowired
    private ConventionalDictEntryClient conDictEntryClient;

    @Autowired
    private RoleFeatureRelationClient roleFeatureRelationClient;

    @RequestMapping(value = ServiceApi.AppFeature.AppFeatures, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppFeature列表")
    public Envelop getAppFeatures(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sort", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sort", required = false) String sort,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page, @RequestParam(value = "roleId", required = false) String roleId) {
        ResponseEntity<List<MAppFeature>> responseEntity = appFeatureClient.getAppFeatures(fields, filters, sort, size, page);
        List<MAppFeature> mAppFeatureList = responseEntity.getBody();
        List<AppFeatureModel> appFeatureModels = new ArrayList<>();
        for (MAppFeature mAppFeature : mAppFeatureList) {
            AppFeatureModel appFeatureModel = new AppFeatureModel();
            BeanUtils.copyProperties(mAppFeature, appFeatureModel);
            appFeatureModel.setRoleId(roleId);
            converModelName(appFeatureModel);
            appFeatureModels.add(appFeatureModel);
        }
        Integer totalCount = getTotalCount(responseEntity);
        Envelop envelop = getResult(appFeatureModels, totalCount, page, size);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppFeature.AppFeatures, method = RequestMethod.POST)
    @ApiOperation(value = "创建AppFeature")
    public Envelop createAppFeature(
            @ApiParam(name = "model", value = "对象JSON结构体", allowMultiple = true, defaultValue = "")
            @RequestParam(value = "model", required = false) String model) {
        Envelop envelop = new Envelop();
        MAppFeature mAppFeature = appFeatureClient.createAppFeature(model);
        if (mAppFeature == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("保存失败！");
            return envelop;
        }
        AppFeatureModel appFeatureModel = new AppFeatureModel();
        BeanUtils.copyProperties(mAppFeature, appFeatureModel);
        converModelName(appFeatureModel);
        envelop.setSuccessFlg(true);
        envelop.setObj(appFeatureModel);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppFeature.AppFeature, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppFeature")
    public Envelop getAppFeature(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) {
        Envelop envelop = new Envelop();
        MAppFeature mAppFeature = appFeatureClient.getAppFeature(id);
        AppFeatureModel appFeatureModel = new AppFeatureModel();
        if (mAppFeature == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取数据失败！");
            return envelop;
        }
        BeanUtils.copyProperties(mAppFeature, appFeatureModel);
        envelop.setSuccessFlg(true);
        envelop.setObj(appFeatureModel);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppFeature.AppFeatures, method = RequestMethod.PUT)
    @ApiOperation(value = "更新AppFeature")
    public Envelop updateAppFeature(
            @ApiParam(name = "model", value = "对象JSON结构体", allowMultiple = true)
            @RequestParam(value = "model", required = false) String appFeature) {
        Envelop envelop = new Envelop();
        MAppFeature mAppFeature = appFeatureClient.createAppFeature(appFeature);
        AppFeatureModel appFeatureModel = new AppFeatureModel();
        if (mAppFeature == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("更新数据失败！");
            return envelop;
        }
        BeanUtils.copyProperties(mAppFeature, appFeatureModel);
        converModelName(appFeatureModel);
        envelop.setSuccessFlg(true);
        envelop.setObj(appFeatureModel);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppFeature.AppFeature, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除AppFeature")
    Envelop deleteAppFeature(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) {
        Envelop envelop = new Envelop();
        Boolean isDelete = appFeatureClient.deleteAppFeature(id);
        envelop.setSuccessFlg(isDelete);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppFeature.FilterFeatureList, method = RequestMethod.GET)
    @ApiOperation(value = "存在性校验")
    Envelop isExitAppFeature(
            @ApiParam(name = "filters", value = "filters", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) {
        Envelop envelop = new Envelop();
        try {
            Boolean isExit = appFeatureClient.isExitAppFeature(filters);
            envelop.setSuccessFlg(true);
            envelop.setObj(isExit);
        } catch (Exception e) {
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppFeature.FilterFeatureNoPage, method = RequestMethod.GET)
    @ApiOperation(value = "获取过滤AppFeature列表")
    public Envelop getAppFeatureNoPage(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "roleId", required = false) String roleId) {
        Collection<MAppFeature> mAppFeatures = appFeatureClient.getAppFeatureNoPage(filters);
        Envelop envelop = new Envelop();
        List<AppFeatureModel> appFeatureModels = new ArrayList<>();
        for (MAppFeature mAppFeature : mAppFeatures) {
            AppFeatureModel appFeatureModel = convertToModel(mAppFeature, AppFeatureModel.class);
            if (StringUtils.isNotBlank(roleId)) {
                appFeatureModel.setRoleId(roleId);
            }
            converModelName(appFeatureModel);
            appFeatureModels.add(appFeatureModel);
        }
        envelop.setDetailModelList(appFeatureModels);
        return envelop;
    }

    @RequestMapping(value = "/role_app_feature/no_paging", method = RequestMethod.GET)
    @ApiOperation(value = "获取角色组的AppFeature列表")
    public Envelop getRoleAppFeatureNoPage(
            @ApiParam(name = "role_id", value = "角色组id")
            @RequestParam(value = "role_id") String roleId) {
        Collection<MRoleFeatureRelation> mRoleFeatureRelations = roleFeatureRelationClient.searchRoleFeatureNoPaging("roleId=" + roleId);
        String featureIds = "";
        for (MRoleFeatureRelation m : mRoleFeatureRelations) {
            featureIds += m.getFeatureId() + ",";
        }
        if (!StringUtils.isEmpty(featureIds)) {
            featureIds = featureIds.substring(0, featureIds.length() - 1);
        }
        Collection<MAppFeature> mAppFeatures = appFeatureClient.getAppFeatureNoPage("id=" + featureIds);
        Envelop envelop = new Envelop();
        List<AppFeatureModel> appFeatureModels = new ArrayList<>();
        for (MAppFeature mAppFeature : mAppFeatures) {
            AppFeatureModel appFeatureModel = convertToModel(mAppFeature, AppFeatureModel.class);
            appFeatureModels.add(appFeatureModel);
        }
        if (appFeatureModels.size() > 0) {
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(appFeatureModels);
            return envelop;
        }
        return failed("");
    }

    /**
     * 格式化字典数据
     *
     * @param appFeatureModel
     */
    private void converModelName(AppFeatureModel appFeatureModel) {
        //应用菜单类型
        if (!StringUtils.isEmpty(appFeatureModel.getType())) {
            MConventionalDict catalopDict = conDictEntryClient.getApplicationMenuType(appFeatureModel.getType());
            appFeatureModel.setTypeName(catalopDict == null ? "" : catalopDict.getValue());
        }
        //审计等级
        if (!StringUtils.isEmpty(appFeatureModel.getAuditLevel())) {
            MConventionalDict catalopDict = conDictEntryClient.getAuditLevel(appFeatureModel.getAuditLevel());
            appFeatureModel.setAuditLevelName(catalopDict == null ? "" : catalopDict.getValue());
        }
        //开放等级
        if (!StringUtils.isEmpty(appFeatureModel.getOpenLevel())) {
            MConventionalDict catalopDict = conDictEntryClient.getOpenLevel(appFeatureModel.getOpenLevel());
            appFeatureModel.setOpenLevelName(catalopDict == null ? "" : catalopDict.getValue());
        }
        //是否已经被角色组适配，界面适配用
        if (!StringUtils.isEmpty(appFeatureModel.getRoleId())) {
            ResponseEntity<Collection<MRoleFeatureRelation>> responseEntity = roleFeatureRelationClient.searchRoleFeature("", "roleId=" + appFeatureModel.getRoleId() + ";featureId=" + appFeatureModel.getId(), "", 1, 1);
            Collection<MRoleFeatureRelation> mRoleFeatureRelations = responseEntity.getBody();
            if (mRoleFeatureRelations != null && mRoleFeatureRelations.size() > 0) {
                appFeatureModel.setIschecked(true);
            } else {
                appFeatureModel.setIschecked(false);
            }
        }
    }

    @RequestMapping(value = "/AppFeatureFindUrl", method = RequestMethod.GET)
    @ApiOperation(value = "根据url获取对象")
    public Envelop AppFeatureFindUrl(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters) {
        Collection<MAppFeature> mAppFeatures = appFeatureClient.getAppFeatureNoPage(filters);
        Envelop envelop = new Envelop();
        List<AppFeatureModel> appFeatureModels = new ArrayList<>();
        for (MAppFeature mAppFeature : mAppFeatures) {
            AppFeatureModel appFeatureModel = convertToModel(mAppFeature, AppFeatureModel.class);
            converModelName(appFeatureModel);
            appFeatureModels.add(appFeatureModel);
        }
        envelop.setDetailModelList(appFeatureModels);
        return envelop;
    }
}
