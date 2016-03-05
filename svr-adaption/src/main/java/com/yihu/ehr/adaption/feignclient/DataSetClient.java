package com.yihu.ehr.adaption.feignclient;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;


/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@ApiIgnore
@FeignClient(MicroServices.StandardMgr)
@RequestMapping(ApiVersion.Version1_0)
public interface DataSetClient {

    @RequestMapping(value = "/std/datasets/map", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集 id-name : map集")
    public Map getDataSetMapByIds(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "ids", value = "数据集编号", defaultValue = "")
            @RequestParam(value = "ids") String ids);

    @RequestMapping(value = "/std/metadatas/map", method = RequestMethod.GET)
    @ApiOperation(value = "获取数据元 id-name : map集")
    public Map getMetaDataMapByIds(
            @ApiParam(name = "version", value = "版本号", defaultValue = "")
            @RequestParam(value = "version") String version,
            @ApiParam(name = "medaIds", value = "数据元编号", defaultValue = "")
            @RequestParam(value = "medaIds") String metaIds);
}
