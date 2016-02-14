package com.yihu.ehr.patient.feign;

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

    @RequestMapping(value = "/rest/{api_version}/dictionaries/martial_status", method = GET )
    MConventionalDict getMartialStatus(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/rest/{api_version}/dictionaries/gender", method = GET )
    MConventionalDict getGender(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "code") String code);


    @RequestMapping(value = "/rest/{api_version}/dictionaries/user_type", method = GET )
    MConventionalDict getUserType(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/rest/{api_version}/dictionaries/residence_type", method = GET )
    MConventionalDict getResidenceType(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/rest/{api_version}/dictionaries/card_status", method = GET )
    MConventionalDict getCardStatus(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "code") String code);

    @RequestMapping(value = "/rest/{api_version}/dictionaries/card_type", method = GET )
    MConventionalDict getCardType(
            @PathVariable(value = "api_version") String apiVersion,
            @RequestParam(value = "code") String code);

}

