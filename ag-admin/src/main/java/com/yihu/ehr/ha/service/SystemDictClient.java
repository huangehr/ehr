package com.yihu.ehr.ha.service;

import com.yihu.ehr.model.dict.MBaseDict;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by AndyCai on 2016/1/19.
 */
@FeignClient("svr-dict")
public interface SystemDictClient {
    @RequestMapping(value = "/conventional_dict_service/getOrgType", method = GET,consumes = "application/json")
    MBaseDict getOrgType(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/conventional_dict_service/getSettledWay", method = GET,consumes = "application/json")
    MBaseDict getSettledWay(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/conventional_dict_service/getAppCatalog", method = GET,consumes = "application/json")
    MBaseDict getAppCatalog(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/conventional_dict_service/getAppStatus", method = GET,consumes = "application/json")
    MBaseDict getAppStatus(@RequestParam(value = "code") String code);
}
