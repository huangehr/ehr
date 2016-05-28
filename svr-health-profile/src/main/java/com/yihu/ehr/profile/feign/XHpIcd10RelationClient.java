package com.yihu.ehr.profile.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.specialdict.MIcd10HpRelation;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.28 9:32
 */
@ApiIgnore
@RequestMapping(ApiVersion.Version1_0)
@FeignClient(name = MicroServices.SpecialDict)
public interface XHpIcd10RelationClient {


    @RequestMapping(value = "/hp_icd10_relation/hp_id" , method = RequestMethod.GET)
    @ApiOperation(value = "根基健康问题id获取健康问题与ICD10的关联")
    List<MIcd10HpRelation> getHpIcd10RelationByHpId(
            @RequestParam(value = "hp_id", required = false) String hpId);

}