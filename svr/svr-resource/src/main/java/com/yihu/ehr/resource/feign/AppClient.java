package com.yihu.ehr.resource.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.app.MApp;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author hzp
 * @created 2017.05.16
 */
@ApiIgnore
@FeignClient(name = MicroServices.Application)
@RequestMapping(ApiVersion.Version1_0)
public interface AppClient {


    @RequestMapping(value = ServiceApi.Apps.App, method = RequestMethod.GET)
    public MApp getApp(@PathVariable(value = "app_id") String appId);
}
