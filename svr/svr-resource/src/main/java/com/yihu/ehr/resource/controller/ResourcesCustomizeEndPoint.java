package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.resource.service.ResourcesCustomizeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * Created by lyr on 2016/5/4.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "resourceCustomize", description = "自定义资源服务接口")
public class ResourcesCustomizeEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ResourcesCustomizeService resourcesCustomizeService;

    /**
     * Map<String, Object>
     * @param filters
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.Resources.NoPageCustomizeList, method = RequestMethod.GET)
    @ApiOperation("获取自定义资源列表树")
    public List<Map<String, Object>> getCustomizeList(
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        return resourcesCustomizeService.getCustomizeList(filters);
    }

    @RequestMapping(value = ServiceApi.Resources.NoPageCustomizeData, method = RequestMethod.GET)
    @ApiOperation("获取自定义资数据")
    public List<Map<String, Object>> getCustomizeData(
            @ApiParam(name = "resourcesCode", value = "资源代码")
            @RequestParam(value = "resourcesCode") String resourcesCode,
            @ApiParam(name = "metadata", value = "数据元")
            @RequestParam(value = "metaData", required = false) String metaData,
            @ApiParam(name = "orgCode", value = "机构代码")
            @RequestParam(value = "orgCode", required = false) String orgCode,
            @ApiParam(name = "appId", value = "机构代码")
            @RequestParam(value = "appId") String appId,
            @ApiParam(name = "queryCondition", value = "查询条件")
            @RequestParam(value = "queryCondition", required = false) String queryCondition,
            @ApiParam(name = "page", value = "第几页")
            @RequestParam(value = "page", required = false) Integer page,
            @ApiParam(name = "size", value = "每页几行")
            @RequestParam(value = "size", required = false) Integer size) throws Exception {
        return resourcesCustomizeService.getCustomizeData(resourcesCode, metaData, orgCode, appId, queryCondition, page, size);
    }
}
