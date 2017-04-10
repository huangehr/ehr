package com.yihu.ehr.adaption.feignclient;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
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
@FeignClient(name = MicroServices.Standard)
public interface DictClient {

    @RequestMapping(value = ApiVersion.Version1_0+ ServiceApi.Standards.MetaDataWithDict, method = RequestMethod.GET)
    @ApiOperation(value = "获取字典 map集")
    Map getDictMapByIds(
            @RequestParam(value = "version") String version,
            @PathVariable(value = "data_set_id") Long dataSetId,
            @PathVariable(value = "meta_data_id") Long metaDataId);

}
