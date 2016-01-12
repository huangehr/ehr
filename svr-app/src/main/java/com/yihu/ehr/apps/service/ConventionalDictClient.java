package com.yihu.ehr.apps.service;

import com.yihu.ehr.model.BaseDict;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-dict")
public interface ConventionalDictClient {

    @RequestMapping(value = "/conventional_dict_service/getOrgType", method = GET,consumes = "application/json")
    BaseDict getOrgType(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/conventional_dict_service/getSettledWay", method = GET,consumes = "application/json")
    BaseDict getSettledWay(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/conventional_dict_service/getAppCatalog", method = GET,consumes = "application/json")
    BaseDict getAppCatalog(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/conventional_dict_service/getAppStatus", method = GET,consumes = "application/json")
    BaseDict getAppStatus(@RequestParam(value = "code") String code);









}
