package com.yihu.ehr.basic.apps.controller;

import com.yihu.ehr.basic.apps.service.AppApiCategoryService;
import com.yihu.ehr.basic.apps.service.AppApiService;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.entity.api.AppApiCategory;
import com.yihu.ehr.util.rest.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * EndPoint - Api业务类别
 * Created by progr1mmer on 2018/3/14.
 */
@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "AppApiCategory", description = "接口分类管理", tags = {"平台应用-接口分类管理"})
public class AppApiCategoryEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private AppApiCategoryService appApiCategoryService;
    @Autowired
    private AppApiService appApiService;

    @RequestMapping(value = ServiceApi.AppApiCategory.Base, method = RequestMethod.POST)
    @ApiOperation("保存记录")
    public Envelop save(
            @ApiParam(name = "appApiCategory", value = "Json串", required = true)
            @RequestParam(value = "appApiCategory") String appApiCategory) throws Exception {
        AppApiCategory appApiCategory1 = toEntity(appApiCategory, AppApiCategory.class);
        AppApiCategory appApiCategory2 = appApiCategoryService.save(appApiCategory1);
        return success(appApiCategory2);
    }

    @RequestMapping(value = ServiceApi.AppApiCategory.Base, method = RequestMethod.DELETE)
    @ApiOperation("删除记录")
    public Envelop delete(
            @ApiParam(name = "ids", value = "id列表xxxx,xxxx,xxxx,...", required = true)
            @RequestParam(value = "ids") String ids){
        String [] idArr = ids.split(",");
        String parent = "";
        for (String id : idArr) {
            if (appApiService.findByCateId(new Integer(id)).size() > 0) {
                parent += id + ",";
            }
        }
        if (StringUtils.isNotEmpty(parent)) {
            return failed("请先删除id：" +  parent + "的子类API");
        }
        List<String> strIdList = Arrays.asList(idArr);
        List<Integer> intIdList = new ArrayList<>(idArr.length);
        strIdList.forEach(item -> intIdList.add(new Integer(item)));
        appApiCategoryService.delete(intIdList);
        return success(true);
    }

    @RequestMapping(value = ServiceApi.AppApiCategory.Base, method = RequestMethod.PUT)
    @ApiOperation("更新记录")
    public Envelop update(
            @ApiParam(name = "appApiCategory", value = "Json串", required = true)
            @RequestParam(value = "appApiCategory") String appApiCategory) throws Exception {
        AppApiCategory appApiCategory1 = toEntity(appApiCategory, AppApiCategory.class);
        if (appApiCategory1.getId() == null || appApiCategoryService.findByField("id", appApiCategory1.getId()).size() <= 0) {
            return failed("操作对象不存在", ErrorCode.OBJECT_NOT_FOUND.value());
        }
        AppApiCategory appApiCategory2 = appApiCategoryService.save(appApiCategory1);
        return success(appApiCategory2);
    }

    @RequestMapping(value = ServiceApi.AppApiCategory.Base, method = RequestMethod.GET)
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
        List<AppApiCategory> appApiCategoryList = appApiCategoryService.search(fields, filters, sorts, page, size);
        int count = (int)appApiCategoryService.getCount(filters);
        Envelop envelop = getPageResult(appApiCategoryList, count, page, size);
        return envelop;
    }

    @RequestMapping(value = ServiceApi.AppApiCategory.Check, method = RequestMethod.GET)
    @ApiOperation(value = "检查字段是否重复")
    public Boolean check(
            @ApiParam(name = "field", value = "检查字段")
            @RequestParam(value = "field", required = false) String field,
            @ApiParam(name = "value", value = "检查值")
            @RequestParam(value = "value", required = false) String value) throws Exception {
        if (appApiCategoryService.findByField(field, value).size() <= 0) {
            return false;
        }
        return true;
    }



}
