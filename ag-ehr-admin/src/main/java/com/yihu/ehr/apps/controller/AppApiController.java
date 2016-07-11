package com.yihu.ehr.apps.controller;

import com.yihu.ehr.agModel.app.AppApiModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.apps.service.AppApiClient;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.app.MAppApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
@Api(value = "AppApi", description = "AppApi", tags = {"AppApi应用"})
public class AppApiController extends BaseController {

    @Autowired
    AppApiClient AppApiClient;

    @RequestMapping(value = ServiceApi.AppApi.AppApis, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApi列表")
    public Envelop getAppApis(
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
        ResponseEntity<List<MAppApi>> responseEntity =  AppApiClient.getAppApis(fields, filters, sort, size, page);
        List<MAppApi> mAppApiList = responseEntity.getBody();
        List<AppApiModel> appApiModels = new ArrayList<>();
        for(MAppApi mAppApi: mAppApiList){
            AppApiModel appApiModel  = new AppApiModel();
            BeanUtils.copyProperties(mAppApi,appApiModel);
            appApiModels.add(appApiModel);
        }
        Integer totalCount = getTotalCount(responseEntity);
        Envelop envelop = getResult(appApiModels,totalCount,page,size);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppApi.AppApis, method = RequestMethod.POST)
    @ApiOperation(value = "创建AppApi")
    public Envelop createAppApi(
            @ApiParam(name = "model", value = "对象JSON结构体", allowMultiple = true, defaultValue = "")
            @RequestParam(value = "model", required = false) String model){
        Envelop envelop = new Envelop();
        MAppApi mAppApi =  AppApiClient.createAppApi(model);
        if(mAppApi==null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("保存失败！");
            return envelop;
        }
        AppApiModel appApiModel = new AppApiModel();
        BeanUtils.copyProperties(mAppApi,appApiModel);
        envelop.setSuccessFlg(true);
        envelop.setObj(appApiModel);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppApi.AppApi, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApi")
    public Envelop getAppApi(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id){
        Envelop envelop = new Envelop();
        MAppApi mAppApi =  AppApiClient.getAppApi(id);
        AppApiModel appApiModel = new AppApiModel();
        if(mAppApi==null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取数据失败！");
            return envelop;
        }
        BeanUtils.copyProperties(mAppApi,appApiModel);
        envelop.setSuccessFlg(true);
        envelop.setObj(appApiModel);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppApi.AppApis, method = RequestMethod.PUT)
    @ApiOperation(value = "更新AppApi")
    public Envelop updateAppApi(
            @ApiParam(name = "model", value = "对象JSON结构体", allowMultiple = true)
            @RequestParam(value = "model", required = false) String AppApi){
        Envelop envelop = new Envelop();
        MAppApi mAppApi =  AppApiClient.createAppApi(AppApi);
        AppApiModel appApiModel = new AppApiModel();
        if(mAppApi==null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("更新数据失败！");
            return envelop;
        }
        BeanUtils.copyProperties(mAppApi,appApiModel);
        envelop.setSuccessFlg(true);
        envelop.setObj(appApiModel);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppApi.AppApi, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除AppApi")
    Envelop deleteAppApi(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id){
        Envelop envelop = new Envelop();
        Boolean isDelete  = AppApiClient.deleteAppApi(id);
        envelop.setSuccessFlg(isDelete);
        return envelop;
    }
}
