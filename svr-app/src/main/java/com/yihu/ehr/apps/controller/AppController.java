package com.yihu.ehr.apps.controller;

import com.yihu.ehr.apps.service.App;
import com.yihu.ehr.apps.service.AppDetailModel;
import com.yihu.ehr.apps.service.AppManager;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.feignClient.dict.ConventionalDictClient;
import com.yihu.ehr.model.dict.MBaseDict;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/12.
 */
@RequestMapping(ApiVersionPrefix.CommonVersion + "/app")
@RestController
@Api(protocols = "https", value = "address", description = "通用app接口", tags = {"app"})
public class AppController extends BaseRestController {

    @Autowired
    private AppManager appManager;

    @Autowired
    private ConventionalDictClient conventionalDictClient;



    /**
     * 1-1 根据查询条件查询应用信息。
     * <p>
     * {
     * "app_id"  : "AnG4G4zIz1",
     * "app_name"  : "赛诺菲慢病系统",
     * "catalog"  : "1",
     * "status"  : "0",
     * "page"  : "0",
     * "rows"  : "0"
     * }
     *
     * @param catalog
     * @param status
     * @return
     */
    @RequestMapping(value = "/apps" , method = RequestMethod.GET)
    @ApiOperation(value = "查询获取app列表")
    public Object getAppList(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "appId", value = "appId", defaultValue = "")
            @RequestParam(value = "appId") String appId,
            @ApiParam(name = "appName", value = "appName", defaultValue = "")
            @RequestParam(value = "appName") String appName,
            @ApiParam(name = "catalog", value = "类别", defaultValue = "")
            @RequestParam(value = "catalog") String catalog,
            @ApiParam(name = "status", value = "状态", defaultValue = "")
            @RequestParam(value = "status") String status,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") String page,
            @ApiParam(name = "rows", value = "页数", defaultValue = "")
            @RequestParam(value = "rows") String rows) throws Exception{

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("appId", appId);
        conditionMap.put("appName", appName);
        conditionMap.put("catalog", catalog);
        conditionMap.put("status", status);
        conditionMap.put("page", page);
        conditionMap.put("rows", rows);
        Result result = new Result();
        List<AppDetailModel> detailModelList = appManager.searchAppDetailModels(conditionMap);
        Integer totalCount = appManager.searchAppsInt(conditionMap);
        result.setObj(detailModelList);
        result.setTotalCount(totalCount);
        return result;
    }

    @RequestMapping(value = "/app" , method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除app")
    public Object deleteApp(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "appId", value = "id", defaultValue = "")
            @RequestParam(value = "appId") String appId) throws Exception{
        appManager.deleteApp(appId);
        return "success";
    }


    @RequestMapping(value = "/app" , method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询app")
    public Object getApp(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "appId", value = "id", defaultValue = "")
            @RequestParam(value = "appId") String appId) throws Exception{
        App app = appManager.getApp(appId);
        return app;
    }

    /**
     *
     * @param name
     * @param catalog
     * @param url
     * @param description
     * @param tags
     * @param userId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "app" , method = RequestMethod.POST)
    @ApiOperation(value = "创建app")
    public Object createApp(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
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
            @ApiParam(name = "userId", value = "用户", defaultValue = "")
            @RequestParam(value = "userId") String userId) throws Exception{

        App app;
        MBaseDict appCatalog = conventionalDictClient.getAppCatalog(catalog);
        MUser userModel = appManager.getUser(userId);
        app = appManager.createApp(name,appCatalog,url, tags, description, userModel);
        return app;
    }

    @RequestMapping(value = "appDetail" , method = RequestMethod.GET)
    @ApiOperation(value = "根据id获取app详细信息")
    public Object getAppDetail(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "appId", value = "id", defaultValue = "")
            @RequestParam(value = "appId") String appId) throws Exception{
        AppDetailModel appDetailModel = appManager.searchAppDetailModel(appId);
        return appDetailModel;

    }

    @RequestMapping(value = "app" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改app")
    public Object updateApp(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "appId", value = "appId", defaultValue = "")
            @RequestParam(value = "appId") String appId,
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
            @RequestParam(value = "tags") String tags) throws Exception{

        App app;
        MBaseDict appCatalog = conventionalDictClient.getAppCatalog(catalog);
        MBaseDict appStatus = conventionalDictClient.getAppStatus(status);
        app = appManager.getApp(appId);
        if (app == null) {
            return "faild";
        } else {
            app.setName(name);
            app.setCatalog(appCatalog.getCode());
            app.setStatus(appStatus.getCode());
            app.setUrl(url);
            app.setDescription(description);
            app.setTags(tags);

            appManager.updateApp(app);
            return "success";
        }

    }

    @RequestMapping(value = "check" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改状态")
    public Object check(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "appId", value = "名id", defaultValue = "")
            @RequestParam(value = "appId") String appId,
            @ApiParam(name = "status", value = "状态", defaultValue = "")
            @RequestParam(value = "status") String status) throws Exception{
        MBaseDict appStatus = conventionalDictClient.getAppStatus(status);
        appManager.checkStatus(appId, appStatus);
        return "success";
    }

    @RequestMapping(value = "validation" , method = RequestMethod.GET)
    @ApiOperation(value = "")
    public Object validationApp(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "id", value = "名id", defaultValue = "")
            @RequestParam(value = "id") String id,
            @ApiParam(name = "secret", value = "", defaultValue = "")
            @RequestParam(value = "secret") String secret) throws Exception{
        return appManager.validationApp(id, secret);
    }



}
