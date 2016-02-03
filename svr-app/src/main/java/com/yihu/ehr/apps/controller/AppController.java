package com.yihu.ehr.apps.controller;

import com.yihu.ehr.apps.feign.ConventionalDictClient;
import com.yihu.ehr.apps.service.App;
import com.yihu.ehr.apps.service.AppDetailModel;
import com.yihu.ehr.apps.service.AppService;
import com.yihu.ehr.constants.ApiVersionPrefix;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/12.
 */
@EnableFeignClients
@RequestMapping(ApiVersionPrefix.Version1_0 + "/app")
@RestController
@Api(protocols = "https", value = "app", description = "通用app接口", tags = {"app"})
public class AppController extends BaseRestController {

    @Autowired
    private AppService appService;

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
    @RequestMapping(value = "/search" , method = RequestMethod.GET)
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
            @RequestParam(value = "page") int page,
            @ApiParam(name = "rows", value = "页数", defaultValue = "")
            @RequestParam(value = "rows") int rows) throws Exception{

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("appId", appId);
        conditionMap.put("appName", appName);
        conditionMap.put("catalog", catalog);
        conditionMap.put("status", status);
        conditionMap.put("page", page);
        conditionMap.put("rows", rows);
        List<AppDetailModel> detailModelList = appService.searchAppDetailModels(apiVersion,conditionMap);
        int totalCount = appService.searchAppsInt(conditionMap);
        return getResult(detailModelList,totalCount,page,rows);
    }

    @RequestMapping(value = "" , method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除app")
    public Object deleteApp(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "app_id", value = "应用编号", defaultValue = "")
            @RequestParam(value = "app_id") String appId) throws Exception{
        appService.deleteApp(appId);
        return true;
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
    @RequestMapping(value = "" , method = RequestMethod.POST)
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
        MConventionalDict appCatalog = conventionalDictClient.getAppCatalog(apiVersion,catalog);
        App app = appService.createApp(apiVersion,name,appCatalog,url, tags, description, userId);
        return convertToModel(app,MApp.class);
    }

    @RequestMapping(value = "" , method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询app")
    public Object getApp(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "appId", value = "id", defaultValue = "")
            @RequestParam(value = "appId") String appId) throws Exception{
        App app = appService.getApp(appId);
        MApp appModel = convertToModel(app,MApp.class);
        appModel.setCatalog(conventionalDictClient.getAppCatalog(apiVersion,app.getCatalog()));
        appModel.setStatus(conventionalDictClient.getAppStatus(apiVersion,app.getStatus()));
        return appModel;
    }

//    @RequestMapping(value = "/detail" , method = RequestMethod.GET)
//    @ApiOperation(value = "根据id获取app详细信息")
//    public Object getAppDetail(
//            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
//            @PathVariable( value = "api_version") String apiVersion,
//            @ApiParam(name = "appId", value = "编号", defaultValue = "")
//            @RequestParam(value = "appId") String appId) throws Exception{
//        AppDetailModel appDetailModel = appService.searchAppDetailModel(apiVersion,appId);
//        return appDetailModel;
//
//    }

    @RequestMapping(value = "" , method = RequestMethod.PUT)
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

        App app = appService.getApp(appId);
        app.setName(name);
        app.setCatalog(catalog);
        app.setStatus(status);
        app.setUrl(url);
        app.setDescription(description);
        app.setTags(tags);
        appService.updateApp(app);
        return convertToModel(app,MApp.class);

    }

    @RequestMapping(value = "/{app_id}/{status}" , method = RequestMethod.PUT)
    @ApiOperation(value = "修改状态")
    public Object check(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "app_id", value = "名id", defaultValue = "")
            @PathVariable(value = "app_id") String appId,
            @ApiParam(name = "status", value = "状态", defaultValue = "")
            @PathVariable(value = "status") String status) throws Exception{
        appService.checkStatus(appId, status);
        return true;
    }

    @RequestMapping(value = "/{app_id}/{secret}" , method = RequestMethod.GET)
    @ApiOperation(value = "验证")
    public Object validationApp(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "app_id", value = "应用编号", defaultValue = "")
            @PathVariable(value = "app_id") String appId,
            @ApiParam(name = "secret", value = "", defaultValue = "")
            @PathVariable(value = "secret") String secret) throws Exception{
        return appService.validationApp(appId, secret);
    }


}
