package com.yihu.ehr.security.feign;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.app.MApp;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(MicroServices.AppMgr)
public interface AppClient {

    @RequestMapping(value = "/rest/{api_version}/app/validation/{app_id}/{secret}", method = GET )
    Boolean validationApp(
            @PathVariable(value = "api_version") String apiVersion,
            @PathVariable(value = "app_id") String appId,
            @PathVariable(value = "secret") String appSecret);


    @RequestMapping(value = "/rest/{api_version}/app", method = GET )
    MApp getApp(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "appId") String appId);


}
