package com.yihu.ehr.basic.apps.controller;

import com.yihu.ehr.basic.apps.service.AppApiErrorCodeService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.api.AppApiErrorCode;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * EndPoint - Api错误码
 * Created by progr1mmer on 2018/3/14.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "AppApiCategory", description = "接口错误码管理", tags = {"平台应用-接口错误码管理"})
public class AppApiErrorCodeEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private AppApiErrorCodeService appApiErrorCodeService;

    @RequestMapping(value = ServiceApi.AppApiErrorCode.Base, method = RequestMethod.POST)
    @ApiOperation("保存记录")
    public Envelop save(
            @ApiParam(name = "appApiErrorCode", value = "Json串", required = true)
            @RequestParam(value = "appApiErrorCode") String appApiErrorCode) throws Exception {
        AppApiErrorCode appApiErrorCode1 = toEntity(appApiErrorCode, AppApiErrorCode.class);
        AppApiErrorCode appApiErrorCode2 = appApiErrorCodeService.save(appApiErrorCode1);
        return success(appApiErrorCode2);
    }

    @RequestMapping(value = ServiceApi.AppApiErrorCode.Base, method = RequestMethod.DELETE)
    @ApiOperation("删除记录")
    public Envelop delete(
            @ApiParam(name = "ids", value = "id列表xxxx,xxxx,xxxx,...", required = true)
            @RequestParam(value = "ids") String ids){
        String [] idArr = ids.split(",");
        List<String> strIdList = Arrays.asList(idArr);
        List<Integer> intIdList = new ArrayList<>(idArr.length);
        strIdList.forEach(item -> intIdList.add(new Integer(item)));
        appApiErrorCodeService.delete(intIdList);
        return success(true);
    }

    @RequestMapping(value = ServiceApi.AppApiErrorCode.Base, method = RequestMethod.PUT)
    @ApiOperation("更新记录")
    public Envelop update(
            @ApiParam(name = "appApiErrorCode", value = "Json串", required = true)
            @RequestParam(value = "appApiErrorCode") String appApiErrorCode) throws Exception {
        AppApiErrorCode appApiErrorCode1 = toEntity(appApiErrorCode, AppApiErrorCode.class);
        if (appApiErrorCode1.getId() == null || appApiErrorCodeService.findByField("id", appApiErrorCode1.getId()).size() <= 0) {
            return failed("操作对象不存在", ErrorCode.OBJECT_NOT_FOUND.value());
        }
        AppApiErrorCode appApiErrorCode2 = appApiErrorCodeService.save(appApiErrorCode1);
        return success(appApiErrorCode2);
    }

    @RequestMapping(value = ServiceApi.AppApiErrorCode.Base, method = RequestMethod.GET)
    @ApiOperation(value = "获取分页")
    public Envelop list(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "page", value = "分页大小", required = true, defaultValue = "1")
            @RequestParam(value = "page") int page,
            @ApiParam(name = "size", value = "页码", required = true, defaultValue = "15")
            @RequestParam(value = "size") int size) throws Exception {
        List<AppApiErrorCode> appApiCategoryList = appApiErrorCodeService.search(fields, filters, sorts, page, size);
        int count = (int)appApiErrorCodeService.getCount(filters);
        Envelop envelop = getPageResult(appApiCategoryList, count, page, size);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppApiErrorCode.CheckCode, method = RequestMethod.GET)
    @ApiOperation(value = "更新时检查Code是否重复")
    public Boolean check(
            @ApiParam(name = "apiId", value = "apiId")
            @RequestParam(value = "apiId", required = false) Integer apiId,
            @ApiParam(name = "newCode", value = "检查值")
            @RequestParam(value = "newCode", required = false) Integer newCode) throws Exception {
        List<AppApiErrorCode> appApiErrorCodeList = appApiErrorCodeService.findByField("apiId", apiId);
        for (AppApiErrorCode appApiErrorCode : appApiErrorCodeList) {
            if (appApiErrorCode.getCode() == newCode) {
                return true;
            }
        }
        return false;
    }



}
