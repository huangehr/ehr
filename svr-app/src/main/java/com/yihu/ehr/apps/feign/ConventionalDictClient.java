package com.yihu.ehr.apps.feign;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.dict.MConventionalDict;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(MicroServices.DictMgr)
public interface ConventionalDictClient {
    @RequestMapping(value = "/rest/v1.0/conventional_dict/app_catalog", method = GET)
    MConventionalDict getAppCatalog(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/rest/v1.0/conventional_dict/app_status", method = GET)
    MConventionalDict getAppStatus(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/rest/v1.0/conventional_dict/user_type", method = GET)
    MConventionalDict getUserType(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/rest/v1.0/conventional_dict/yes_no", method = GET)
    MConventionalDict getYesNo(@RequestParam(value = "code") Boolean code);
}
