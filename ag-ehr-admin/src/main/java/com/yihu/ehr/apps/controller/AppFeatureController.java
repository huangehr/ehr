package com.yihu.ehr.apps.controller;

import com.yihu.ehr.agModel.app.AppFeatureModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.apps.service.AppFeatureClient;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.app.MApp;
import com.yihu.ehr.model.app.MAppFeature;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linz on 2016年7月8日11:30:18.
 */
@RequestMapping(ApiVersion.Version1_0+"/admin" )
@RestController
@Api(value = "AppFeature", description = "菜单功能维护列表AppFeature", tags = {"平台应用"})
public class AppFeatureController extends BaseController {

    @Autowired
    AppFeatureClient appFeatureClient;

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppFeature.AppFeatures, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppFeature列表")
    public Envelop getAppFeatures(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sort", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sort", required = false) String sort,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page){
        ResponseEntity<List<MAppFeature>> responseEntity =  appFeatureClient.getAppFeatures(fields, filters, sort, size, page);
        List<MAppFeature> mAppFeatureList = responseEntity.getBody();
        List<AppFeatureModel> appFeatureModels = new ArrayList<>();
        for(MAppFeature mAppFeature: mAppFeatureList){
            AppFeatureModel appFeatureModel  = new AppFeatureModel();
            BeanUtils.copyProperties(mAppFeature,appFeatureModel);
            appFeatureModels.add(appFeatureModel);
        }
        Integer totalCount = getTotalCount(responseEntity);
        Envelop envelop = getResult(appFeatureModels,totalCount,page,size);
        return envelop;
    }

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppFeature.AppFeatures, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建AppFeature")
    public Envelop createAppFeature(
            @ApiParam(name = "appFeature", value = "对象JSON结构体", allowMultiple = true, defaultValue = "")
            @RequestParam(value = "appFeature", required = false) String appFeature){
        Envelop envelop = new Envelop();
        MAppFeature mAppFeature =  appFeatureClient.createAppFeature(appFeature);
        if(mAppFeature==null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("保存失败！");
            return envelop;
        }
        AppFeatureModel appFeatureModel = new AppFeatureModel();
        BeanUtils.copyProperties(mAppFeature,appFeatureModel);
        envelop.setSuccessFlg(true);
        envelop.setObj(appFeatureModel);
        return envelop;
    }

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppFeature.AppFeature, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppFeature")
    public Envelop getAppFeature(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id){
        Envelop envelop = new Envelop();
        MAppFeature mAppFeature =  appFeatureClient.getAppFeature(id);
        AppFeatureModel appFeatureModel = new AppFeatureModel();
        if(mAppFeature==null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取数据失败！");
            return envelop;
        }
        BeanUtils.copyProperties(mAppFeature,appFeatureModel);
        envelop.setSuccessFlg(true);
        envelop.setObj(appFeatureModel);
        return envelop;
    }

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppFeature.AppFeatures, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "更新AppFeature")
    public Envelop updateAppFeature(
            @ApiParam(name = "AppFeature", value = "对象JSON结构体", allowMultiple = true)
            @RequestParam(value = "appFeature", required = false) String appFeature){
        Envelop envelop = new Envelop();
        MAppFeature mAppFeature =  appFeatureClient.createAppFeature(appFeature);
        AppFeatureModel appFeatureModel = new AppFeatureModel();
        if(mAppFeature==null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("更新数据失败！");
            return envelop;
        }
        BeanUtils.copyProperties(mAppFeature,appFeatureModel);
        envelop.setSuccessFlg(true);
        envelop.setObj(appFeatureModel);
        return envelop;
    }

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppFeature.AppFeature, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除AppFeature")
    Envelop deleteAppFeature(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id){
        Envelop envelop = new Envelop();
        Boolean isDelete  = appFeatureClient.deleteAppFeature(id);
        envelop.setSuccessFlg(isDelete);
        return envelop;
    }

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppFeature.FilterFeatureList, method = RequestMethod.DELETE)
    @ApiOperation(value = "存在性校验")
    Envelop isExitAppFeature(
            @ApiParam(name = "filters", value = "filters", defaultValue = "")
            @PathVariable(value = "filters") String filters){
        Envelop envelop = new Envelop();
        Boolean isExit  = appFeatureClient.isExitAppFeature(filters);
        envelop.setSuccessFlg(isExit);
        return envelop;
    }

}
