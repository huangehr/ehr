package com.yihu.ehr.adaption.feignclient;

import com.yihu.ehr.api.RestApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import io.swagger.annotations.ApiOperation;
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
@FeignClient(name = MicroServices.StandardMgr)
@RequestMapping(ApiVersion.Version1_0)
public interface DataSetClient {

    @RequestMapping(value = RestApi.Standards.DataSetsName, method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集 id-name : map集")
    Map getDataSetMapByIds(
            @RequestParam(value = "version") String version,
            @RequestParam(value = "ids") String ids);


    @RequestMapping(value = RestApi.Standards.MetaDatasName, method = RequestMethod.GET)
    @ApiOperation(value = "获取数据元 id-name : map集")
    Map getMetaDataMapByIds(
            @RequestParam(value = "version") String version,
            @RequestParam(value = "medaIds") String metaIds);
}
