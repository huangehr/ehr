package com.yihu.ehr.org.feignClient.dict;

import com.yihu.ehr.model.dict.MBaseDict;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient("svr-dict")
public interface ConventionalDictClient {

    @RequestMapping(value = "/rest/{api_version}/conventional_dict/orgType", method = GET )
    MBaseDict getOrgType(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/rest/{api_version}/conventional_dict/settledWay", method = GET )
    MBaseDict getSettledWay(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/rest/{api_version}/conventional_dict/appCatalog", method = GET )
    MBaseDict getAppCatalog(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/rest/{api_version}/conventional_dict/appStatus", method = GET )
    MBaseDict getAppStatus(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/rest/{api_version}/conventional_dict/userType", method = GET )
    MBaseDict getUserType(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/rest/{api_version}/conventional_dict/yesNo", method = GET )
    MBaseDict getYesNo(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "code") Boolean code);


}
