package com.yihu.ehr.resource.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.resource.dao.ResourcesQueryDao;
import com.yihu.ehr.resource.service.ResourcesQueryService;
import com.yihu.ehr.resource.service.ResourcesTransformService;
import com.yihu.ehr.util.Envelop;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by hzp on 2016/4/13.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0 + "/rs/transform")
@Api(value = "transform", description = "转换格式接口")
public class ResourcesTransformEndPoint {
    @Autowired
    private ResourcesTransformService resourcesTransformService;

    @ApiOperation("EHR内部标准转国家标准")
    @RequestMapping(value="/stdTransformList",method = RequestMethod.POST)
    public List<Map<String,Object>> stdTransformList(
            @ApiParam(name="rsData",value="资源数据",required = true)
            @RequestParam(value = "rsData",required = true) List<Map<String,Object>> rsData,
            @ApiParam(name="version",value="版本",required = true)
            @RequestParam(value = "version",required = true) String version)
    {
        return resourcesTransformService.displayCodeConvert(rsData,version);
    }

    @ApiOperation("EHR内部标准转国家标准（单条记录）")
    @RequestMapping(value="/stdTransform",method = RequestMethod.POST)
    public Map<String,Object> stdTransform(
            @ApiParam(name="rsData",value="资源数据",required = true)
            @RequestParam(value = "rsData",required = true) Map<String,Object> rsData,
            @ApiParam(name="version",value="版本",required = true)
            @RequestParam(value = "version",required = true) String version)
    {
        return resourcesTransformService.displayCodeConvert(rsData,version);
    }
}
