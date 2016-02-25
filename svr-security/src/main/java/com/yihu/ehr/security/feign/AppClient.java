package com.yihu.ehr.security.feign;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.app.MApp;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(MicroServices.AppMgr)
@RequestMapping(value = "/api/v1.0")
@ApiIgnore
public interface AppClient {

    @RequestMapping(value = "/apps/existence/{app_id}", method = GET )
    boolean isAppExistence(
            @PathVariable(value = "app_id") String appId,
            @RequestParam(value = "secret") String appSecret);

    @RequestMapping(value = "/apps/{app_id}", method = GET )
    MApp getApp(@PathVariable(value = "app_id") String appId);


}
