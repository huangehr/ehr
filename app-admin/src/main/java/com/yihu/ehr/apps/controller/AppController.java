package com.yihu.ehr.apps.controller;

import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.RestAPI;
import com.yihu.ehr.constants.SessionAttributeKeys;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.HttpClientUtil;
import com.yihu.ehr.util.log.LogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/12.
 */
@RequestMapping("/app")
@Controller(RestAPI.AppManagerController)
@SessionAttributes(SessionAttributeKeys.CurrentUser)
public class AppController {
    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.url}")
    private String comUrl;

    @RequestMapping("template/appInfo")
    public String appInfoTemplate(Model model, String appId, String mode) {

        String result ="";
        try {
            //mode定义：new modify view三种模式，新增，修改，查看
            String url = "/appcon/app";
            Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put("appId", appId);
            conditionMap.put("mode", mode);
            result = HttpClientUtil.doGet(comUrl + url, conditionMap, username, password);
        }
        catch (Exception ex)
        {
            LogService.getLogger(AppController.class).error(ex.getMessage());
        }

        model.addAttribute("app", result);
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

        String url = "/appcon/apps";
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("appId", searchNm);
        conditionMap.put("appName", searchNm);
        conditionMap.put("catalog", catalog);
        conditionMap.put("status", status);
        conditionMap.put("page", page);
        conditionMap.put("rows", rows);
      //  Result result = null;
        Object _res = null;
        try {

            //设置版本号
           // conditionMap.put("apiVersion",version);
            _res = HttpClientUtil.doGet(comUrl + url, conditionMap, username, password);

        } catch (Exception ex) {
            LogService.getLogger(AppController.class).error(ex.getMessage());
        }

        return _res;
    }

    @RequestMapping("/deleteApp")
    @ResponseBody
    public Object deleteApp(String appId) {
        Envelop result = new Envelop();
        try {
            String url = "/appcon/app";
            Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put("appId", appId);
           // appManager.deleteApp(appId);
            String _res = HttpClientUtil.doDelete(comUrl + url, conditionMap, username, password);
            result.setSuccessFlg(true);
        } catch (Exception ex) {
            result.setSuccessFlg(false);
            LogService.getLogger(AppController.class).error(ex.getMessage());
        }
        return result;
    }

    @RequestMapping("createApp")
    @ResponseBody
    public Object createApp(String name,
                            String catalog,
                            String url,
                            String description,
                            String tags,
                            String userId) {
        Envelop result = new Envelop();

        try {
            String urlPath = "/appcon/app";
            Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put("name", name);
            conditionMap.put("catalog", catalog);
            conditionMap.put("url", url);
            conditionMap.put("description", description);
            conditionMap.put("tags", tags);
            conditionMap.put("userId", userId);

            String _res = HttpClientUtil.doPost(comUrl + urlPath, conditionMap, username, password);

            if (_res.equals("")) {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidUpdate.toString());

            } else {
                result.setSuccessFlg(true);
                result.setObj(_res);
            }
        } catch (Exception ex) {
            LogService.getLogger(AppController.class).error(ex.getMessage());
        }
        return result;
    }

    @RequestMapping("getAppDetail")
    @ResponseBody
    public Object getAppDetail(String appId) {
        Envelop result = new Envelop();

        try {
            String url = "/appcon/app";
            Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put("appId", appId);
            // appManager.deleteApp(appId);
            String _res = HttpClientUtil.doGet(comUrl + url, conditionMap, username, password);

            Map<String, String> data = new HashMap<>();
            data.put("appModel", _res);

            result.setSuccessFlg(true);
            result.setObj(data);
        } catch (Exception ex) {
            result.setSuccessFlg(false);
            LogService.getLogger(AppController.class).error(ex.getMessage());
        }
        return result;
    }

    @RequestMapping("updateApp")
    @ResponseBody
    public Object updateApp(String appId, String name, String catalog, String status, String url, String description, String tags) {

        Envelop result = new Envelop();
        try {
            String urlPath = "/appcon/app";
            Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put("name", name);
            conditionMap.put("catalog", catalog);
            conditionMap.put("url", url);
            conditionMap.put("description", description);
            conditionMap.put("tags", tags);
            conditionMap.put("appId", appId);
            conditionMap.put("status", status);
            // appManager.deleteApp(appId);
            String _res = HttpClientUtil.doPut(comUrl + urlPath, conditionMap, username, password);
            if(_res.equals("success"))
            {
                result.setSuccessFlg(true);
            }
            else {
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidAppRegister.toString());
            }
        }
        catch (Exception ex)
        {
            LogService.getLogger(AppController.class).error(ex.getMessage());
        }

        return result;
    }

    @RequestMapping("check")
    @ResponseBody
    public Object check(String appId, String status) {
        Envelop result = new Envelop();

        try {
            String urlPath = "/appcon/check";
            Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put("appId", appId);
            conditionMap.put("status", status);
            String _res = HttpClientUtil.doPut(comUrl + urlPath, conditionMap, username, password);

            if (_res.equals("success")) {
                result.setSuccessFlg(true);
            } else {
                result.setSuccessFlg(false);
            }

        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result;
    }

}
