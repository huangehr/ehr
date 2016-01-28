package com.yihu.ehr.security.feignClient.app;
import com.yihu.ehr.model.app.MApp;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-app")
public interface AppClient {

    @RequestMapping(value = "/rest/{api_version}/app/validation", method = GET )
    Boolean validationApp(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "appId") String appId,
            @RequestParam(value = "appSecret") String appSecret);


    @RequestMapping(value = "/rest/{api_version}/app/app", method = GET )
    MApp getApp(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "appId") String appId);


}
