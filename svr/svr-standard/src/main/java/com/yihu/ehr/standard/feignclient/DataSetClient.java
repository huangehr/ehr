package com.yihu.ehr.standard.feignclient;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.hos.model.standard.MStdDataSet;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.Map;


/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@FeignClient(name = MicroServices.Standard)
public interface DataSetClient {

    @RequestMapping(value = ApiVersion.Version1_0+ ServiceApi.Standards.DataSetsName, method = RequestMethod.GET)
    @ApiOperation(value = "获取数据集 id-name : map集")
    Map getDataSetMapByIds(
            @RequestParam(value = "version") String version,
            @RequestParam(value = "ids", required = false) String ids);


    @RequestMapping(value =ApiVersion.Version1_0+ ServiceApi.Standards.MetaDatasName, method = RequestMethod.POST)
    @ApiOperation(value = "获取数据元 id-name : map集")
    Map getMetaDataMapByIds(
            @RequestBody String parmModel);

    @RequestMapping(value =ApiVersion.Version1_0+  ServiceApi.Standards.NoPageDataSets, method = RequestMethod.GET)
    @ApiOperation(value = "标准数据集不分页搜索")
    public Collection<MStdDataSet> searchSourcesWithoutPaging(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version) throws Exception ;

}
