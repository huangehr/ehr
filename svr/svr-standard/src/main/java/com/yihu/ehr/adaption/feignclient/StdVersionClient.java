package com.yihu.ehr.adaption.feignclient;


import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.hos.model.standard.MSTDVersion;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;


/**
 * @author lincl
 * @version 1.0
 * @created 2016.3.2
 */
//@ApiIgnore
//@FeignClient(name = MicroServices.Standard)
@Deprecated
public interface StdVersionClient {

    @RequestMapping(value = ApiVersion.Version1_0+ ServiceApi.Standards.Version, method = RequestMethod.GET)
    @ApiOperation(value = "获取版本信息")
    MSTDVersion getVersion(@PathVariable(value = "version") String version) throws Exception;
}
