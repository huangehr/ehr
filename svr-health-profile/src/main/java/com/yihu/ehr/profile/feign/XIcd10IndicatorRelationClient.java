package com.yihu.ehr.profile.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.specialdict.MIcd10IndicatorRelation;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.28 9:52
 */
@ApiIgnore
@RequestMapping(ApiVersion.Version1_0)
@FeignClient(name = MicroServices.SpecialDict)
public interface XIcd10IndicatorRelationClient {

    @RequestMapping(value = "/icd10_indicator_relations/icd10_ids" , method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10id列表获取IDC10和指标关系")
    List<MIcd10IndicatorRelation> getIcd10IndicatorRelationsByIcd10Ids(
            @RequestParam(value = "icd10_ids", required = false) List<String> icd10Ids);
}