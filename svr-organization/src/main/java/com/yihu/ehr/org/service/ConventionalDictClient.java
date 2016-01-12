package com.yihu.ehr.org.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-dict")
public interface ConventionalDictClient {

    @RequestMapping(value = "/conventional_dict_service/getOrgType", method = GET,consumes = "application/json")
    String getOrgType(String code);

    @RequestMapping(value = "/conventional_dict_service/getSettledWay", method = GET,consumes = "application/json")
    String getSettledWay(String code);


}
