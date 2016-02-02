package com.yihu.ehr.patient.feignClient;

import com.yihu.ehr.model.dict.MConventionalDict;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@EnableFeignClients
@FeignClient("svr-dict")
@RequestMapping("/rest/v1.0/conventional_dict")
public interface ConventionalDictClient {

    @RequestMapping(value = "/orgType", method = GET,consumes = "application/json")
    MConventionalDict getOrgType(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/settledWay", method = GET,consumes = "application/json")
    MConventionalDict getSettledWay(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/appCatalog", method = GET,consumes = "application/json")
    MConventionalDict getAppCatalog(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/appStatus", method = GET,consumes = "application/json")
    MConventionalDict getAppStatus(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/userType", method = GET,consumes = "application/json")
    MConventionalDict getUserType(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/yesNo", method = GET,consumes = "application/json")
    MConventionalDict getYesNo(@RequestParam(value = "code") Boolean code);

    @RequestMapping(value = "/cardType", method = GET,consumes = "application/json")
    MConventionalDict getCardType(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/cardStatus", method = GET,consumes = "application/json")
    MConventionalDict getCardStatus(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/gender", method = GET,consumes = "application/json")
    MConventionalDict getGender(@RequestParam(value = "code") String code);


}
