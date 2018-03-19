package com.yihu.ehr.basic.apps.controller;

import com.yihu.ehr.basic.apps.model.AppApi;
import com.yihu.ehr.basic.apps.model.AppApiParameter;
import com.yihu.ehr.basic.apps.model.AppApiResponse;
import com.yihu.ehr.basic.apps.service.AppApiCategoryService;
import com.yihu.ehr.basic.apps.service.AppApiParameterService;
import com.yihu.ehr.basic.apps.service.AppApiResponseService;
import com.yihu.ehr.basic.apps.service.AppApiService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.api.AppApiCategory;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.app.MAppApi;
import com.yihu.ehr.model.app.MAppApiDetail;
import com.yihu.ehr.model.app.MAppApiParameter;
import com.yihu.ehr.model.app.OpenAppApi;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import javafx.beans.binding.ObjectExpression;
import org.springframework.aop.aspectj.annotation.LazySingletonAspectInstanceFactoryDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.*;

/**
 * @author linz
 * @version 1.0
 * @created 2016年7月7日21:04:13
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "AppApi", description = "平台应用接口管理", tags = {"平台应用-接口管理"})
public class AppApiEndPoint extends EnvelopRestEndPoint {
    @Autowired
    private AppApiService appApiService;
    @Autowired
    private AppApiParameterService appApiParameterService;
    @Autowired
    private AppApiResponseService appApiResponseService;
    @Autowired
    private AppApiCategoryService appApiCategoryService;

    @RequestMapping(value = ServiceApi.AppApi.AppApis, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建AppApi")
    public MAppApi createAppApi(
            @ApiParam(name = "appApi", value = "对象JSON结构体", allowMultiple = true)
            @RequestBody String appApiJson) throws Exception {
        AppApi appApi = toEntity(appApiJson, AppApi.class);
        appApi = appApiService.createAppApi(appApi);
        return convertToModel(appApi, MAppApi.class);
    }

    @RequestMapping(value = ServiceApi.AppApi.AppApis, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApi列表")
    public Collection<MAppApi> getAppApis(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<AppApi> appApiList = appApiService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, appApiService.getCount(filters), page, size);
        return convertToModels(appApiList, new ArrayList<>(appApiList.size()), MAppApi.class, fields);
    }

    @RequestMapping(value = ServiceApi.AppApi.AppApis, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "更新appApi")
    public MAppApi updateAppApi(
            @ApiParam(name = "appApi", value = "对象JSON结构体", allowMultiple = true)
            @RequestBody String appJson) throws Exception {
        AppApi appApi = toEntity(appJson, AppApi.class);
        if (appApiService.retrieve(appApi.getId()) == null) {
            throw new ApiException(ErrorCode.NOT_FOUND, "应用API不存在");
        }
        appApiService.save(appApi);
        return convertToModel(appApi, MAppApi.class);
    }

    @RequestMapping(value = ServiceApi.AppApi.AppApi, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApi")
    public MAppApi getAppApi(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") int id) throws Exception {
        AppApi appApi = appApiService.retrieve(id);
        return convertToModel(appApi, MAppApi.class);
    }

    @RequestMapping(value = ServiceApi.AppApi.AppApi, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除AppApi")
    public boolean deleteAppApi(
            @ApiParam(name = "id", value = "id")
            @PathVariable(value = "id") int id) throws Exception {
        appApiService.delete(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.AppApi.AppApisNoPage, method = RequestMethod.GET)
    @ApiOperation(value = "获取过滤App列表")
    public Collection<MAppApi> getAppApiNoPage(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters
    ) throws Exception {
        List<AppApi> appApiList = appApiService.search(filters);
        return convertToModels(appApiList, new ArrayList<>(appApiList.size()), MAppApi.class, "");
    }


    @RequestMapping(value = ServiceApi.AppApi.AppApiSearch, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApi列表")
    Collection<MAppApiDetail> searchApi(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        List<AppApi> appApiList = appApiService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, appApiService.getCount(filters), page, size);
        Collection<MAppApiDetail> mAppApiDetails = convertToModels(appApiList, new ArrayList<>(appApiList.size()), MAppApiDetail.class, "");

        mAppApiDetails.forEach(appApi -> {
            try {
                List apiParams = appApiParameterService.search("appApiId=" + appApi.getId());
                Collection<MAppApiParameter> mAppApiParameters = convertToModels(apiParams, new ArrayList<>(apiParams.size()), MAppApiParameter.class, "");
                appApi.setParameters(mAppApiParameters);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        return mAppApiDetails;
    }

    //------------------------ 开放平台基本请求部分 -------------------------

    @RequestMapping(value = ServiceApi.AppApi.Save, method = RequestMethod.POST)
    @ApiOperation(value = "新增Api")
    public Envelop save(
            @ApiParam(name = "appApi", value = "对象JSON结构体", required = true, allowMultiple = true)
            @RequestParam(value = "appApi") String appApi,
            @ApiParam(name = "apiParam", value = "api请求参数集合")
            @RequestParam(value = "apiParam", required = false) String apiParam,
            @ApiParam(name = "apiResponse", value = "api响应参数集合")
            @RequestParam(value = "apiResponse", required = false) String apiResponse,
            @ApiParam(name = "apiErrorCode", value = "api错误码集合")
            @RequestParam(value = "apiErrorCode", required = false) String apiErrorCode) throws Exception {
        AppApi appApi1 = appApiService.completeSave(appApi, apiParam, apiResponse, apiErrorCode);
        List<AppApiParameter> appApiParameters = appApiParameterService.search("appApiId=" + appApi1.getId());
        List<AppApiResponse> appApiResponses = appApiResponseService.search("appApiId=" + appApi1.getId());
        List resultLis = new ArrayList();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("param", appApiParameters);
        dataMap.put("response", appApiResponses);
        resultLis.add(dataMap);
        return success(appApi1, resultLis);
    }

    @RequestMapping(value = ServiceApi.AppApi.Delete, method = RequestMethod.POST)
    @ApiOperation(value = "删除Api")
    public Boolean delete(
            @ApiParam(name = "id", value = "ID", required = true)
            @RequestParam(value = "id") Integer id) throws Exception {
        appApiService.completeDelete(id);
        return true;
    }

    @RequestMapping(value = ServiceApi.AppApi.Update, method = RequestMethod.POST)
    @ApiOperation(value = "更新Api")
    public Envelop update(
            @ApiParam(name = "appApi", value = "对象JSON结构体", required = true, allowMultiple = true)
            @RequestParam(value = "appApi") String appApi,
            @ApiParam(name = "apiParam", value = "api请求参数集合")
            @RequestParam(value = "apiParam", required = false) String apiParam,
            @ApiParam(name = "apiResponse", value = "api响应参数集合")
            @RequestParam(value = "apiResponse", required = false) String apiResponse,
            @ApiParam(name = "apiErrorCode", value = "api错误码集合")
            @RequestParam(value = "apiErrorCode", required = false) String apiErrorCode) throws Exception {
        AppApi appApi1 = toEntity(appApi, AppApi.class);
        if (null == appApiService.findById(appApi1.getId()) ) {
            return failed("操作的API不存在", ErrorCode.OBJECT_NOT_FOUND.value());
        }
        AppApi newAppApi = appApiService.completeSave(appApi, apiParam, apiResponse, apiErrorCode);
        List<AppApiParameter> appApiParameters = appApiParameterService.search("appApiId=" + newAppApi.getId());
        List<AppApiResponse> appApiResponses = appApiResponseService.search("appApiId=" + newAppApi.getId());
        List resultLis = new ArrayList();
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("param", appApiParameters);
        dataMap.put("response", appApiResponses);
        resultLis.add(dataMap);
        return success(appApi1, resultLis);
    }

    @RequestMapping(value = ServiceApi.AppApi.Page, method = RequestMethod.GET)
    @ApiOperation(value = "Api分页")
    public Envelop page(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，规则参见说明文档")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size) throws Exception {
        List<AppApi> appApiList = appApiService.search(fields, filters, sorts, page, size);
        int count = (int)appApiService.getCount(filters);
        Envelop envelop = getPageResult(appApiList, count, page, size);
        List<AppApi> appApiList1 = envelop.getDetailModelList();
        List<AppApi> appApiList2 = new ArrayList<>(appApiList1.size());
        for (AppApi appApi : appApiList1) {
            if (appApi.getCategory() != null) {
                AppApiCategory appApiCategory = appApiCategoryService.findOne(appApi.getCategory());
                if (appApiCategory != null) {
                    appApi.setCategoryName(appApiCategory.getName());
                }
            }
            appApiList2.add(appApi);
        }
        envelop.setDetailModelList(appApiList2);
        return envelop;
    }

    // -------------------------------- 接入授权部分 --------------------------------

    @RequestMapping(value = ServiceApi.AppApi.AuthList, method = RequestMethod.GET)
    @ApiOperation(value = "获取AppApi列表")
    public List<AppApi> authApiList(
            @ApiParam(name = "clientId", value = "应用ID", required = true)
            @RequestParam(value = "clientId") String clientId) throws Exception{
        List<AppApi> appApiList = appApiService.authApiList(clientId);
        return appApiList;
    }



}
