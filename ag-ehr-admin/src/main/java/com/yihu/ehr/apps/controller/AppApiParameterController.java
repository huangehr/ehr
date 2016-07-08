package com.yihu.ehr.apps.controller;

import com.yihu.ehr.agModel.app.AppApiParameterModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.apps.service.AppApiParameterClient;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.app.MAppApiParameter;
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
@Api(value = "AppApiParameter", description = "AppApiParameter", tags = {"AppApiParameter应用"})
public class AppApiParameterController extends BaseController {

    @Autowired
    AppApiParameterClient AppApiParameterClient;

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppApiParameter.AppApiParameters, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApiParameter列表")
    public Envelop getAppApiParameters(
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
        ResponseEntity<List<MAppApiParameter>> responseEntity =  AppApiParameterClient.getAppApiParameters(fields, filters, sort, size, page);
        List<MAppApiParameter> mAppApiParameterList = responseEntity.getBody();
        List<AppApiParameterModel> AppApiParameterModels = new ArrayList<>();
        for(MAppApiParameter mAppApiParameter: mAppApiParameterList){
            AppApiParameterModel appApiParameterModel  = new AppApiParameterModel();
            BeanUtils.copyProperties(mAppApiParameter,appApiParameterModel);
            createDictName(appApiParameterModel);
            AppApiParameterModels.add(appApiParameterModel);
        }
        Integer totalCount = getTotalCount(responseEntity);
        Envelop envelop = getResult(AppApiParameterModels,totalCount,page,size);
        return envelop;
    }

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppApiParameter.AppApiParameters, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建AppApiParameter")
    public Envelop createAppApiParameter(
            @ApiParam(name = "model", value = "对象JSON结构体", allowMultiple = true, defaultValue = "")
            @RequestParam(value = "model", required = false) String model){
        Envelop envelop = new Envelop();
        MAppApiParameter mAppApiParameter =  AppApiParameterClient.createAppApiParameter(model);
        if(mAppApiParameter==null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("保存失败！");
            return envelop;
        }
        AppApiParameterModel appApiParameterModel = new AppApiParameterModel();
        BeanUtils.copyProperties(mAppApiParameter,appApiParameterModel);
        envelop.setSuccessFlg(true);
        envelop.setObj(appApiParameterModel);
        return envelop;
    }

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppApiParameter.AppApiParameter, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApiParameter")
    public Envelop getAppApiParameter(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id){
        Envelop envelop = new Envelop();
        MAppApiParameter mAppApiParameter =  AppApiParameterClient.getAppApiParameter(id);
        AppApiParameterModel appApiParameterModel = new AppApiParameterModel();
        if(mAppApiParameter==null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取数据失败！");
            return envelop;
        }
        BeanUtils.copyProperties(mAppApiParameter,appApiParameterModel);
        envelop.setSuccessFlg(true);
        envelop.setObj(appApiParameterModel);
        return envelop;
    }

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppApiParameter.AppApiParameters, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "更新AppApiParameter")
    public Envelop updateAppApiParameter(
            @ApiParam(name = "model", value = "对象JSON结构体", allowMultiple = true)
            @RequestParam(value = "model", required = false) String AppApiParameter){
        Envelop envelop = new Envelop();
        MAppApiParameter mAppApiParameter =  AppApiParameterClient.createAppApiParameter(AppApiParameter);
        AppApiParameterModel appApiParameterModel = new AppApiParameterModel();
        if(mAppApiParameter==null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("更新数据失败！");
            return envelop;
        }
        BeanUtils.copyProperties(mAppApiParameter,appApiParameterModel);
        createDictName(appApiParameterModel);
        envelop.setSuccessFlg(true);
        envelop.setObj(appApiParameterModel);
        return envelop;
    }

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.AppApiParameter.AppApiParameter, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除AppApiParameter")
    Envelop deleteAppApiParameter(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id){
        Envelop envelop = new Envelop();
        Boolean isDelete  = AppApiParameterClient.deleteAppApiParameter(id);
        envelop.setSuccessFlg(isDelete);
        return envelop;
    }

    /**
     * 格式化字典数据
     * @param appApiParameterModel
     */
    private void createDictName(AppApiParameterModel appApiParameterModel){

    }
}
