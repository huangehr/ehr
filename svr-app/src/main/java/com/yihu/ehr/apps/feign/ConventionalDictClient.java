package com.yihu.ehr.apps.feign;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.dict.MConventionalDict;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(MicroServices.DictMgr)
public interface ConventionalDictClient {
    @ApiIgnore
    @RequestMapping(value = "/rest/v1.0/dictionaries/app_catalog", method = GET)
    MConventionalDict getAppCatalog(@RequestParam(value = "code") String code);

    @ApiIgnore
    @RequestMapping(value = "/rest/v1.0/dictionaries/app_status", method = GET)
    MConventionalDict getAppStatus(@RequestParam(value = "code") String code);

    @ApiIgnore
    @RequestMapping(value = "/rest/v1.0/dictionaries/user_type", method = GET)
    MConventionalDict getUserType(@RequestParam(value = "code") String code);

    @ApiIgnore
    @RequestMapping(value = "/rest/v1.0/dictionaries/yes_no", method = GET)
    MConventionalDict getYesNo(@RequestParam(value = "code") Boolean code);
}
