package com.yihu.ehr.ha.apps.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.ha.SystemDict.service.ConventionalDictEntryClient;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.apps.service.AppClient;
import com.yihu.ehr.agModel.app.AppDetailModel;
import com.yihu.ehr.agModel.app.AppModel;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AndyCai on 2016/1/19.
 */

@RequestMapping(ApiVersion.Version1_0+"/admin" )
@RestController
@Api(value = "app", description = "应用管理接口，用于接入应用管理", tags = {"应用管理接口"})
public class AppController extends BaseController {
    @Autowired
    private AppClient appClient;
    @Autowired
    private ConventionalDictEntryClient conDictEntryClient;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/apps", method = RequestMethod.GET)
    @ApiOperation(value = "获取App列表")
    public Envelop getApps(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，规则参见说明文档", defaultValue = "id,name,secret,url,createTime")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sort", value = "排序，规则参见说明文档", defaultValue = "+name,+createTime")
            @RequestParam(value = "sort", required = false) String sort,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) throws Exception {
        List<AppModel> appModelList = new ArrayList<>();
        ResponseEntity<List<MApp>> responseEntity = appClient.getApps(fields,filters,sort,size,page);
        List<MApp> mAppList = responseEntity.getBody();
        for(MApp app :mAppList){
            appModelList.add(changeToAppModel(app));
        }
        Integer totalCount = getTotalCount(responseEntity);
        Envelop envelop = getResult(appModelList,totalCount,page,size);
        return envelop;
    }

    /**
     * @param appJson
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/apps", method = RequestMethod.POST)
    @ApiOperation(value = "创建App")
    public Envelop createApp(
            @ApiParam(name = "app", value = "对象JSON结构体", allowMultiple = true, defaultValue = "{\"name\": \"\", \"url\": \"\", \"catalog\": \"\", \"description\": \"\", \"creator\":\"\"}")
            @RequestParam(value = "app", required = false) String appJson) throws Exception {
        Envelop envelop = new Envelop();
        //传入的appJson里包含userId
        AppDetailModel appDetailModel = objectMapper.readValue(appJson,AppDetailModel.class);
        MApp app = convertToModel(appDetailModel,MApp.class);
        MApp mApp = appClient.createApp(objectMapper.writeValueAsString(app));
        if(mApp==null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("app创建失败！");
        }else{
            envelop.setSuccessFlg(true);
            envelop.setObj(changeToAppDetailModel(mApp));
        }
        return envelop;
    }

    @RequestMapping(value = "/apps/{app_id}", method = RequestMethod.GET)
    @ApiOperation(value = "获取App")
    public Envelop getApp(
            @ApiParam(name = "app_id", value = "id", defaultValue = "")
            @PathVariable(value = "app_id") String appId) throws Exception {
        Envelop envelop = new Envelop();
        MApp mApp = appClient.getApp(appId);
        if (mApp == null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("app获取失败");
        }else{
            envelop.setSuccessFlg(true);
            envelop.setObj(changeToAppDetailModel(mApp));
        }
        return envelop;
    }


    @RequestMapping(value = "/apps", method = RequestMethod.PUT)
    @ApiOperation(value = "更新App")
    public Envelop updateApp(
            @ApiParam(name = "app", value = "对象JSON结构体", allowMultiple = true)
            @RequestParam(value = "app", required = false) String appJson) throws Exception {
        Envelop envelop = new Envelop();
        AppDetailModel appDetailModel = objectMapper.readValue(appJson,AppDetailModel.class);
        MApp app = convertToModel(appDetailModel,MApp.class);
        MApp mApp = appClient.updateApp(objectMapper.writeValueAsString(app));
        if(mApp==null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("app更新失败！");
        }else{
            envelop.setSuccessFlg(true);
            envelop.setObj(changeToAppDetailModel(mApp));
        }
        return envelop;
    }

    @RequestMapping(value = "/apps/{app_id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除app")
    public Envelop deleteApp(
            @ApiParam(name = "app_id", value = "id", defaultValue = "")
            @PathVariable(value = "app_id") String appId){
        try {
            boolean result = appClient.deleteApp(appId);
            if(!result)
            {
                return failed("删除失败!");
            }

            return success(null);
        }
        catch (Exception ex)
        {
            return failedSystem();
        }

    }

    @RequestMapping(value = "/apps/status",method = RequestMethod.PUT)
    @ApiOperation(value = "修改状态")
    public boolean updateStatus(
            @ApiParam(name= "app_id",value = "app_id",defaultValue = "")
            @RequestParam(value = "app_id") String appId,
            @ApiParam(name = "app_status",value = "状态",defaultValue = "")
            @RequestParam(value = "app_status") String appStatus)throws Exception{
        return appClient.updateStatus(appId, appStatus);
    }

    @RequestMapping(value = "apps/existence/app_id/{app_id}",method = RequestMethod.GET)
    @ApiOperation(value = "验证app")
    public boolean isAppExistence(
            @ApiParam(name= "app_id",value = "app_id",defaultValue = "")
            @PathVariable(value = "app_id") String appId,
            @ApiParam(name = "secret",value = "密钥",defaultValue = "")
            @RequestParam(value = "secret") String secret)throws Exception{
        return appClient.isAppExists(appId, secret);
    }

    @RequestMapping(value = "apps/existence/app_name/{app_name}",method = RequestMethod.GET)
    @ApiOperation(value = "验证app名字是否存在")
    public boolean isAppNameExists(
            @ApiParam(value = "app_name")
            @PathVariable(value = "app_name") String appName){
        return appClient.isAppNameExists(appName);
    }

    /**
     *  将微服务返回的MApp转化为前端AppModel
     * @param mApp
     * @return AppModel
     */
    private AppModel changeToAppModel(MApp mApp) {
        AppModel appModel = convertToModel(mApp, AppModel.class);
        //获取app类别字典值
        MConventionalDict catalogDict = conDictEntryClient.getAppCatalog(mApp.getCatalog());
        appModel.setCatalogName(catalogDict == null ? "" : catalogDict.getValue());
        //获取状态字典值
        MConventionalDict statusDict = conDictEntryClient.getAppStatus(mApp.getStatus());
        appModel.setStatusName(statusDict == null ? "" : statusDict.getValue());
        return appModel;
    }

    /**
     * 将微服务返回的MApp转化为前端显示的appDetailModel
     * @param mApp
     * @return
     */
    private AppDetailModel changeToAppDetailModel(MApp mApp){
        AppDetailModel app = convertToModel(mApp, AppDetailModel.class);
        //TODO 微服务提供的model缺少tags标签属性
        //获取app类别字典值
        MConventionalDict catalopDict = conDictEntryClient.getAppCatalog(mApp.getCatalog());
        app.setCatalogName(catalopDict == null ? "" : catalopDict.getValue());
        //获取app状态字典值
        MConventionalDict statusDict = conDictEntryClient.getAppStatus(mApp.getStatus());
        app.setStatusName(statusDict == null ? "" : statusDict.getValue());
        return app;
    }
}
