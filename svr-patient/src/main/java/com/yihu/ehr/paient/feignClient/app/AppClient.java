package com.yihu.ehr.paient.feignClient.app;
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

    @RequestMapping(value = "/validation", method = GET ,consumes = "application/json")
    Boolean validationApp(
            @RequestParam(value = "id") String appId,
            @RequestParam(value = "appSecret") String appSecret);


    @RequestMapping(value = "app", method = GET ,consumes = "application/json")
    MApp getApp(@RequestParam(value = "appId") String appId);


}
