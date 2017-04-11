package com.yihu.ehr.apps.controller;

import com.yihu.ehr.agModel.app.AppApiModel;
import com.yihu.ehr.apps.service.AppApiClient;
import com.yihu.ehr.apps.service.AppApiParameterClient;
import com.yihu.ehr.apps.service.AppApiResponseClient;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.app.MAppApi;
import com.yihu.ehr.model.app.MAppApiDetail;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.user.MRoleApiRelation;
import com.yihu.ehr.model.user.MRoleAppRelation;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.users.service.RoleApiRelationClient;
import com.yihu.ehr.users.service.RoleAppRelationClient;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by linz on 2016年7月8日11:30:18.
 */
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@RestController
@Api(value = "AppApi", description = "AppApi", tags = {"AppApi应用"})
public class AppApiController extends BaseController {

    private static final String DELETE = "delete";
    private static final String ADD = "add";
    private static final String UPDATE = "update";
    private static final String NEW_DATA = "0";
    private static final String DATA_STATUS = "__status";
    @Autowired
    AppApiClient appApiClient;
    @Autowired
    RoleApiRelationClient roleApiRelationClient;
    @Autowired
    AppApiParameterClient appApiParameterClient;
    @Autowired
    AppApiResponseClient appApiResponseClient;
    @Autowired
    private ConventionalDictEntryClient conDictEntryClient;

    @Autowired
    private RoleAppRelationClient roleAppRelationClient;

    @RequestMapping(value = ServiceApi.AppApi.AppApis, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApi列表")
    public Envelop getAppApis(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sort", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sort", required = false) String sort,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) {
        ResponseEntity<List<MAppApi>> responseEntity = appApiClient.getAppApis(fields, filters, sort, size, page);
        List<MAppApi> mAppApiList = responseEntity.getBody();
        List<AppApiModel> appApiModels = new ArrayList<>();
        for (MAppApi mAppApi : mAppApiList) {
            AppApiModel appApiModel = new AppApiModel();
            BeanUtils.copyProperties(mAppApi, appApiModel);
            appApiModels.add(appApiModel);
        }
        Integer totalCount = getTotalCount(responseEntity);
        Envelop envelop = getResult(appApiModels, totalCount, page, size);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppApi.AppApis, method = RequestMethod.POST)
    @ApiOperation(value = "创建AppApi")
    public Envelop createAppApi(
            @ApiParam(name = "model", value = "对象JSON结构体", allowMultiple = true, defaultValue = "")
            @RequestParam(value = "model", required = false) String model,
            @ApiParam(name = "apiParms", value = "api请求参数集合")
            @RequestParam(value = "apiParms", required = false) String apiParms,
            @ApiParam(name = "apiResponse", value = "api响应参数集合")
            @RequestParam(value = "apiResponse", required = false) String apiResponse) {
        Envelop envelop = new Envelop();
        MAppApi mAppApi = appApiClient.createAppApi(model);
        if (mAppApi == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("保存失败！");
            return envelop;
        }
        AppApiModel appApiModel = new AppApiModel();
        BeanUtils.copyProperties(mAppApi, appApiModel);
        saveApiParmsResponse(appApiModel.getId() + "", apiParms, apiResponse);
        envelop.setSuccessFlg(true);
        envelop.setObj(appApiModel);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppApi.AppApi, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApi")
    public Envelop getAppApi(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) {
        Envelop envelop = new Envelop();
        MAppApi mAppApi = appApiClient.getAppApi(id);
        AppApiModel appApiModel = new AppApiModel();
        if (mAppApi == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取数据失败！");
            return envelop;
        }
        BeanUtils.copyProperties(mAppApi, appApiModel);
        envelop.setSuccessFlg(true);
        envelop.setObj(appApiModel);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppApi.AppApis, method = RequestMethod.PUT)
    @ApiOperation(value = "更新AppApi")
    public Envelop updateAppApi(
            @ApiParam(name = "model", value = "对象JSON结构体", allowMultiple = true)
            @RequestParam(value = "model", required = false) String AppApi,
            @ApiParam(name = "apiParms", value = "api请求参数集合")
            @RequestParam(value = "apiParms", required = false) String apiParms,
            @ApiParam(name = "apiResponse", value = "api响应参数集合")
            @RequestParam(value = "apiResponse", required = false) String apiResponse) {
        Envelop envelop = new Envelop();
        MAppApi mAppApi = appApiClient.createAppApi(AppApi);
        AppApiModel appApiModel = new AppApiModel();
        if (mAppApi == null) {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("更新数据失败！");
            return envelop;
        }
        BeanUtils.copyProperties(mAppApi, appApiModel);
        saveApiParmsResponse(appApiModel.getId() + "", apiParms, apiResponse);
        envelop.setSuccessFlg(true);
        envelop.setObj(appApiModel);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppApi.AppApi, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除AppApi")
    public Envelop deleteAppApi(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) {
        Envelop envelop = new Envelop();
        Boolean isDelete = appApiClient.deleteAppApi(id);
        envelop.setSuccessFlg(isDelete);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppApi.AppApisNoPage, method = RequestMethod.GET)
    @ApiOperation(value = "获取过滤App列表")
    public Envelop getAppApiNoPage(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "roleId", value = "角色组ID，需要知道是否被关联才需要传入")
            @RequestParam(value = "roleId", required = false) String roleId) {
        Collection<MAppApi> mAppApis = appApiClient.getAppApiNoPage(filters);
        Envelop envelop = new Envelop();
        List<AppApiModel> appApiModels = new ArrayList<>();
        for (MAppApi mAppApi : mAppApis) {
            AppApiModel appApiModel = convertToModel(mAppApi, AppApiModel.class);
            if (StringUtils.isNotBlank(roleId)) {
                appApiModel.setRoleId(roleId);
            }
            converModelName(appApiModel);
            appApiModels.add(appApiModel);
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(appApiModels);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppApi.AppApiSearch, method = RequestMethod.GET)
    @ApiOperation(value = "查询Api详情")
    public Envelop searchApi(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page
    ) {
        ResponseEntity<List<MAppApiDetail>> responseEntity = appApiClient.searchApi(fields, filters, sorts, size, page);
        List<MAppApiDetail> mAppApiList = responseEntity.getBody();

        Integer totalCount = getTotalCount(responseEntity);
        return getResult(mAppApiList, totalCount, page, size);
    }

    @RequestMapping(value = "/role_app_api/no_paging", method = RequestMethod.GET)
    @ApiOperation(value = "获取角色组的AppApi列表")
    public Envelop getRoleAppFeatureNoPage(
            @ApiParam(name = "role_id", value = "角色组id")
            @RequestParam(value = "role_id") String roleId) {
        Collection<MRoleApiRelation> mRoleApiRelations = roleApiRelationClient.searchRoleApiRelationNoPaging("roleId=" + roleId);
        String apiIds = "";
        for (MRoleApiRelation m : mRoleApiRelations) {
            apiIds += m.getApiId() + ",";
        }
        if (!StringUtils.isEmpty(apiIds)) {
            apiIds = apiIds.substring(0, apiIds.length() - 1);
        }
        Collection<MAppApi> mAppApis = appApiClient.getAppApiNoPage("id=" + apiIds);
        Envelop envelop = new Envelop();
        List<AppApiModel> appApiModels = new ArrayList<>();
        for (MAppApi mAppApi : mAppApis) {
            AppApiModel appApiModel = convertToModel(mAppApi, AppApiModel.class);
            appApiModels.add(appApiModel);
        }
        envelop.setDetailModelList(appApiModels);
        return envelop;
    }

    /**
     * 格式化字典数据
     *
     * @param appApiModel
     */
    private void converModelName(AppApiModel appApiModel) {
        //是否已经被角色组适配，界面适配用
        if (!StringUtils.isEmpty(appApiModel.getRoleId())) {
            ResponseEntity<Collection<MRoleApiRelation>> responseEntity = roleApiRelationClient.searchRoleApiRelations("", "roleId=" + appApiModel.getRoleId() + ";apiId=" + appApiModel.getId(), "", 1, 1);
            Collection<MRoleApiRelation> mRoleFeatureRelations = responseEntity.getBody();
            if (mRoleFeatureRelations != null && mRoleFeatureRelations.size() > 0) {
                appApiModel.setIschecked(true);
            }
        }
        //审计等级
        if (!StringUtils.isEmpty(appApiModel.getAuditLevel())) {
            MConventionalDict catalopDict = conDictEntryClient.getAuditLevel(appApiModel.getAuditLevel());
            appApiModel.setAuditLevelName(catalopDict == null ? "" : catalopDict.getValue());
        }
        //开放等级
        if (!StringUtils.isEmpty(appApiModel.getOpenLevel())) {
            MConventionalDict catalopDict = conDictEntryClient.getOpenLevel(appApiModel.getOpenLevel());
            appApiModel.setOpenLevelName(catalopDict == null ? "" : catalopDict.getValue());
        }
    }

    /**
     * 保存apiParms及apiResponse
     *
     * @param apiId
     * @param apiParms
     * @param apiResponse
     */
    private void saveApiParmsResponse(String apiId, String apiParms, String apiResponse) {
        try {
            List<Map<String, Object>> list;
            if (!StringUtils.isEmpty(apiParms)) {
                list = objectMapper.readValue(apiParms, List.class);
                for (Map<String, Object> parmsMap : list) {
                    //删除的是新增的数据直接跳过
                    if (DELETE.equals(parmsMap.get(DATA_STATUS)) && NEW_DATA.equals(parmsMap.get("id") + "")) {
                        continue;
                    } else {
                        parmsMap.put("appApiId", apiId);
                        String json = toJson(parmsMap);
                        if (NEW_DATA.equals(parmsMap.get("id") + "")) {
                            appApiParameterClient.createAppApiParameter(json);
                        } else if (UPDATE.equals(parmsMap.get(DATA_STATUS))) {
                            appApiParameterClient.updateAppApiParameter(json);
                        } else if (DELETE.equals(parmsMap.get(DATA_STATUS))) {
                            appApiParameterClient.deleteAppApiParameter(parmsMap.get("id") + "");
                        }
                    }
                }
            }
            if (!StringUtils.isEmpty(apiResponse)) {
                list = objectMapper.readValue(apiResponse, List.class);
                for (Map<String, Object> parmsMap : list) {
                    //删除的是新增的数据直接跳过
                    if (DELETE.equals(parmsMap.get(DATA_STATUS)) && NEW_DATA.equals(parmsMap.get("id") + "")) {
                        continue;
                    } else {
                        parmsMap.put("appApiId", apiId);
                        String json = toJson(parmsMap);
                        if (ADD.equals(parmsMap.get(DATA_STATUS))) {
                            appApiResponseClient.createAppApiResponse(json);
                        } else if (UPDATE.equals(parmsMap.get(DATA_STATUS))) {
                            appApiResponseClient.updateAppApiResponse(json);
                        } else if (DELETE.equals(parmsMap.get(DATA_STATUS))) {
                            appApiResponseClient.deleteAppApiResponse(parmsMap.get("id") + "");
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    

    @RequestMapping(value = ServiceApi.AppApi.AppApiAuth, method = RequestMethod.GET)
    @ApiOperation(value = "验证应用的api调用权限")
    public Envelop checkAppApi(
            @ApiParam(name = "appId", value = "应用ID")
            @RequestParam(value = "appId") String appId,
            @ApiParam(name = "apiName", value = "api名称")
            @RequestParam(value = "apiName") String apiName) {
        Envelop envelop = new Envelop();
        try {
            //获取应用和api关系
            Collection<MRoleAppRelation> mRoleAppRelations = roleAppRelationClient.searchRoleAppNoPaging("appId=" + appId);
            String roleIds = mRoleAppRelations.stream().map(MRoleAppRelation::getRoleId).collect(Collectors.toList()).stream().map(roleId -> roleId.toString()).collect(Collectors.joining(","));
            if("".equals(roleIds)){
                return failed("应用无该API的访问权限");
            }
            //获取角色和api关系
            Collection<MRoleApiRelation> mRoleApiRelations = roleApiRelationClient.searchRoleApiRelationNoPaging("roleId=" + roleIds);
            String apiIds = mRoleApiRelations.stream().map(MRoleApiRelation::getApiId).collect(Collectors.toList()).stream().map(apiId -> apiId.toString()).collect(Collectors.joining(","));
            //获取角色相关的所有api
            Collection<MAppApi> mAppApis = appApiClient.getAppApiNoPage("id=" + apiIds + ";methodName=" + apiName);
            List<MAppApi> appApiList = mAppApis.stream().collect(Collectors.toList());
            if (appApiList!=null && appApiList.size()>0){
                return success("");
            }else {
                return failed("无该应用API的访问权限");
            }
//            envelop.setDetailModelList(appApiList);
//            return envelop;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
