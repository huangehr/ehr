package com.yihu.ehr.apps.controller;

import com.yihu.ehr.apps.feign.ConventionalDictClient;
import com.yihu.ehr.apps.service.App;
import com.yihu.ehr.apps.service.AppJpaService;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.util.controller.BaseRestController;
import com.yihu.ehr.query.URLQueryParser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.criteria.CriteriaQuery;
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
@Api(protocols = "https", value = "Application", description = "EHR应用管理及鉴权", tags = {"应用管理"})
public class AppController extends BaseRestController {
    @Autowired
    private AppJpaService appService;

    @Autowired
    private ConventionalDictClient conventionalDictClient;

    /**
     * 1-1 根据查询条件查询应用信息。
     */
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
        List<App> appList = appService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, appService.getCount(filters), page, size);

        return convertToModels(appList, new ArrayList<MApp>(appList.size()), MApp.class, fields.split(","));
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
        MConventionalDict appCatalog = conventionalDictClient.getAppCatalog(catalog);
        App app = appService.createApp(name, appCatalog, url, tags, description, userId);

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

    @RequestMapping(value = "/apps/{app_id}", method = RequestMethod.PUT)
    @ApiOperation(value = "更新App")
    public MApp updateApp(
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
        MConventionalDict appCatalog = conventionalDictClient.getAppCatalog(catalog);
        MConventionalDict appStatus = conventionalDictClient.getAppStatus(status);
        app = appService.retrieve(appId);
        if (app == null) throw new ApiException(ErrorCode.InvalidAppId);

        app.setName(name);
        app.setCatalog(appCatalog.getCode());
        app.setStatus(appStatus.getCode());
        app.setUrl(url);
        app.setDescription(description);
        app.setTags(tags);
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
}
