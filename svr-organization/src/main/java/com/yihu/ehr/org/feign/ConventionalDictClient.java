package com.yihu.ehr.org.feign;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.dict.MConventionalDict;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(MicroServices.DictMgr)
@RequestMapping(value = "/rest/v1.0", method = GET )
public interface ConventionalDictClient {

    @RequestMapping(value = "/dictionaries/org_type", method = GET )
    MConventionalDict getOrgType(
            @RequestParam(value = "code") String code);


    @RequestMapping(value = "/dictionaries/settled_way", method = GET )
    MConventionalDict getSettledWay(
            @RequestParam(value = "code") String code);



}
