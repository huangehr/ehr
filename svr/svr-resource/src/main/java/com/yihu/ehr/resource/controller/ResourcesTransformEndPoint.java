package com.yihu.ehr.resource.controller;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.resource.MStdTransformDto;
import com.yihu.ehr.resource.service.ResourcesTransformService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Created by hzp on 2016/4/13.
 */

@RestController
@RequestMapping(value = ApiVersion.Version1_0 )
@Api(value = "Transform", description = "转换格式接口")
public class ResourcesTransformEndPoint {

    @Autowired
    private ResourcesTransformService resourcesTransformService;
    @Autowired
    private ObjectMapper objectMapper;


    @ApiOperation("EHR内部标准转国家标准")
    @RequestMapping(value="/rs/transform/stdTransformList",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<Map<String,Object>> stdTransformList(
            @ApiParam(name="stdTransformDtoJson",value="资源数据模型",required = true)
            @RequestBody String stdTransformDtoJson) throws Exception {
        MStdTransformDto stdTransformDto = objectMapper.readValue(stdTransformDtoJson,MStdTransformDto.class);
        String resource =  stdTransformDto.getSource();
        String version = stdTransformDto.getVersion();
        String dataset = stdTransformDto.getDataset();

        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, Map.class);
        List<Map<String, Object>> list = objectMapper.readValue(resource, javaType);
        return resourcesTransformService.displayCodeConvert(list, version,dataset);
    }

    @ApiOperation("EHR内部标准转国家标准（单条记录）")
    @RequestMapping(value="/rs/transform/stdTransform",method = RequestMethod.POST,consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String,Object> stdTransform(
            @ApiParam(name="stdTransformDtoJson",value="资源数据模型",required = true)
            @RequestBody String stdTransformDtoJson) throws Exception {
        MStdTransformDto stdTransformDto = objectMapper.readValue(stdTransformDtoJson,MStdTransformDto.class);
        String resource =  stdTransformDto.getSource();
        String version = stdTransformDto.getVersion();
        String dataset = stdTransformDto.getDataset();

        Map<String, Object> map = objectMapper.readValue(resource, Map.class);

        return resourcesTransformService.displayCodeConvert(map,version,dataset);
    }



    @ApiOperation("EHR主表数据分解")
    @RequestMapping(value = "/rs/transform/stdMasterTransform", method = RequestMethod.POST)
    public Map<String,Object> stdMasterTransform(
            @ApiParam(name="stdTransformDtoJson",value="资源数据模型",required = true)
            @RequestBody String stdTransformDtoJson) throws Exception {
        MStdTransformDto stdTransformDto = objectMapper.readValue(stdTransformDtoJson,MStdTransformDto.class);
        String resource =  stdTransformDto.getSource();
        String version = stdTransformDto.getVersion();
        String dataset = stdTransformDto.getDataset();
        Map<String, Object> map = objectMapper.readValue(resource, Map.class);

        return resourcesTransformService.stdMasterTransform(map, dataset, version);
    }

}
