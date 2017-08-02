package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.model.resource.MRsResources;
import com.yihu.ehr.resource.model.RsMetadata;
import com.yihu.ehr.resource.model.RsResources;
import com.yihu.ehr.resource.service.ResourcesCustomizeListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by lyr on 2016/5/4.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0)
@Api(value = "resourceCustomizeList", description = "自定义资源服务接口")
public class ResourcesCustomizeListEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private ResourcesCustomizeListService resourcesCustomizeListService;

    /**
     * Map<String, Object>
     * @param filters
     * @return
     * @throws Exception
     */
    @RequestMapping(value = ServiceApi.Resources.NoPageCustomizeList, method = RequestMethod.GET)
    @ApiOperation("获取资源列表树")
    public List<Map<String, Object>> getCustomizeCategories(
            @ApiParam(name = "filters", value = "过滤", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters) throws Exception {
        return resourcesCustomizeListService.getCustomizeList(filters);
    }

}
