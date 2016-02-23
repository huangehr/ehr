package com.yihu.ehr.apps.controller;

import com.yihu.ehr.apps.service.App;
import com.yihu.ehr.apps.service.AppService;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.constants.ErrorCode;
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
@RequestMapping(ApiVersionPrefix.Version1_0)
@Api(protocols = "https", value = "Application", description = "EHR应用管理", tags = {"应用管理"})
public class AppController extends BaseRestController {
    @Autowired
    private AppService appService;


    @RequestMapping(value = "/apps", method = RequestMethod.GET)
    @ApiOperation(value = "获取App列表")
    public Collection<MApp> getApps(
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
        page = reducePage(page);

        if (StringUtils.isEmpty(filters)){
            Page<App> appPages = appService.getAppList(sorts, page, size);

            pagedResponse(request, response, appPages.getTotalElements(), page, size);
            return convertToModels(appPages.getContent(), new ArrayList<>(appPages.getNumber()), MApp.class, fields);
        } else {
            List<App> appList = appService.search(fields, filters, sorts, page, size);

            pagedResponse(request, response, appService.getCount(filters), page, size);
            return convertToModels(appList, new ArrayList<>(appList.size()), MApp.class, fields);
        }
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
        app.setId(getObjectId(BizObject.App));
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
        app.setId(getObjectId(BizObject.App));
        if (appService.retrieve(app.getId()) == null) throw new ApiException(ErrorCode.InvalidAppId, "应用不存在");
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


    @RequestMapping(value = "/apps/status" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改状态")
    public boolean updateSatus(
            @ApiParam(name = "app_id", value = "id", defaultValue = "")
            @RequestParam(value = "app_id") String appId,
            @ApiParam(name = "status", value = "状态", defaultValue = "")
            @RequestParam(value = "app_status") String appStatus) throws Exception{
        appService.checkStatus(appId, appStatus);
        return true;
    }

    @RequestMapping(value = "/apps/existence/{app_id}" , method = RequestMethod.GET)
    @ApiOperation(value = "验证")
    public boolean isAppExistence(
            @ApiParam(name = "app_id", value = "id", defaultValue = "")
            @PathVariable(value = "app_id") String appId,
            @ApiParam(name = "secret", value = "", defaultValue = "")
            @RequestParam(value = "secret") String secret) throws Exception{
        return appService.findByIdAndSecret(appId, secret)!=null;
    }

    @RequestMapping(value = "/apps/name" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的app名称是否已经存在")
    boolean isAppNameExists(
            @ApiParam(name = "app_name", value = "id", defaultValue = "")
            @RequestParam(value = "app_name") String appName){
        return appService.isAppNameExists(appName);
    }
}
