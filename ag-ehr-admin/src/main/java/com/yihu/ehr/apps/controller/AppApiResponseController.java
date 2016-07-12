package com.yihu.ehr.apps.controller;

import com.yihu.ehr.agModel.app.AppApiResponseModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.apps.service.AppApiResponseClient;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.app.MAppApiResponse;
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
@Api(value = "AppApiResponse", description = "AppApiResponse", tags = {"AppApiResponse应用"})
public class AppApiResponseController extends BaseController {

    @Autowired
    AppApiResponseClient AppApiResponseClient;

    @RequestMapping(value = ServiceApi.AppApiResponse.AppApiResponses, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApiResponse列表")
    public Envelop getAppApiResponses(
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
        ResponseEntity<List<MAppApiResponse>> responseEntity =  AppApiResponseClient.getAppApiResponses(fields, filters, sort, size, page);
        List<MAppApiResponse> mAppApiResponseList = responseEntity.getBody();
        List<AppApiResponseModel> appApiResponseModels = new ArrayList<>();
        for(MAppApiResponse mAppApiResponse: mAppApiResponseList){
            AppApiResponseModel appApiResponseModel  = new AppApiResponseModel();
            BeanUtils.copyProperties(mAppApiResponse,appApiResponseModel);
            appApiResponseModels.add(appApiResponseModel);
        }
        Integer totalCount = getTotalCount(responseEntity);
        Envelop envelop = getResult(appApiResponseModels,totalCount,page,size);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppApiResponse.AppApiResponses, method = RequestMethod.POST)
    @ApiOperation(value = "创建AppApiResponse")
    public Envelop createAppApiResponse(
            @ApiParam(name = "model", value = "对象JSON结构体", allowMultiple = true, defaultValue = "")
            @RequestParam(value = "model", required = false) String model){
        Envelop envelop = new Envelop();
        MAppApiResponse mAppApiResponse =  AppApiResponseClient.createAppApiResponse(model);
        if(mAppApiResponse==null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("保存失败！");
            return envelop;
        }
        AppApiResponseModel appApiResponseModel = new AppApiResponseModel();
        BeanUtils.copyProperties(mAppApiResponse,appApiResponseModel);
        envelop.setSuccessFlg(true);
        envelop.setObj(appApiResponseModel);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppApiResponse.AppApiResponse, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApiResponse")
    public Envelop getAppApiResponse(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id){
        Envelop envelop = new Envelop();
        MAppApiResponse mAppApiResponse =  AppApiResponseClient.getAppApiResponse(id);
        AppApiResponseModel appApiResponseModel = new AppApiResponseModel();
        if(mAppApiResponse==null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取数据失败！");
            return envelop;
        }
        BeanUtils.copyProperties(mAppApiResponse,appApiResponseModel);
        envelop.setSuccessFlg(true);
        envelop.setObj(appApiResponseModel);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppApiResponse.AppApiResponses, method = RequestMethod.PUT)
    @ApiOperation(value = "更新AppApiResponse")
    public Envelop updateAppApiResponse(
            @ApiParam(name = "model", value = "对象JSON结构体", allowMultiple = true)
            @RequestParam(value = "model", required = false) String AppApiResponse){
        Envelop envelop = new Envelop();
        MAppApiResponse mAppApiResponse =  AppApiResponseClient.createAppApiResponse(AppApiResponse);
        AppApiResponseModel appApiResponseModel = new AppApiResponseModel();
        if(mAppApiResponse==null){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("更新数据失败！");
            return envelop;
        }
        BeanUtils.copyProperties(mAppApiResponse,appApiResponseModel);
        envelop.setSuccessFlg(true);
        envelop.setObj(appApiResponseModel);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppApiResponse.AppApiResponse, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除AppApiResponse")
    Envelop deleteAppApiResponse(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id){
        Envelop envelop = new Envelop();
        Boolean isDelete  = AppApiResponseClient.deleteAppApiResponse(id);
        envelop.setSuccessFlg(isDelete);
        return envelop;
    }

    /**
     * 格式化字典数据
     * @param appApiResponseModel
     */
    private void converModelName(AppApiResponseModel appApiResponseModel){

    }
}
