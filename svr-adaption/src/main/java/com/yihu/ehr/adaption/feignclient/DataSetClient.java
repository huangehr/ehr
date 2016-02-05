package com.yihu.ehr.adaption.feignclient;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


/**
 * Created by Administrator on 2016/1/4.
 */
@EnableFeignClients
@FeignClient("svr-standard")
@RequestMapping("/rest")
public interface DataSetClient {

    @RequestMapping(value = "/{api_version}/std/dataset/map", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集 id-name : map集")
    public Map getDataSetMapByIds(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "ids", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "ids") String ids);

    @RequestMapping(value = "/{api_version}/std/dataset/metamap", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据元 id-name : map集")
    public Map getMetaDataMapByIds(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "ids", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "ids") String dataSetIds,
            @ApiParam(name = "medaIds", value = "数据元编号", defaultValue = "")
            @RequestParam(value = "medaIds") String metaIds) ;
}
