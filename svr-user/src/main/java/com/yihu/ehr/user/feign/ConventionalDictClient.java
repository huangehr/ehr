package com.yihu.ehr.user.feign;

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
@RequestMapping(value = "/rest/v1.0")
public interface ConventionalDictClient {

    @RequestMapping(value = "/dictionaries/martial_status", method = GET )
    MConventionalDict getMartialStatus(
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/dictionaries/gender", method = GET )
    MConventionalDict getGender(
            @RequestParam(value = "code") String code);


    @RequestMapping(value = "/dictionaries/user_type", method = GET )
    MConventionalDict getUserType(
            @RequestParam(value = "code") String code);
}
