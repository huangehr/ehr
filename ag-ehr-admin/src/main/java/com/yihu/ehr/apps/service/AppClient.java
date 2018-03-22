package com.yihu.ehr.apps.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.model.app.MUserApp;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/19.
 */
@FeignClient(name=MicroServices.Application)
public interface AppClient {

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Apps.Apps, method = RequestMethod.GET)
    @ApiOperation(value = "获取App列表")
    ResponseEntity<List<MApp>> getApps(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sort", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sort", required = false) String sort,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Apps.AppsNoPage, method = RequestMethod.GET)
    @ApiOperation(value = "获取app列表，不分页")
    Collection<MApp> getAppsNoPage(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件",defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Apps.Apps, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建App")
    MApp createApp(
            @ApiParam(name = "app", value = "对象JSON结构体", allowMultiple = true, defaultValue = "{\"name\": \"\", \"url\": \"\", \"catalog\": \"\", \"description\": \"\", \"creator\":\"\",\"icon\": \"\",\"releaseFlag\": \"\"}")
            @RequestBody String appJson);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Apps.getApp, method = RequestMethod.GET)
    @ApiOperation(value = "获取App")
    MApp getApp(
            @ApiParam(name = "app_id", value = "id", defaultValue = "")
            @RequestParam(value = "app_id") String appId);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Apps.Apps, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "更新App")
    MApp updateApp(
            @ApiParam(name = "app", value = "对象JSON结构体", allowMultiple = true)
            @RequestBody String appJson);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Apps.App, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除app")
    boolean deleteApp(
            @ApiParam(name = "app_id", value = "id", defaultValue = "")
            @PathVariable(value = "app_id") String app_id);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Apps.AppStatus, method = RequestMethod.PUT)
    @ApiOperation(value = "修改状态")
    boolean updateStatus(
            @ApiParam(name = "app_id", value = "id", defaultValue = "")
            @PathVariable(value = "app_id") String appId,
            @ApiParam(name = "app_status", value = "状态", defaultValue = "")
            @RequestParam(value = "app_status") String appStatus);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Apps.AppExistence, method = RequestMethod.GET)
    boolean isAppExists(
            @ApiParam(name = "app_id", value = "id", defaultValue = "")
            @PathVariable(value = "app_id") String appId,
            @ApiParam(name = "secret", value = "", defaultValue = "")
            @RequestParam(value = "secret") String secret);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Apps.AppNameExistence, method = RequestMethod.GET)
    boolean isAppNameExists(
            @ApiParam(value = "app_name")
            @PathVariable(value = "app_name") String appName);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Apps.FilterList, method = RequestMethod.GET)
    @ApiOperation(value = "存在性校验")
    boolean isExitApp(
            @ApiParam(name = "filters", value = "filters", defaultValue = "")
            @RequestParam(value = "filters") String filters);

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Apps.getApps, method = RequestMethod.GET)
    @ApiOperation(value = "根据条件，获取App列表")
    Collection<MApp>  getApps(
            @ApiParam(name = "userId", value = "userId", required = true)
            @RequestParam(value = "userId") String userId ,
            @ApiParam(name = "catalog", value = "catalog", required = true)
            @RequestParam(value = "catalog") String catalog,
            @ApiParam(name = "manageType", value = "APP管理类型，backStage：后台管理，client：客户端。")
            @RequestParam(value = "manageType", required = false) String manageType);

    @RequestMapping(value =  ApiVersion.Version1_0 + ServiceApi.Apps.CheckField, method = RequestMethod.POST)
    @ApiOperation(value = "根据条件判断应用ID或者名称是否存在")
    Envelop isFieldExist(
            @ApiParam(name = "field", value = "字段", required = true)
            @RequestParam(value = "field") String field,
            @ApiParam(name = "value", value = "值", required = true)
            @RequestParam(value = "value") String value);

    @RequestMapping(value =  ApiVersion.Version1_0 + ServiceApi.Apps.AppAuthClient, method = RequestMethod.POST)
    @ApiOperation(value = "开放平台审核结果处理接口，包含App初始化和应用角色分配")
    Envelop authClient(
            @ApiParam(name = "appJson", value = "App")
            @RequestParam(value = "appJson") String appJson,
            @ApiParam(name = "roleId", value = "角色ID")
            @RequestParam(value = "roleId") Integer roleId);

    @RequestMapping(value = ApiVersion.Version1_0 +  ServiceApi.UserApp.CreateUserApp, method = RequestMethod.GET)
    @ApiOperation(value = "创建用户与app关联")
    MUserApp createUserApp(
            @ApiParam(name = "userAppJson", value = "用户APP对象json")
            @RequestParam(value = "userAppJson", required = true) String userAppJson);
}
