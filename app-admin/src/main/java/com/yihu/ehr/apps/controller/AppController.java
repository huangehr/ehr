package com.yihu.ehr.apps.controller;

import com.yihu.ehr.agModel.app.AppDetailModel;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.RestAPI;
import com.yihu.ehr.constants.SessionAttributeKeys;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.URLQueryBuilder;
import com.yihu.ehr.util.controller.BaseUIController;
import com.yihu.ehr.util.log.LogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/12.
 */
@RequestMapping("/app")
@Controller(RestAPI.AppManagerController)
@SessionAttributes(SessionAttributeKeys.CurrentUser)
public class AppController extends BaseUIController {
    @Value("${service-gateway.username}")
    private String username;
    @Value("${service-gateway.password}")
    private String password;
    @Value("${service-gateway.url}")
    private String comUrl;

//    @Autowired
//    private RestTemplate template;

    @RequestMapping("template/appInfo")
    public String appInfoTemplate(Model model, String appId, String mode) {

        String result ="";
        Object app=null;
        try {
            //mode定义：new modify view三种模式，新增，修改，查看
            if (mode.equals("new")){
                app = new AppDetailModel();
                ((AppDetailModel)app).setStatus("WaitingForApprove");
            }else{
                String url = "/apps/"+appId;
                Map<String, Object> conditionMap = new HashMap<>();
                conditionMap.put("app_id", appId);
                RestTemplate template = new RestTemplate();
                result = template.getForObject(comUrl + url, String.class,conditionMap);
                Envelop envelop = getEnvelop(result);
                if(envelop.isSuccessFlg()){
                    app = result;
                }
            }
        }
        catch (Exception ex)
        {
            LogService.getLogger(AppController.class).error(ex.getMessage());
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
        URLQueryBuilder builder = new URLQueryBuilder();
        if (!StringUtils.isEmpty(searchNm)) {
            builder.addFilter("id", "?", searchNm, "g1");
            builder.addFilter("name", "?", searchNm, "g1");
        }
        if (!StringUtils.isEmpty(catalog)) {
            builder.addFilter("catalog", "=", catalog, null);
        }
        if (!StringUtils.isEmpty(status)) {
            builder.addFilter("status", "=", status, null);
        }
        builder.setPageNumber(page)
                .setPageSize(rows);
        String param = builder.toString();
        String url = "/apps";
        String resultStr = "";
        try {
            RestTemplate template = new RestTemplate();
            resultStr = template.getForObject(comUrl+url+"?"+param,String.class);
        } catch (Exception ex) {
            LogService.getLogger(AppController.class).error(ex.getMessage());
        }

        return resultStr;
    }

    @RequestMapping("/deleteApp")
    @ResponseBody
    public Object deleteApp(String appId) {
        Envelop result = new Envelop();
        try {
            String url = "/apps/"+appId;
            MultiValueMap<String, String> conditionMap = new LinkedMultiValueMap<>();
            conditionMap.add("app_id", appId);
            RestTemplate template = new RestTemplate();
            template.delete(comUrl + url, conditionMap);
            //todo:没有返回值
            result.setSuccessFlg(true);
        } catch (Exception ex) {
            result.setSuccessFlg(false);
            LogService.getLogger(AppController.class).error(ex.getMessage());
        }
        return result;
    }

    @RequestMapping("createApp")
    @ResponseBody
    public Object createApp(AppDetailModel appDetailModel) {

        Envelop result = new Envelop();
        String resultStr="";
        String url="/apps";
        MultiValueMap<String,String> conditionMap = new LinkedMultiValueMap<String, String>();
        appDetailModel.setCreator("296test");//todo:创建用户不能为空
        conditionMap.add("app", toJson(appDetailModel));
        try {
            RestTemplate template = new RestTemplate();
            resultStr = template.postForObject(comUrl + url, conditionMap, String.class);

            Envelop envelop = getEnvelop(resultStr);
            if (envelop.isSuccessFlg()){
                result.setSuccessFlg(true);
                result.setObj(envelop.getObj());
            }else{
                result.setSuccessFlg(false);
                result.setErrorMsg(ErrorCode.InvalidAppRegister.toString());
            }
        } catch (Exception ex) {
            LogService.getLogger(AppController.class).error(ex.getMessage());
        }
        return result;
    }


    @RequestMapping("updateApp")
    @ResponseBody
    public Object updateApp(AppDetailModel appDetailModel) {

        Envelop result = new Envelop();
        Envelop envelop = new Envelop();
        String resultStr="";
        String url="/apps";
        try {
            RestTemplate template = new RestTemplate();
            //获取app
            String id = appDetailModel.getId();
            MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
            map.add("app_id", id);
            resultStr = template.getForObject(comUrl + url+'/'+id, String.class,map);
            envelop = getEnvelop(resultStr);
            if(envelop.isSuccessFlg()){
                AppDetailModel appUpdate = getEnvelopModel(envelop.getObj(), AppDetailModel.class);
                appUpdate.setName(appDetailModel.getName());
                appUpdate.setCatalog(appDetailModel.getCatalog());
                appUpdate.setStatus(appDetailModel.getStatus());
                appUpdate.setTags(appDetailModel.getTags());
                appUpdate.setUrl(appDetailModel.getUrl());
                appUpdate.setDescription(appDetailModel.getDescription());

                //更新
                MultiValueMap<String,String> conditionMap = new LinkedMultiValueMap<String, String>();
                conditionMap.add("app", toJson(appUpdate));
                template.put(comUrl+url,conditionMap);
                //todo:没有返回值
                result.setObj(appUpdate);
                result.setSuccessFlg(true);
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
        String urlPath = "/apps/status";
        MultiValueMap<String, String> conditionMap = new LinkedMultiValueMap<>();
        conditionMap.add("app_id", appId);
        conditionMap.add("app_status", status);
        try {
            RestTemplate template = new RestTemplate();
            template.put(comUrl + urlPath, conditionMap);
            //todo:没有返回值
            result.setSuccessFlg(true);
        } catch (Exception e) {
            result.setSuccessFlg(false);
        }
        return result;
    }

}
