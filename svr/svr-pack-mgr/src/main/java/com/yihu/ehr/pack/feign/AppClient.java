package com.yihu.ehr.pack.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.app.MApp;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by AndyCai on 2016/1/19.
 */
@FeignClient(name=MicroServices.Basic)
public interface AppClient {


    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Apps.App, method = RequestMethod.GET)
    @ApiOperation(value = "获取App")
    MApp getApp(
            @ApiParam(name = "app_id", value = "id", defaultValue = "")
            @PathVariable(value = "app_id") String appId);


}
