package com.yihu.ehr.api.doctor.controller;

import com.yihu.ehr.agModel.app.AppApiModel;
import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.api.doctor.service.AppApiClient;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.controller.BaseController;
import com.yihu.ehr.model.app.MAppApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cws on 2017年2月4日
 */
@RequestMapping(ApiVersion.Version1_0+"/doctor" )
@RestController
@Api(value = "AppApi", description = "AppApi", tags = {"AppApi应用"})
public class AppApiController extends BaseController {

    @Autowired
    AppApiClient appApiClient;

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
        ResponseEntity<List<MAppApi>> responseEntity =  appApiClient.getAppApis(fields, filters, sort, size, page);
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
}
