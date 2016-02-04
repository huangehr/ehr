package com.yihu.ehr.patient.feign;

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

    @RequestMapping(value = "/org_type", method = GET,consumes = "application/json")
    MConventionalDict getOrgType(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/settled_way", method = GET,consumes = "application/json")
    MConventionalDict getSettledWay(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/app_catalog", method = GET,consumes = "application/json")
    MConventionalDict getAppCatalog(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/app_status", method = GET,consumes = "application/json")
    MConventionalDict getAppStatus(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/user_type", method = GET,consumes = "application/json")
    MConventionalDict getUserType(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/yes_no", method = GET,consumes = "application/json")
    MConventionalDict getYesNo(@RequestParam(value = "code") Boolean code);

    @RequestMapping(value = "/card_type", method = GET,consumes = "application/json")
    MConventionalDict getCardType(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/card_status", method = GET,consumes = "application/json")
    MConventionalDict getCardStatus(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/gender", method = GET,consumes = "application/json")
    MConventionalDict getGender(@RequestParam(value = "code") String code);


    @RequestMapping(value = "/martial_status", method = GET,consumes = "application/json")
    MConventionalDict getMartialStatus(@RequestParam(value = "code") String code);

    @RequestMapping(value = "/residence_type", method = GET,consumes = "application/json")
    MConventionalDict getResidenceType(@RequestParam(value = "code") String code);

}
