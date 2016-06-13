package com.yihu.ehr.resource.controller;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.resource.service.ResourcesTransformService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
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
            @ApiParam(name="resource",value="资源数据",required = true)
            @RequestParam(value = "resource",required = true) String resource,
            @ApiParam(name="version",value="版本",required = true)
            @RequestParam(value = "version",required = true) String version) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, Map.class);
        List<Map<String, Object>> list = mapper.readValue(resource, javaType);
        return resourcesTransformService.displayCodeConvert(list, version);
    }

    @ApiOperation("EHR内部标准转国家标准（单条记录）")
    @RequestMapping(value="/stdTransform",method = RequestMethod.POST)
    public Map<String,Object> stdTransform(
            @ApiParam(name="resource",value="资源数据",required = true)
            @RequestParam(value = "resource",required = true) String resource,
            @ApiParam(name="version",value="版本",required = true)
            @RequestParam(value = "version",required = true) String version) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(resource, Map.class);

        return resourcesTransformService.displayCodeConvert(map,version);
    }
}
