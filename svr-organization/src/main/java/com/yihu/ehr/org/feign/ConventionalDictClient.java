package com.yihu.ehr.org.feign;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.dict.MConventionalDict;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(MicroServices.DictMgr)
public interface ConventionalDictClient {

    @RequestMapping(value = "/rest/{api_version}/dictionaries/org_type", method = GET )
    MConventionalDict getOrgType(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "code") String code);


    @RequestMapping(value = "/rest/{api_version}/dictionaries/settled_way", method = GET )
    MConventionalDict getSettledWay(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "code") String code);



}
