package com.yihu.ehr.apps.controller;

import com.yihu.ehr.apps.model.App;
import com.yihu.ehr.apps.model.AppDetailModel;
import com.yihu.ehr.apps.model.AppManager;
import com.yihu.ehr.constrant.Result;
import com.yihu.ehr.constrant.SessionAttributeKeys;
import com.yihu.ehr.dict.service.AppCatalog;
import com.yihu.ehr.dict.service.AppStatus;
import com.yihu.ehr.dict.service.ConventionalDictEntry;
import com.yihu.ehr.util.controller.BaseController;
import com.yihu.ehr.util.controller.BaseRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/12.
 */
@RequestMapping("/app")
@Controller
@SessionAttributes(SessionAttributeKeys.CurrentUser)
public class AppController extends BaseRestController {

    @Autowired
    private AppManager appManager;

    @Autowired
    private ConventionalDictEntry absDictEManage;

    private App app;

    @RequestMapping("template/appInfo")
    public String appInfoTemplate(Model model, String appId, String mode) {

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

    @RequestMapping("initial")
    public String appInitial(Model model) {

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
    @RequestMapping("/searchApps")
    @ResponseBody
    public Object getAppList(String searchNm, String catalog, String status, int page, int rows) {
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("appId", searchNm);
        conditionMap.put("appName", searchNm);
        conditionMap.put("catalog", catalog);
        conditionMap.put("status", status);
        conditionMap.put("page", page);
        conditionMap.put("rows", rows);

        BaseController baseController = new BaseController();

        List<AppDetailModel> detailModelList = appManager.searchAppDetailModels(conditionMap);
        Integer totalCount = appManager.searchAppsInt(conditionMap);

        Result result = baseController.getResult(detailModelList, totalCount, page, rows);


        return result;
    }

    @RequestMapping("/deleteApp")
    @ResponseBody
    public Object deleteApp(String appId) {
        appManager.deleteApp(appId);
        return "删除app成功！";
    }

    @RequestMapping("createApp")
    @ResponseBody
    public Object createApp(
            String name,
            String catalog,
            String url,
            String description,
            String tags,
            @ModelAttribute(SessionAttributeKeys.CurrentUser) User user) {

        AppCatalog appCatalog = absDictEManage.getAppCatalog(catalog);
        app = appManager.createApp(name, appCatalog, url, tags, description, user);

        Result result = new Result();
        if (app == null) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.InvalidUpdate.toString());
            return result.toJson();
        } else {
            result.setSuccessFlg(true);
            result.setObj(app);
            return result.toJson();
        }
    }

    @RequestMapping("getAppDetail")
    @ResponseBody
    public String getAppDetail(String appId) {
        Result result = new Result();

        try {
            AppDetailModel appDetailModel = appManager.searchAppDetailModel(appId);

            Map<String, AppDetailModel> data = new HashMap<>();
            data.put("appModel", appDetailModel);

            result.setSuccessFlg(true);
            result.setObj(data);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

    @RequestMapping("updateApp")
    @ResponseBody
    public Object updateApp(String appId, String name, String catalog, String status, String url, String description, String tags) {

        AppCatalog appCatalog = absDictEManage.getAppCatalog(catalog);
        AppStatus appStatus = absDictEManage.getAppStatus(status);

        Result result = new Result();

        app = appManager.getApp(appId);
        if (app == null) {
            result.setSuccessFlg(false);
            result.setErrorMsg(ErrorCode.InvalidAppRegister.toString());
            return result.toJson();
        } else {
            app.setName(name);
            app.setCatalog(appCatalog);
            app.setStatus(appStatus);
            app.setUrl(url);
            app.setDescription(description);
            app.setTags(tags);

            try {
                appManager.updateApp(app);
                result.setSuccessFlg(true);
                return result.toJson();
            } catch (Exception e) {

                return result.toJson();
            }
        }

    }

    @RequestMapping("check")
    @ResponseBody
    public String check(String appId, String status) {
        Result result = new Result();

        try {
            AppStatus appStatus = absDictEManage.getAppStatus(status);
            appManager.checkStatus(appId, appStatus);
            result.setSuccessFlg(true);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result.toJson();
    }

}
