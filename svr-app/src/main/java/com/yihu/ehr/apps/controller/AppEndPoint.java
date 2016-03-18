package com.yihu.ehr.apps.controller;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.apps.service.App;
import com.yihu.ehr.apps.service.AppService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.RestAPI;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author linaz
 * @version 1.0
 * @created 2015.8.12 16:53:06
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(protocols = "https", value = "Application", description = "EHR应用管理", tags = {"应用管理"})
public class AppEndPoint extends BaseRestController {
    @Autowired
    private AppService appService;

    @RequestMapping(value = RestApi.Apps.Apps, method = RequestMethod.POST)
    @ApiOperation(value = "创建App")
    public MApp createApp(
            @ApiParam(name = "app", value = "对象JSON结构体", allowMultiple = true, defaultValue = "{\"name\": \"\", \"url\": \"\", \"catalog\": \"\", \"description\": \"\", \"creator\":\"\"}")
            @RequestParam(value = "app", required = false) String appJson) throws Exception {
        App app = toEntity(appJson, App.class);
        app.setId(getObjectId(BizObject.App));
        app = appService.createApp(app);
        return convertToModel(app, MApp.class);
    }

    @RequestMapping(value = RestApi.Apps.Apps, method = RequestMethod.GET)
    @ApiOperation(value = "获取App列表")
    public Collection<MApp> getApps(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        page = reducePage(page);

        if (StringUtils.isEmpty(filters)) {
            Page<App> appPages = appService.getAppList(sorts, page, size);

            pagedResponse(request, response, appPages.getTotalElements(), page, size);
            return convertToModels(appPages.getContent(), new ArrayList<>(appPages.getNumber()), MApp.class, fields);
        } else {
            List<App> appList = appService.search(fields, filters, sorts, page, size);

            pagedResponse(request, response, appService.getCount(filters), page, size);
            return convertToModels(appList, new ArrayList<>(appList.size()), MApp.class, fields);
        }
    }

    @RequestMapping(value = RestApi.Apps.Apps, method = RequestMethod.PUT)
    @ApiOperation(value = "更新App")
    public MApp updateApp(
            @ApiParam(name = "app", value = "对象JSON结构体", allowMultiple = true)
            @RequestParam(value = "app", required = false) String appJson) throws Exception {
        App app = toEntity(appJson, App.class);
        if (appService.retrieve(app.getId()) == null) throw new ApiException(ErrorCode.InvalidAppId, "应用不存在");
        appService.save(app);
        return convertToModel(app, MApp.class);
    }

    @RequestMapping(value = RestApi.Apps.App, method = RequestMethod.GET)
    @ApiOperation(value = "获取App")
    public MApp getApp(
            @ApiParam(name = "app_id", value = "id")
            @PathVariable(value = "app_id") String appId) throws Exception {
        App app = appService.retrieve(appId);
        return convertToModel(app, MApp.class);
    }

    @RequestMapping(value = RestApi.Apps.App, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除App")
    public void deleteApp(
            @ApiParam(name = "app_id", value = "id")
            @PathVariable(value = "app_id") String appId) throws Exception {
        appService.delete(appId);
    }

    @RequestMapping(value = RestApi.Apps.AppStatus, method = RequestMethod.PUT)
    @ApiOperation(value = "修改状态")
    public boolean updateStatus(
            @ApiParam(name = "app_id", value = "id")
            @PathVariable(value = "app_id") String appId,
            @ApiParam(name = "status", value = "状态")
            @RequestParam(value = "app_status") String appStatus) throws Exception {
        appService.checkStatus(appId, appStatus);
        return true;
    }

    @RequestMapping(value = RestApi.Apps.AppExistence, method = RequestMethod.GET)
    @ApiOperation(value = "验证应用是否存在")
    public boolean isAppExistence(
            @ApiParam(name = "app_id", value = "id")
            @PathVariable(value = "app_id") String appId,
            @ApiParam(name = "secret", value = "")
            @RequestParam(value = "secret") String secret) throws Exception {
        return appService.findByIdAndSecret(appId, secret) != null;
    }

    @RequestMapping(value = RestApi.Apps.AppNameExistence, method = RequestMethod.GET)
    @ApiOperation(value = "判断应用名称是否已经存在")
    boolean isAppNameExists(
            @ApiParam(value = "app_name")
            @PathVariable(value = "app_name") String appName) {
        return appService.isAppNameExists(appName);
    }
}
