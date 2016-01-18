package com.yihu.ehr.apps.service;

import com.yihu.ehr.model.dict.MBaseDict;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-dict")
public interface ConventionalDictClient {

    @RequestMapping(value = "/conventional_dict_service/orgType", method = GET,consumes = "application/json")
    MBaseDict getOrgType(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/conventional_dict_service/settledWay", method = GET,consumes = "application/json")
    MBaseDict getSettledWay(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/conventional_dict_service/appCatalog", method = GET,consumes = "application/json")
    MBaseDict getAppCatalog(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/conventional_dict_service/appStatus", method = GET,consumes = "application/json")
    MBaseDict getAppStatus(@RequestParam(value = "code") String code);









}
