package com.yihu.ehr.profile.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.specialdict.MIcd10DrugRelation;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.28 9:46
 */
@ApiIgnore
@RequestMapping(ApiVersion.Version1_0)
@FeignClient(name = MicroServices.SpecialDict)
public interface XIcd10DrugRelationClient {

    @RequestMapping(value = "/icd10_drug_relations/icd10_ids" , method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10Id列表获取IDC10和药品关系")
    List<MIcd10DrugRelation> getIcd10DrugRelationsByIcd10Ids(
            @RequestParam(value = "icd10_ids", required = false) List<String> icd10Ids);
}