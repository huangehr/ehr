package com.yihu.ehr.apps.feign;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.dict.MConventionalDict;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@EnableFeignClients
@FeignClient(MicroServices.DictMgr)
public interface ConventionalDictClient {

    @RequestMapping(value = "/rest/{api_version}/conventional_dict/app_catalog", method = GET )
    MConventionalDict getAppCatalog(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/rest/{api_version}/conventional_dict/app_status", method = GET )
    MConventionalDict getAppStatus(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/rest/{api_version}/conventional_dict/user_type", method = GET )
    MConventionalDict getUserType(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/rest/{api_version}/conventional_dict/yes_no", method = GET )
    MConventionalDict getYesNo(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "code") Boolean code);


}
