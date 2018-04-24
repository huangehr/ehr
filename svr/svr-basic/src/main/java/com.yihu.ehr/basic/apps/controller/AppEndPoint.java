package com.yihu.ehr.basic.apps.controller;

import com.yihu.ehr.basic.apps.model.App;
import com.yihu.ehr.basic.apps.model.AppsRelation;
import com.yihu.ehr.basic.apps.service.AppService;
import com.yihu.ehr.basic.apps.service.AppsRelationService;
import com.yihu.ehr.basic.apps.service.OauthClientDetailsService;
import com.yihu.ehr.basic.dict.service.SystemDictEntryService;
import com.yihu.ehr.basic.user.entity.RoleAppRelation;
import com.yihu.ehr.basic.user.entity.Roles;
import com.yihu.ehr.basic.user.service.RoleAppRelationService;
import com.yihu.ehr.basic.user.service.RolesService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.dict.SystemDictEntry;
import com.yihu.ehr.entity.oauth2.OauthClientDetails;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.model.dict.SystemDictEntryAppModel;
import com.yihu.ehr.util.id.BizObject;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author linaz
 * @version 1.0
 * @created 2015.8.12 16:53:06
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "Application", description = "EHR应用管理", tags = {"应用管理-EHR应用管理"})
public class AppEndPoint extends EnvelopRestEndPoint {

    @Value("${fast-dfs.public-server}")
    private String fastDfsPublicServers;

    @Autowired
    private AppService appService;
    @Autowired
    private RolesService roleAppRelation;
    @Autowired
    private SystemDictEntryService systemDictEntryService;
    @Autowired
    private AppsRelationService appsRelationService;

    @RequestMapping(value = ServiceApi.Apps.Apps, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建App")
    public MApp createApp(
            @ApiParam(name = "app", value = "对象JSON结构体", allowMultiple = true, defaultValue = "{\"name\": \"\", \"url\": \"\", \"catalog\": \"\", \"description\": \"\", \"creator\":\"\"},\"icon\": \"\",\"releaseFlag\": \"\"")
            @RequestBody String appJson) throws Exception {
        App app = toEntity(appJson, App.class);
        app.setId(getObjectId(BizObject.App));
        app = appService.createApp(app);
        return convertToModel(app, MApp.class);
    }

    @RequestMapping(value = ServiceApi.Apps.Apps, method = RequestMethod.GET)
    @ApiOperation(value = "获取App列表")
    public Collection<MApp> getApps(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime,icon,releaseFlag")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sort", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sort", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
            List<App> appList = appService.search(fields, filters, sorts, page, size);
            pagedResponse(request, response, appService.getCount(filters), page, size);
            return convertToModels(appList, new ArrayList<>(appList.size()), MApp.class, fields);
    }

    @RequestMapping(value = ServiceApi.Apps.AppsNoPage, method = RequestMethod.GET)
    @ApiOperation(value = "获取app列表，不分页")
    public Collection<MApp> getAppsNoPage(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件",defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        List<App> appList = appService.search(filters);
        return convertToModels(appList,new ArrayList<MApp>(appList.size()),MApp.class,"");
    }

    @RequestMapping(value = ServiceApi.Apps.Apps, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "更新App")
    public MApp updateApp(
            @ApiParam(name = "app", value = "对象JSON结构体", allowMultiple = true)
            @RequestBody String appJson) throws Exception {
        App app = toEntity(appJson, App.class);
        if (appService.retrieve(app.getId()) == null) {
            throw new RuntimeException("不存在相应appId：" + app.getId());
        }
        appService.save(app);
        return convertToModel(app, MApp.class);
    }

    @RequestMapping(value = ServiceApi.Apps.getApp, method = RequestMethod.GET)
    @ApiOperation(value = "获取App")
    public MApp getApp(
            @ApiParam(name = "app_id", value = "id")
            @RequestParam(value = "app_id") String appId) throws Exception {
        App app = appService.retrieve(appId);
        if (app != null && StringUtils.isNotEmpty(app.getIcon())) {
            String iconUrl = fastDfsPublicServers + "/" + app.getIcon().replace(":", "/");
            app.setIcon(iconUrl);
        }
        return convertToModel(app, MApp.class);
    }

    @RequestMapping(value = ServiceApi.Apps.App, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除App")
    public boolean deleteApp(
            @ApiParam(name = "app_id", value = "id")
            @PathVariable(value = "app_id") String app_id) throws Exception {
        if (appService.retrieve(app_id) != null) {
            appService.delete(app_id);
        }
        return true;
    }

    @RequestMapping(value = ServiceApi.Apps.App, method = RequestMethod.GET)
    @ApiOperation(value = "获取App信息")
    public MApp app (
            @ApiParam(name = "app_id", value = "id")
            @PathVariable(value = "app_id") String app_id) throws Exception {
        App app = appService.retrieve(app_id);
        return convertToModel(app, MApp.class);
    }

    @RequestMapping(value = ServiceApi.Apps.AppStatus, method = RequestMethod.PUT)
    @ApiOperation(value = "修改状态")
    public boolean updateStatus(
            @ApiParam(name = "app_id", value = "id")
            @PathVariable(value = "app_id") String appId,
            @ApiParam(name = "app_status", value = "状态")
            @RequestParam(value = "app_status") String appStatus) throws Exception {
        appService.checkStatus(appId, appStatus);
        return true;
    }

    @RequestMapping(value = ServiceApi.Apps.AppExistence, method = RequestMethod.GET)
    @ApiOperation(value = "验证应用是否存在")
    public boolean isAppExistence(
            @ApiParam(name = "app_id", value = "id")
            @PathVariable(value = "app_id") String appId,
            @ApiParam(name = "secret", value = "")
            @RequestParam(value = "secret") String secret) throws Exception {
        return appService.findByIdAndSecret(appId, secret);
    }

    @RequestMapping(value = ServiceApi.Apps.AppNameExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断应用名称是否已经存在")
    public boolean isAppNameExists(
            @ApiParam(value = "app_name")
            @PathVariable(value = "app_name") String appName) {
        return appService.isAppNameExists(appName);
    }

    @RequestMapping(value = ServiceApi.Apps.FilterList, method = RequestMethod.GET)
    @ApiOperation(value = "获取过滤App列表")
    public Boolean getAppFilter(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        Long count = appService.getCount(filters);
        return count > 0 ? true : false;
    }

    @RequestMapping(value =  ServiceApi.Apps.getApps, method = RequestMethod.GET)
    @ApiOperation(value = "根据条件，获取app列表")
    public Collection<MApp> getApps(
            @ApiParam(name = "userId", value = "userId", required = true)
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "catalog", value = "catalog", required = true)
            @RequestParam(value = "catalog") String catalog,
            @ApiParam(name = "manageType", value = "APP管理类型，backStage：后台管理，client：客户端。", required = true)
            @RequestParam(value = "manageType", required = false) String manageType) throws Exception {
        List<App> appList = appService.getApps(userId, catalog, manageType);
        return convertToModels(appList,new ArrayList<MApp>(appList.size()),MApp.class,"");
    }

    @RequestMapping(value = ServiceApi.Apps.getAppTypeAndApps, method = RequestMethod.GET)
    @ApiOperation(value = "根据条件，获取APP类型及其所拥有的应用")
    public Envelop getAppTypeAndApps(
            @ApiParam(name = "userId", value = "用户ID", required = true)
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "manageType", value = "APP管理类型，backStage：后台管理，client：客户端。")
            @RequestParam(value = "manageType", required = false) String manageType) throws Exception {
        Envelop envelop = new Envelop();
        //获取系统字典项（App类型）
        String filters = "dictId=" + 1;
        String fields = "";
        String sort = "+sort";
        int page = 1;
        int size = 999;
        List<SystemDictEntry> systemDictEntryList = systemDictEntryService.search(fields, filters, sort, size, page);
        List<SystemDictEntryAppModel> systemDictEntryModelList = (List<SystemDictEntryAppModel>) convertToModels(systemDictEntryList, new ArrayList<SystemDictEntryAppModel>(systemDictEntryList.size()), SystemDictEntryAppModel.class, null);
        List<SystemDictEntryAppModel> DictEntryModelList=new ArrayList<>();
        if (systemDictEntryList.size() > 0) {
            for (SystemDictEntryAppModel dict : systemDictEntryModelList){
                Collection<App> mAppList = appService.getApps(userId, dict.getCode(), manageType);
                List<MApp> appModelList = (List<MApp>) convertToModels(mAppList, new ArrayList<MApp>(mAppList.size()), MApp.class, null);
                dict.setChildren(appModelList);
                DictEntryModelList.add(dict);
            }
        }
        //应用列表
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(DictEntryModelList);
        return envelop;
    }


    // -------------------------- 开放平台 ---------------------------------

    @RequestMapping(value =  ServiceApi.Apps.CheckField, method = RequestMethod.POST)
    @ApiOperation(value = "注册时根据条件判断应用ID或者名称是否存在")
    public Boolean isFieldExist(
            @ApiParam(name = "field", value = "字段", required = true)
            @RequestParam(value = "field") String field,
            @ApiParam(name = "value", value = "值", required = true)
            @RequestParam(value = "value") String value) throws Exception{
        List<App> appList = appService.search(field + "=" + value);
        if (appList != null && appList.size() > 0) {
            return true;
        }
        return false;
    }

    @RequestMapping(value =  ServiceApi.Apps.CheckName, method = RequestMethod.POST)
    @ApiOperation(value = "更新的时候判断名字是否存在")
    public Boolean checkName(
            @ApiParam(name = "appId", value = "应用Id", required = true)
            @RequestParam(value = "appId") String appId,
            @ApiParam(name = "newName", value = "值", required = true)
            @RequestParam(value = "newName") String newName) throws Exception{
        App app = appService.findById(appId);
        if (!app.getName().equals(newName) && appService.findByField("name", newName).size() > 0) {
            return true;
        }
        return false;
    }

    @RequestMapping(value =  ServiceApi.Apps.AppAuthClient, method = RequestMethod.POST)
    @ApiOperation(value = "开放平台审核结果处理接口，包含App初始化和应用角色分配")
    public Envelop authClient(
            @ApiParam(name = "appJson", value = "App", required = true)
            @RequestParam(value = "appJson") String appJson,
            @ApiParam(name = "roleId", value = "基础角色ID", required = true)
            @RequestParam(value = "roleId") Long roleId) throws Exception{
        //app 表
        App app = objectMapper.readValue(appJson, App.class);
        app.setCreateTime(new Date());
        app.setAuditor("system");
        app.setAuditTime(new Date());
        app.setCatalog("ApplicationService");
        app.setStatus("Approved");
        app.setSourceType(0);
        app.setCode("DEFAULT");
        app.setManageType("client");
        app.setReleaseFlag(1);
        //oauth 表
        OauthClientDetails oauthClientDetails = new OauthClientDetails();
        oauthClientDetails.setClientId(app.getId());
        oauthClientDetails.setResourceIds("user");
        oauthClientDetails.setClientSecret(app.getSecret());
        oauthClientDetails.setScope("read");
        oauthClientDetails.setAuthorizedGrantTypes("authorization_code,refresh_token,password,implicit");
        oauthClientDetails.setWebServerRedirectUri(app.getUrl());
        oauthClientDetails.setAuthorities(null);
        oauthClientDetails.setAccessTokenValidity(null);
        oauthClientDetails.setAccessTokenValidity(null);
        oauthClientDetails.setAdditionalInformation(null);
        oauthClientDetails.setAutoApprove("true");
        //验证基础角色
        Roles basicRole = roleAppRelation.retrieve(roleId);
        if (null == basicRole) {
            return failed("基础角色为空");
        }
        //创建扩展角色
        Roles additionRole = new Roles();
        additionRole.setCode(app.getId());
        additionRole.setName("扩展开发者");
        additionRole.setDescription("开放平台扩展开发者");
        additionRole.setAppId(basicRole.getAppId()); //此处设置角色所属的应用ID
        additionRole.setType("0");
        App newApp = appService.authClient(app, oauthClientDetails, basicRole, additionRole);
        return success(newApp);
    }

    @RequestMapping(value =  ServiceApi.Apps.SimpleUpdate, method = RequestMethod.POST)
    @ApiOperation(value = "开放平台应用简单更新")
    public Envelop simpleUpdate(
            @ApiParam(name = "appId", value = "AppId", required = true)
            @RequestParam(value = "appId") String appId,
            @ApiParam(name = "name", value = "名称", required = true)
            @RequestParam(value = "name") String name,
            @ApiParam(name = "url", value = "回调地址", required = true)
            @RequestParam(value = "url") String url) {
        List<App> appList = appService.findByField("id", appId);
        if (appList.size() <= 0) {
            return failed("操作对象不存在", ErrorCode.OBJECT_NOT_FOUND.value());
        }
        App app1 = appList.get(0);
        app1.setName(name);
        app1.setUrl(url);
        App app2 = appService.save(app1);
        return success(app2);
    }


    /**
     * 医生工作平台--显示应用列表
     * @param userId
     * @param parentAppId
     * @return
     * @throws Exception
     * create by zhangdan on 2018/04/19
     */
    @RequestMapping(value =  ServiceApi.Apps.GetAppsRelationByUserIdAndParentAppId, method = RequestMethod.POST)
    @ApiOperation(value = "医生工作平台个人平台应用列表")
    public Envelop getAppsRelationByUserID(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam(value = "userId") String userId,
            @ApiParam(name = "parentAppId", value = "医生工作平台应用id", required = true)
            @RequestParam(value = "parentAppId") String parentAppId)throws Exception {
        if (StringUtils.isEmpty(userId)){
            return failed("请先登录!");
        }
        List<Map<String,Object>> list = appService.getAppByParentIdAndUserId(userId, parentAppId);
        return success(list);
    }

    @RequestMapping(value =  ServiceApi.Apps.GetAppsRelationByUserJson, method = RequestMethod.POST)
    @ApiOperation(value = "支撑平台配置应用之间的关系")
    public Envelop getAppsRelationByUserID(
            @ApiParam(name = "jsonData", value = "新增应用关系json字符串", required = true)
            @RequestBody String jsonData)throws Exception {
        AppsRelation appsRelation = toEntity(jsonData, AppsRelation.class);
        AppsRelation relation = appsRelationService.save(appsRelation);
        return success(relation);
    }



}
