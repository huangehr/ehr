package com.yihu.ehr.apps.controller;

import com.yihu.ehr.apps.service.App;
import com.yihu.ehr.apps.service.AppDetailModel;
import com.yihu.ehr.apps.service.AppManager;
import com.yihu.ehr.apps.service.ConventionalDictClient;
import com.yihu.ehr.model.dict.MBaseDict;
import com.yihu.ehr.model.user.MUser;
import com.yihu.ehr.util.ApiErrorEcho;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/12.
 */
@RequestMapping("/app")
@RestController
public class AppController extends BaseRestController {

    @Autowired
    private AppManager appManager;

    @Autowired
    private ConventionalDictClient conventionalDictClient;

    @RequestMapping(value = "template/appInfo" , method = RequestMethod.GET)
    public Object appInfoTemplate(Model model, String appId, String mode) throws Exception{
        App app;
        //mode定义：new modify view三种模式，新增，修改，查看
        if(mode.equals("view")){
            app = appManager.getApp(appId);
        }
        else if(mode.equals("modify")){
            app = appManager.getApp(appId);
        }
        else{
            app = new App();
        }

        model.addAttribute("app", app);
        model.addAttribute("mode",mode);
        model.addAttribute("contentPage","/app/appInfoDialog");
        return "generalView";
    }

    @RequestMapping(value = "initial" , method = RequestMethod.GET)
    public Object appInitial(Model model) {

        model.addAttribute("contentPage","/app/appManage");
        return "pageView";
    }

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
     * @param searchNm
     * @param catalog
     * @param status
     * @return
     */
    @RequestMapping(value = "/apps" , method = RequestMethod.GET)
    public Object getAppList(
            @ApiParam(name = "searchNm", value = "查询条件", defaultValue = "")
            @RequestParam(value = "searchNm") Integer searchNm,
            @ApiParam(name = "catalog", value = "类别", defaultValue = "")
            @RequestParam(value = "catalog") Integer catalog,
            @ApiParam(name = "status", value = "状态", defaultValue = "")
            @RequestParam(value = "status") Integer status,
            @ApiParam(name = "page", value = "当前页", defaultValue = "")
            @RequestParam(value = "page") Integer page,
            @ApiParam(name = "rows", value = "页数", defaultValue = "")
            @RequestParam(value = "rows") Integer rows) throws Exception{

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("appId", searchNm);
        conditionMap.put("appName", searchNm);
        conditionMap.put("catalog", catalog);
        conditionMap.put("status", status);
        conditionMap.put("page", page);
        conditionMap.put("rows", rows);
        List<AppDetailModel> detailModelList = appManager.searchAppDetailModels(conditionMap);
        Integer totalCount = appManager.searchAppsInt(conditionMap);
        return detailModelList;
    }

    @RequestMapping(value = "/app" , method = RequestMethod.DELETE)
    public Object deleteApp(
            @ApiParam(name = "appId", value = "id", defaultValue = "")
            @RequestParam(value = "appId") String appId) throws Exception{
        appManager.deleteApp(appId);
        return "success";
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
    public Object createApp(
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
        app = appManager.createApp(name, appCatalog, url, tags, description, userModel);

        if (app == null) {
            ApiErrorEcho apiErrorEcho = new ApiErrorEcho();
            apiErrorEcho.putMessage("failed");
            return apiErrorEcho;
        } else {
            return app;
        }
    }

    @RequestMapping(value = "appDetail" , method = RequestMethod.GET)
    public Object getAppDetail(
            @ApiParam(name = "appId", value = "id", defaultValue = "")
            @RequestParam(value = "appId") String appId) throws Exception{
        AppDetailModel appDetailModel = appManager.searchAppDetailModel(appId);
        return appDetailModel;

    }

    @RequestMapping(value = "app" , method = RequestMethod.PUT)
    public Object updateApp(
            @ApiParam(name = "appId", value = "名id", defaultValue = "")
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
            ApiErrorEcho apiErrorEcho = new ApiErrorEcho();
            apiErrorEcho.putMessage("failed");
            return apiErrorEcho;
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
    public Object check(
            @ApiParam(name = "appId", value = "名id", defaultValue = "")
            @RequestParam(value = "appId") String appId,
            @ApiParam(name = "status", value = "状态", defaultValue = "")
            @RequestParam(value = "status") String status) throws Exception{
        MBaseDict appStatus = conventionalDictClient.getAppStatus(status);
        appManager.checkStatus(appId, appStatus);
        return "success";
    }

    @RequestMapping(value = "validation" , method = RequestMethod.GET)
    public Object validationApp(
            @ApiParam(name = "id", value = "名id", defaultValue = "")
            @RequestParam(value = "id") String id,
            @ApiParam(name = "secret", value = "状态", defaultValue = "")
            @RequestParam(value = "secret") String secret) throws Exception{
        return appManager.validationApp(id, secret);
    }



}
