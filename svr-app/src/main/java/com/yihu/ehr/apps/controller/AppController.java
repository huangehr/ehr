package com.yihu.ehr.apps.controller;

import com.yihu.ehr.apps.feign.ConventionalDictClient;
import com.yihu.ehr.apps.service.App;
import com.yihu.ehr.apps.service.AppService;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ResponseHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author linaz
 * @version 1.0
 * @created 2015.8.12 16:53:06
 */
@RestController
@RequestMapping(ApiVersionPrefix.Version1_0)
@Api(protocols = "https", value = "Application", description = "EHR应用管理中心接口", tags = {"app"})
public class AppController extends BaseRestController {
    @Autowired
    private AppService appService;

    @Autowired
    private ConventionalDictClient conventionalDictClient;

    /**
     * 1-1 根据查询条件查询应用信息。
     */
    @RequestMapping(value = "/apps", method = RequestMethod.GET)
    @ApiOperation(value = "获取App列表")
    @ResponseHeader
    public List<MApp> getApps(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filter", value = "过滤器，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "filter") String filter,
            @ApiParam(name = "sort", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sort") String sort,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "")
            @RequestParam(value = "size") int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "")
            @RequestParam(value = "page") int page,
            @Response HttpServletResponse response) throws Exception {

        List<App> appList = appService.searchApps(conditionMap);
        int totalCount = appService.getAppCount(conditionMap);

        echoCollection(response, "", totalCount, page, size);
        return convertToModels(appList, new ArrayList<MApp>(appList.size()), fields);
    }

    /**
     * @param name
     * @param catalog
     * @param url
     * @param description
     * @param tags
     * @param userId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/apps", method = RequestMethod.POST)
    @ApiOperation(value = "创建App")
    public MApp createApp(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "catalog", value = "类别", defaultValue = "")
            @RequestParam(value = "catalog") String catalog,
            @ApiParam(name = "url", value = "url", defaultValue = "")
            @RequestParam(value = "url") String url,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "tags", value = "标记", defaultValue = "")
            @RequestParam(value = "tags") String tags,
            @ApiParam(name = "user_id", value = "用户", defaultValue = "")
            @RequestParam(value = "user_id") String userId) throws Exception {
        MConventionalDict appCatalog = conventionalDictClient.getAppCatalog(apiVersion, catalog);
        App app = appService.createApp(apiVersion, name, appCatalog, url, tags, description, userId);

        return convertToModel(app, MApp.class);
    }

    @RequestMapping(value = "/apps/{app_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取App")
    public MApp getApp(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "app_id", value = "id", defaultValue = "")
            @PathVariable(value = "app_id") String appId) throws Exception {
        App app = appService.getApp(appId);
        return convertToModel(app, MApp.class);
    }

    @RequestMapping(value = "/apps/{app_id}", method = RequestMethod.PUT)
    @ApiOperation(value = "更新App")
    public MApp updateApp(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "app_id", value = "appId", defaultValue = "")
            @PathVariable(value = "app_id") String appId,
            @ApiParam(name = "name", value = "名称", defaultValue = "")
            @RequestParam(value = "name") String name,
            @ApiParam(name = "catalog", value = "类别", defaultValue = "")
            @RequestParam(value = "catalog") String catalog,
            @ApiParam(name = "status", value = "状态", defaultValue = "")
            @RequestParam(value = "status") String status,
            @ApiParam(name = "url", value = "url", defaultValue = "")
            @RequestParam(value = "url") String url,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description") String description,
            @ApiParam(name = "tags", value = "标记", defaultValue = "")
            @RequestParam(value = "tags") String tags) throws Exception {

        App app;
        MConventionalDict appCatalog = conventionalDictClient.getAppCatalog(apiVersion, catalog);
        MConventionalDict appStatus = conventionalDictClient.getAppStatus(apiVersion, status);
        app = appService.getApp(appId);
        if (app == null) throw new ApiException(ErrorCode.InvalidAppId);

        app.setName(name);
        app.setCatalog(appCatalog.getCode());
        app.setStatus(appStatus.getCode());
        app.setUrl(url);
        app.setDescription(description);
        app.setTags(tags);
        appService.updateApp(app);

        return convertToModel(app, MApp.class);
    }

    @RequestMapping(value = "/apps/{app_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除app")
    public boolean deleteApp(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable(value = "api_version") String apiVersion,
            @ApiParam(name = "app_id", value = "id", defaultValue = "")
            @PathVariable(value = "app_id") String appId) throws Exception {

        appService.deleteApp(appId);
        return true;
    }
}
