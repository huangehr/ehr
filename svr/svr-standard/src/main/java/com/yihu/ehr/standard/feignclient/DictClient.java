package com.yihu.ehr.standard.feignclient;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.hos.model.standard.MStdDict;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.Map;


/**
 * @author lincl
 * @version 1.0
 * @created 2016.2.1
 */
@ApiIgnore
@FeignClient(name = MicroServices.Standard)
public interface DictClient {

    @RequestMapping(value = ApiVersion.Version1_0+ ServiceApi.Standards.MetaDataWithDict, method = RequestMethod.GET)
    @ApiOperation(value = "获取字典 map集")
    Map getDictMapByIds(
            @RequestParam(value = "version") String version,
            @PathVariable(value = "data_set_id") Long dataSetId,
            @PathVariable(value = "meta_data_id") Long metaDataId);

    @RequestMapping(value = ServiceApi.Standards.NoPageDictionaries, method = RequestMethod.GET)
    @ApiOperation(value = "标准字典不分页搜索")
    public Collection<MStdDict> searchSourcesWithoutPaging(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有条件", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "version", value = "版本", defaultValue = "")
            @RequestParam(value = "version") String version);

}
