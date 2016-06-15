package com.yihu.ehr.profile.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.specialdict.MIcd10Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@ApiIgnore
@RequestMapping(ApiVersion.Version1_0)
@FeignClient(name = MicroServices.SpecialDict)
public interface XIcd10DictClient {

    @RequestMapping(value = "/icd10_dict/ids" , method = RequestMethod.GET)
    @ApiOperation(value = "根据id列表获取ICD10字典信息")
    List<MIcd10Dict> getIcd10DictListByIds(@RequestParam(value = "ids") List<String> ids);

    @RequestMapping(value = "/hp_icd10_relation_cache/one" , method = RequestMethod.GET)
    @ApiOperation(value = "获取单个缓存")
    MIcd10Dict getIcd10DictValue(@RequestParam(value = "icd10_id") String icd10Id);

}