package com.yihu.ehr.apps.controller;

import com.yihu.ehr.apps.feign.ConventionalDictClient;
import com.yihu.ehr.apps.service.App;
import com.yihu.ehr.apps.service.AppService;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@RequestMapping(ApiVersionPrefix.Version1_0)
@Api(protocols = "https", value = "Application", description = "EHR应用管理", tags = {"应用管理"})
public class AppController extends BaseRestController {
    @Autowired
    private AppService appService;

    @Autowired
    private ConventionalDictClient conventionalDictClient;

    @RequestMapping(value = "/apps", method = RequestMethod.GET)
    @ApiOperation(value = "获取App列表")
    public Collection<MApp> getApps(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false, defaultValue = "15") int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Page<App> apps = appService.getAppList(sorts, page, size);
        pagedResponse(request, response, apps.getTotalElements(), page, size);

        return convertToModels(apps.getContent(), new ArrayList<>(apps.getNumber()), MApp.class, fields);
    }

    /**
     * @param appJson
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/apps", method = RequestMethod.POST)
    @ApiOperation(value = "创建App")
    public MApp createApp(
            @ApiParam(name = "app", value = "对象JSON结构体", allowMultiple = true, defaultValue = "{\"name\": \"\", \"url\": \"\", \"catalog\": \"\", \"description\": \"\", \"creator\":\"\"}")
            @RequestParam(value = "app", required = false) String appJson) throws Exception {
        App app = toEntity(appJson, App.class);
        if(appService.isAppNameExists(app.getName())) throw new ApiException(ErrorCode.InvalidAppRegister, "应用程序名称已存在");

        app = appService.createApp(app);

        return convertToModel(app, MApp.class);
    }

    @RequestMapping(value = "/apps/{app_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取App")
    public MApp getApp(
            @ApiParam(name = "app_id", value = "id", defaultValue = "")
            @PathVariable(value = "app_id") String appId) throws Exception {
        App app = appService.retrieve(appId);
        return convertToModel(app, MApp.class);
    }

    @RequestMapping(value = "/apps", method = RequestMethod.PUT)
    @ApiOperation(value = "更新App")
    public MApp updateApp(
            @ApiParam(name = "app", value = "对象JSON结构体", allowMultiple = true)
            @RequestParam(value = "app", required = false) String appJson) throws Exception {
        App app = toEntity(appJson, App.class);
        if (appService.retrieve(app.getId()) == null) throw new ApiException(ErrorCode.InvalidAppId, "应用不存在");

        if(!appService.retrieve(app.getId()).getName().equals(app.getName()) && appService.isAppNameExists(app.getName())){
            throw new ApiException(ErrorCode.InvalidAppRegister, "应用程序名称已存在");
        }

        appService.save(app);

        return convertToModel(app, MApp.class);
    }

    @RequestMapping(value = "/apps/{app_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除app")
    public void deleteApp(
            @ApiParam(name = "app_id", value = "id", defaultValue = "")
            @PathVariable(value = "app_id") String appId) throws Exception {
        appService.delete(appId);
    }

    @RequestMapping(value = "/apps/search", method = RequestMethod.GET)
    @ApiOperation(value = "获取App列表")
    public Collection<MApp> searchApps(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
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

        return convertToModels(appList, new ArrayList<MApp>(appList.size()), MApp.class, fields);
    }
}
