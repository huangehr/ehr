package com.yihu.ehr.patient.feign;

import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.dict.MConventionalDict;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Administrator on 2016/1/4.
 */
@FeignClient(MicroServices.DictMgr)
@RequestMapping(value = "/api/v1.0")
@ApiIgnore
public interface ConventionalDictClient {

    @ApiIgnore
    @RequestMapping(value = "/dictionaries/martial_status", method = GET )
    MConventionalDict getMartialStatus(@RequestParam(value = "code") String code);

    @ApiIgnore
    @RequestMapping(value = "/dictionaries/gender", method = GET )
    MConventionalDict getGender(@RequestParam(value = "code") String code);

    @ApiIgnore
    @RequestMapping(value = "/dictionaries/user_type", method = GET )
    MConventionalDict getUserType(@RequestParam(value = "code") String code);


    @RequestMapping(value = "/dictionaries/residence_type", method = GET )
    MConventionalDict getResidenceType(@RequestParam(value = "code") String code);

    @ApiIgnore
    @RequestMapping(value = "/dictionaries/card_status", method = GET )
    MConventionalDict getCardStatus(@RequestParam(value = "code") String code);

    @ApiIgnore
    @RequestMapping(value = "/dictionaries/card_type", method = GET )
    MConventionalDict getCardType(@RequestParam(value = "code") String code);


    @RequestMapping(value = "/dictionaries/std_source_types", method = GET )
    Collection<MConventionalDict> getStdSourceTypeList(@RequestParam(value = "codes") List<String> codes);


}

