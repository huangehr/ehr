package com.yihu.ehr.basic.apps.controller;

import com.yihu.ehr.basic.apps.model.App;
import com.yihu.ehr.basic.apps.service.AppService;
import com.yihu.ehr.basic.apps.service.OauthClientDetailsService;
import com.yihu.ehr.basic.user.entity.RoleAppRelation;
import com.yihu.ehr.basic.user.service.RoleAppRelationService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.oauth2.OauthClientDetails;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.util.id.BizObject;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
    private OauthClientDetailsService oauthClientDetailsService;
    @Autowired
    private RoleAppRelationService roleAppRelationService;

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
        if (appService.retrieve(app.getId()) == null) throw new ApiException(ErrorCode.InvalidAppId, "应用不存在");
        appService.save(app);
        return convertToModel(app, MApp.class);
    }

    @RequestMapping(value = ServiceApi.Apps.App, method = RequestMethod.GET)
    @ApiOperation(value = "获取App")
    public MApp getApp(
            @ApiParam(name = "app_id", value = "id")
            @PathVariable(value = "app_id") String appId) throws Exception {
        App app = appService.retrieve(appId);
        if (StringUtils.isNotEmpty(app.getIcon())) {
            String iconUrl = fastDfsPublicServers + "/" + app.getIcon().replace(":", "/");
            app.setIcon(iconUrl);
        }
        return convertToModel(app, MApp.class);
    }

    @RequestMapping(value = ServiceApi.Apps.App, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除App")
    public boolean deleteApp(
            @ApiParam(name = "app_id", value = "id")
            @PathVariable(value = "app_id") String appId) throws Exception {
        appService.delete(appId);
        //删除Oauth
        oauthClientDetailsService.delete(appId);
        //删除应用角色
        List<RoleAppRelation> relationList = roleAppRelationService.search("appId=" + appId);
        if(relationList != null && relationList.size() > 0) {
            for(RoleAppRelation roleAppRelation : relationList) {
                roleAppRelationService.delete(roleAppRelation.getId());
            }
        }
        return true;
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
            @RequestParam(value = "filters", required = false) String filters
    ) throws Exception {
        Long count = appService.getCount(filters);
        return count>0?true:false;
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

    // -------------------------- 开放平台 ---------------------------------

    @RequestMapping(value =  ServiceApi.Apps.AppFieldExistence, method = RequestMethod.POST)
    @ApiOperation(value = "根据条件判断应用ID或者名称是否存在")
    public Envelop isFieldExist(
            @ApiParam(name = "field", value = "字段", required = true)
            @RequestParam(value = "field") String field,
            @ApiParam(name = "value", value = "值", required = true)
            @RequestParam(value = "value") String value) throws Exception{
        Envelop envelop = new Envelop();
        List<App> appList = appService.search(field + "=" + value);
        if (appList != null && appList.size() > 0) {
            envelop.setSuccessFlg(true);
            return envelop;
        }
        envelop.setSuccessFlg(false);
        return envelop;
    }

    @RequestMapping(value =  ServiceApi.Apps.AppAuthClient, method = RequestMethod.POST)
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @ApiOperation(value = "开放平台审核结果处理接口，包含App初始化和应用角色分配")
    public Envelop authClient(
            @ApiParam(name = "appJson", value = "App", required = true)
            @RequestParam(value = "appJson") String appJson,
            @ApiParam(name = "roleId", value = "角色ID", required = true)
            @RequestParam(value = "roleId") Integer roleId) throws Exception{
        Envelop envelop = new Envelop();
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
        appService.save(app);
        //oauth 表
        OauthClientDetails oauthClientDetails = new OauthClientDetails();
        oauthClientDetails.setClientId(app.getId());
        oauthClientDetails.setResourceIds("user");
        oauthClientDetails.setClientSecret(app.getSecret());
        oauthClientDetails.setScope("read");
        oauthClientDetails.setAuthorizedGrantTypes("authorization_code,refresh_token,password,implicit");
        oauthClientDetails.setWebServerRedirectUri(app.getUrl());
        oauthClientDetails.setAccessTokenValidity(null);
        oauthClientDetails.setAccessTokenValidity(null);
        oauthClientDetails.setAutoApprove("true");
        oauthClientDetailsService.save(oauthClientDetails);
        //应用角色表
        RoleAppRelation roleAppRelation = new RoleAppRelation();
        roleAppRelation.setAppId(app.getId());
        roleAppRelation.setRoleId(roleId);
        String[] fields = {"appId", "roleId"};
        String[] values = {roleAppRelation.getAppId(), roleAppRelation.getRoleId() + ""};
        List<RoleAppRelation> roleAppRelations = roleAppRelationService.findByFields(fields, values);
        if(roleAppRelations == null || roleAppRelations.size() <= 0){
            roleAppRelationService.save(roleAppRelation);
        }
        envelop.setSuccessFlg(true);
        envelop.setObj(app);
        return envelop;
    }

}
