package com.yihu.ehr.apps.controller;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.apps.model.App;
import com.yihu.ehr.apps.service.AppService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
@Api(value = "Application", description = "EHR应用管理", tags = {"应用管理"})
public class AppEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private AppService appService;

    @RequestMapping(value = ServiceApi.Apps.Apps, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建App")
    public MApp createApp(
            @ApiParam(name = "app", value = "对象JSON结构体", allowMultiple = true, defaultValue = "{\"name\": \"\", \"url\": \"\", \"catalog\": \"\", \"description\": \"\", \"creator\":\"\"}")
            @RequestBody String appJson) throws Exception {
        App app = toEntity(appJson, App.class);
        app.setId(getObjectId(BizObject.App));
        app = appService.createApp(app);
        return convertToModel(app, MApp.class);
    }

    @RequestMapping(value = ServiceApi.Apps.Apps, method = RequestMethod.GET)
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
        return convertToModel(app, MApp.class);
    }

    @RequestMapping(value = ServiceApi.Apps.App, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除App")
    public boolean deleteApp(
            @ApiParam(name = "app_id", value = "id")
            @PathVariable(value = "app_id") String appId) throws Exception {
        appService.delete(appId);
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
    boolean isAppNameExists(
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
}
