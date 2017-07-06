package com.yihu.ehr.apps.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.agModel.dict.SystemDictEntryModel;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.apps.service.AppApiClient;
import com.yihu.ehr.model.app.MAppApi;
import com.yihu.ehr.model.app.MUserApp;
import com.yihu.ehr.model.dict.MDictionaryEntry;
import com.yihu.ehr.model.dict.MSystemDict;
import com.yihu.ehr.model.org.MOrganization;
import com.yihu.ehr.model.resource.MRsAppResource;
import com.yihu.ehr.model.resource.MRsResources;
import com.yihu.ehr.model.user.MRoleAppRelation;
import com.yihu.ehr.model.user.MRoles;
import com.yihu.ehr.organization.service.OrganizationClient;
import com.yihu.ehr.resource.client.ResourcesClient;
import com.yihu.ehr.resource.client.ResourcesGrantClient;
import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.constants.AgAdminConstants;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.apps.service.AppClient;
import com.yihu.ehr.agModel.app.AppDetailModel;
import com.yihu.ehr.agModel.app.AppModel;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.systemdict.service.SystemDictClient;
import com.yihu.ehr.users.service.RoleAppRelationClient;
import com.yihu.ehr.users.service.RolesClient;
import com.yihu.ehr.util.rest.Envelop;
import com.yihu.ehr.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * Created by AndyCai on 2016/1/19.
 */

@RequestMapping(ApiVersion.Version1_0+"/admin" )
@RestController
@Api(value = "app", description = "应用管理接口", tags = {"应用管理接口"})
public class AppController extends BaseController {
    @Autowired
    private AppClient appClient;
    @Autowired
    private ConventionalDictEntryClient conDictEntryClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrganizationClient organizationClient;
    @Autowired
    private ResourcesGrantClient resourcesGrantClient;
    @Autowired
    private ResourcesClient resourcesClient;
    @Autowired
    private RoleAppRelationClient roleAppRelationClient;
    @Autowired
    private RolesClient rolesClient;
    @Autowired
    private AppApiClient appApiClient;
    @Autowired
    private SystemDictClient systemDictClient;


    @RequestMapping(value = "test", method = RequestMethod.GET)
    @ApiOperation(value = "获取app列表，不分页")
    public Envelop test() throws Exception {
        Envelop envelop = new Envelop();
        envelop.setSuccessFlg(true);
        return envelop;
    }
    
    @RequestMapping(value = "/apps", method = RequestMethod.GET)
    @ApiOperation(value = "获取App列表")
    public Envelop getApps(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sort", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sort", required = false) String sort,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {
        List<AppModel> appModelList = new ArrayList<>();
        ResponseEntity<List<MApp>> responseEntity = appClient.getApps(fields,filters,sort,size,page);
        List<MApp> mAppList = responseEntity.getBody();
        for(MApp app :mAppList){
            appModelList.add(convertToAppModel(app));
        }
        Integer totalCount = getTotalCount(responseEntity);
        Envelop envelop = getResult(appModelList,totalCount,page,size);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.Apps.AppsNoPage, method = RequestMethod.GET)
    @ApiOperation(value = "获取app列表，不分页")
    public Envelop getAppsNoPage(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件",defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        Envelop envelop = new Envelop();
        Collection<MApp> mAppList = appClient.getAppsNoPage(filters);
        List<AppModel> appModelList = new ArrayList<>();
        for(MApp app :mAppList){
            appModelList.add(convertToAppModel(app));
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(appModelList);
        return envelop;
    }

    /**
     * @param appJson
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/apps", method = RequestMethod.POST)
    @ApiOperation(value = "创建App")
    public Envelop createApp(
            @ApiParam(name = "app", value = "对象JSON结构体", allowMultiple = true, defaultValue = "{\"name\": \"\", \"url\": \"\", \"catalog\": \"\", \"description\": \"\", \"creator\":\"\",\"icon\": \"\",\"releaseFlag\": \"\"}")
            @RequestParam(value = "app", required = false) String appJson) throws Exception {
        Envelop envelop = new Envelop();
        //传入的appJson里包含userId
        AppDetailModel appDetailModel = objectMapper.readValue(appJson,AppDetailModel.class);
        MApp app = convertToMApp(appDetailModel);
        MApp mApp = appClient.createApp(objectMapper.writeValueAsString(app));
        if(mApp==null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("app创建失败！");
        }else{
            String role =  appDetailModel.getRole();
            if(StringUtils.isNotBlank(role)){
                //role 为角色组ids,以逗号隔开
                roleAppRelationClient.batchCreateRoleAppRelation(mApp.getId(), role);
            }
            envelop.setSuccessFlg(true);
            envelop.setObj(convertToAppDetailModel(mApp));
        }
        return envelop;
    }

    @RequestMapping(value = "/apps/{app_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取App")
    public Envelop getApp(
            @ApiParam(name = "app_id", value = "id", defaultValue = "")
            @PathVariable(value = "app_id") String appId) throws Exception {
        Envelop envelop = new Envelop();
        MApp mApp = appClient.getApp(appId);
        if (mApp == null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("app获取失败");
        }else{
            //格式化角色
            if("0".equals(mApp.getSourceType()+"")){
                Collection<MRoleAppRelation> mRoleAppRelations =  roleAppRelationClient.searchRoleAppNoPaging("appId=" + appId);
                Map<String,Object> roleMap = new HashMap<>();
                for(MRoleAppRelation mRoleAppRelation:mRoleAppRelations){
                    MRoles mRoles =  rolesClient.getRolesById(mRoleAppRelation.getRoleId());
                    if(mRoles!=null){
                        roleMap.put(mRoles.getId()+"",mRoles.getName());
                    }
                }
               AppDetailModel appDetailModel =  convertToAppDetailModel(mApp);
                if(roleMap.keySet().size()>0){
                    appDetailModel.setRoleJson(toJson(roleMap));
                }
                envelop.setSuccessFlg(true);
                envelop.setObj(appDetailModel);
            }else{
                envelop.setSuccessFlg(true);
                envelop.setObj(convertToAppDetailModel(mApp));
            }
        }
        return envelop;
    }


    @RequestMapping(value = "/apps", method = RequestMethod.PUT)
    @ApiOperation(value = "更新App")
    public Envelop updateApp(
            @ApiParam(name = "app", value = "对象JSON结构体", allowMultiple = true)
            @RequestParam(value = "app", required = false) String appJson) throws Exception {
        Envelop envelop = new Envelop();
        AppDetailModel appDetailModel = objectMapper.readValue(appJson,AppDetailModel.class);
        MApp app = convertToMApp(appDetailModel);
        MApp mApp = appClient.updateApp(objectMapper.writeValueAsString(app));
        if(mApp==null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("app更新失败！");
        }else{
            String role =  appDetailModel.getRole();
            if(StringUtils.isNotBlank(role)){
                //role 为角色组ids,以逗号隔开
                roleAppRelationClient.batchUpdateRoleAppRelation(mApp.getId(),role);
            }
            envelop.setSuccessFlg(true);
            envelop.setObj(convertToAppDetailModel(mApp));
        }
        return envelop;
    }

    @RequestMapping(value = "/apps/{app_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除app")
    public Envelop deleteApp(
            @ApiParam(name = "app_id", value = "id", defaultValue = "")
            @PathVariable(value = "app_id") String appId){
        try {
            boolean result = appClient.deleteApp(appId);
            if(!result)
            {
                return failed("删除失败!");
            }

            return success(null);
        }
        catch (Exception ex)
        {
            return failedSystem();
        }

    }

    @RequestMapping(value = "/apps/status",method = RequestMethod.PUT)
    @ApiOperation(value = "修改状态")
    public boolean updateStatus(
            @ApiParam(name= "app_id",value = "app_id",defaultValue = "")
            @RequestParam(value = "app_id") String appId,
            @ApiParam(name = "app_status",value = "状态",defaultValue = "")
            @RequestParam(value = "app_status") String appStatus)throws Exception{
        return appClient.updateStatus(appId, appStatus);
    }

    @RequestMapping(value = "apps/existence/app_id/{app_id}",method = RequestMethod.GET)
    @ApiOperation(value = "验证app")
    public boolean isAppExistence(
            @ApiParam(name= "app_id",value = "app_id",defaultValue = "")
            @PathVariable(value = "app_id") String appId,
            @ApiParam(name = "secret",value = "密钥",defaultValue = "")
            @RequestParam(value = "secret") String secret)throws Exception{
        return appClient.isAppExists(appId, secret);
    }

    @RequestMapping(value = "apps/existence/app_name/{app_name}",method = RequestMethod.GET)
    @ApiOperation(value = "验证app名字是否存在")
    public boolean isAppNameExists(
            @ApiParam(value = "app_name")
            @PathVariable(value = "app_name") String appName){
        return appClient.isAppNameExists(appName);
    }

    /**
     *  将微服务返回的MApp转化为前端AppModel
     * @param mApp
     * @return AppModel
     */
    private AppModel convertToAppModel(MApp mApp) {
        if(mApp==null)
            return null;

        AppModel appModel = convertToModel(mApp, AppModel.class);
        //获取app类别字典值
        if(!StringUtils.isEmpty(mApp.getCatalog())){
            MConventionalDict catalogDict = conDictEntryClient.getAppCatalog(mApp.getCatalog());
            appModel.setCatalogName(catalogDict == null ? "" : catalogDict.getValue());
        }
        //获取状态字典值
        if(!StringUtils.isEmpty(mApp.getStatus())){
            MConventionalDict statusDict = conDictEntryClient.getAppStatus(mApp.getStatus());
            appModel.setStatusName(statusDict == null ? "" : statusDict.getValue());
        }
        //获取机构名称
        if(!StringUtils.isEmpty(mApp.getOrg())){
            MOrganization organization = organizationClient.getOrg(mApp.getOrg());
            appModel.setOrgName(organization == null ? "" : organization.getFullName());
        }
        //获取已授权资源名称
        //根据appId获取授权资源rsIds
        //TODO 提供根据appId获取RsAppResourceModel集合接口来替代
        ResponseEntity<List<MRsAppResource>> responseEntity = resourcesGrantClient.queryAppResourceGrant("", "appId=" + mApp.getId(), "", 1, 999);
        List<MRsAppResource> rsAppResources = responseEntity.getBody();
        if(rsAppResources.size()==0){
            return appModel;
        }
        String rsIds = "";
        for (MRsAppResource m:rsAppResources){
            rsIds += m.getResourceId()+",";
        }
        rsIds = rsIds.substring(0,rsIds.length()-1);
        //根据rsIds获取资源对象集合-再获取资源名字字符串
        //TODO 提供查询资源不分页方法替代
        ResponseEntity<List<MRsResources>> entity = resourcesClient.queryResources("", "id=" + rsIds, "", 1, 999);
        List<MRsResources> mRsResources = entity.getBody();
        if(mRsResources.size() == 0){
            return appModel;
        }
        String rsNames = "";
        for(MRsResources m:mRsResources){
            rsNames += m.getName()+",";
        }
        rsNames = rsNames.substring(0,rsNames.length()-1);
        appModel.setResourceNames(rsNames);
        return appModel;
    }

    /**
     * 将微服务返回的MApp转化为前端显示的appDetailModel
     * @param mApp
     * @return
     */
    private AppDetailModel convertToAppDetailModel(MApp mApp){
        if(mApp==null)
        {
            return null;
        }

        AppDetailModel app = convertToModel(mApp, AppDetailModel.class);

        app.setCreateTime(DateToString(mApp.getCreateTime(), AgAdminConstants.DateTimeFormat));
        app.setAuditTime(DateToString(mApp.getAuditTime(),AgAdminConstants.DateTimeFormat));

        //获取app类别字典值
        if(!StringUtils.isEmpty(mApp.getCatalog())){
            MConventionalDict catalopDict = conDictEntryClient.getAppCatalog(mApp.getCatalog());
            app.setCatalogName(catalopDict == null ? "" : catalopDict.getValue());
        }
        //获取app状态字典值
        if(!StringUtils.isEmpty(mApp.getStatus())){
            MConventionalDict statusDict = conDictEntryClient.getAppStatus(mApp.getStatus());
            app.setStatusName(statusDict == null ? "" : statusDict.getValue());
        }
        //获取机构名称
        if(!StringUtils.isEmpty(mApp.getOrg())){
            MOrganization organization = organizationClient.getOrg(mApp.getOrg());
            app.setOrgName(organization == null ? "" : organization.getFullName());
        }

     /*   MConventionalDict sourceType = conDictEntryClient.getApplicationSource(mApp.getSourceType()+"");
        app.setSourceTypeName(sourceType == null ? "" : sourceType.getValue());*/
        return app;
    }

    private MApp convertToMApp(AppDetailModel detailModel)
    {
        if(detailModel==null)
        {
            return null;
        }
        MApp mApp = convertToModel(detailModel,MApp.class);
        mApp.setCreateTime(StringToDate(detailModel.getCreateTime(),AgAdminConstants.DateTimeFormat));
        mApp.setAuditTime(StringToDate(detailModel.getAuditTime(),AgAdminConstants.DateTimeFormat));
        return mApp;
    }

    @RequestMapping(value = "/apps/filterList", method = RequestMethod.GET)
    @ApiOperation(value = "存在性校验")
    Envelop isExitAppFeature(
            @ApiParam(name = "filters", value = "filters", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters){
        Envelop envelop = new Envelop();
        try{
            Boolean isExit  = appClient.isExitApp(filters);
            envelop.setSuccessFlg(true);
            envelop.setObj(isExit);
        }catch (Exception e){
            envelop.setSuccessFlg(false);
        }
        return envelop;
    }

    @RequestMapping(value = "/appsExitApi", method = RequestMethod.GET)
    @ApiOperation(value = "获取App列表")
    public Envelop getAppsExitApi(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sort", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sort", required = false) String sort,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {
        List<AppModel> appModelList = new ArrayList<>();
        Collection<MAppApi> mAppApis =  appApiClient.getAppApiNoPage("type=2");
        if(mAppApis!=null&&mAppApis.size()>0){
            StringBuffer buffer = new StringBuffer();
            for(MAppApi mAppApi: mAppApis){
                buffer.append(mAppApi.getAppId()).append(",");
            }
            String queryInfo = buffer.toString();
            queryInfo = queryInfo.substring(0,queryInfo.lastIndexOf(","));
            if(StringUtils.isNotBlank(filters)){
                filters+=";appId<>"+queryInfo;
            }else{
                filters="appId<>"+queryInfo;
            }
        }
        ResponseEntity<List<MApp>> responseEntity = appClient.getApps(fields,filters,sort,size,page);
        List<MApp> mAppList = responseEntity.getBody();
        for(MApp app :mAppList){
            appModelList.add(convertToAppModel(app));
        }
        Integer totalCount = getTotalCount(responseEntity);
        Envelop envelop = getResult(appModelList,totalCount,page,size);
        return envelop;
    }


    @RequestMapping(value = "/getAppTreeByType", method = RequestMethod.GET)
    @ApiOperation(value = "获取App tree")
    public Envelop getAppTreeByType(
            @ApiParam(name = "userId", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "userId", required = false) String userId) {
        Envelop envelop = new Envelop();
        //获取系统字典项（App类型）
        String filters="dictId="+1;
        String fields="";
        String sort="+sort";
        int size=999;
        int page=1;

        ResponseEntity<List<MDictionaryEntry>> responseEntity = systemDictClient.getDictEntries(fields, filters, sort, size, page);
        List<MDictionaryEntry> dictionaryEntries = responseEntity.getBody();
        List<SystemDictEntryModel> systemDictEntryModelList = (List<SystemDictEntryModel>) convertToModels(dictionaryEntries, new ArrayList<SystemDictEntryModel>(dictionaryEntries.size()), SystemDictEntryModel.class, null);
        List<SystemDictEntryModel> DictEntryModelList=new ArrayList<>();
        List<AppModel> appModelList=new ArrayList<>();
        Map<String,String> map=new HashedMap();
        if(null!=systemDictEntryModelList&&systemDictEntryModelList.size()>0){
            for(SystemDictEntryModel dict:systemDictEntryModelList){
                Collection<MApp> mAppList = appClient.getAppsByUserIdAndCatalog(userId,dict.getCode());
                appModelList = (List<AppModel>) convertToModels(mAppList, new ArrayList<AppModel>(mAppList.size()), AppModel.class, null);
                dict.setChildren(appModelList);
                DictEntryModelList.add(dict);
                }
            }

        //应用列表
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(DictEntryModelList);
        return envelop;
    }


}
