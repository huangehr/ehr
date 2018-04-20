package com.yihu.ehr.feign;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.app.MApp;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(name= MicroServices.Application)
@ApiIgnore
public interface AppClient {

    @RequestMapping(value = ApiVersion.Version1_0+ ServiceApi.Apps.App,method = RequestMethod.GET)
    MApp getApp(@PathVariable(value = "app_id") String appId);




}
