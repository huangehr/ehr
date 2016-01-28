package com.yihu.ehr.security.feignClient.app;
import com.yihu.ehr.model.app.MApp;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-app")
@RequestMapping("/rest/v1.0/app")
public interface AppClient {

    @RequestMapping(value = "/validation", method = GET )
    Boolean validationApp(
            @RequestParam(value = "appId") String appId,
            @RequestParam(value = "appSecret") String appSecret);


    @RequestMapping(value = "app", method = GET )
    MApp getApp(@RequestParam(value = "appId") String appId);


}
