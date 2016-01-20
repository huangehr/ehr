package com.yihu.ehr.feignClient.dict;

import com.yihu.ehr.model.dict.MBaseDict;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-dict")
@RequestMapping("/rest/v1.0/conventional_dict")
public interface ConventionalDictClient {

    @RequestMapping(value = "/orgType", method = GET,consumes = "application/json")
    MBaseDict getOrgType(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/settledWay", method = GET,consumes = "application/json")
    MBaseDict getSettledWay(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/appCatalog", method = GET,consumes = "application/json")
    MBaseDict getAppCatalog(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/appStatus", method = GET,consumes = "application/json")
    MBaseDict getAppStatus(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/userType", method = GET,consumes = "application/json")
    MBaseDict getUserType(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/yesNo", method = GET,consumes = "application/json")
    MBaseDict getYesNo(@RequestParam(value = "code") Boolean code);




}
