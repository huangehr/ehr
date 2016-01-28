package com.yihu.ehr.org.feignClient.dict;

import com.yihu.ehr.model.dict.MBaseDict;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@EnableFeignClients
@FeignClient("svr-dict")
@RequestMapping("/rest/v1.0/conventional_dict")
public interface ConventionalDictClient {

    @RequestMapping(value = "/orgType", method = GET )
    MBaseDict getOrgType(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/settledWay", method = GET )
    MBaseDict getSettledWay(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/appCatalog", method = GET )
    MBaseDict getAppCatalog(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/appStatus", method = GET )
    MBaseDict getAppStatus(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/userType", method = GET )
    MBaseDict getUserType(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/yesNo", method = GET )
    MBaseDict getYesNo(@RequestParam(value = "code") Boolean code);


}
