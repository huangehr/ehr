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
public interface DictClient {

    @RequestMapping(value = "/{api_version}/cdadict/map", method = RequestMethod.GET)
    @ApiOperation(value = "获取字典 map集")
    public Map getDataSetMapByIds(
            @ApiParam(name = "api_version", value = "API版本号", defaultValue = "v1.0")
            @PathVariable( value = "api_version") String apiVersion,
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "dataSetId", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "dataSetId") Long dataSetId,
            @ApiParam(name = "metaDataId", value = "数据元编号", defaultValue = "")
            @RequestParam(value = "metaDataId") Long metaDataId);

}
