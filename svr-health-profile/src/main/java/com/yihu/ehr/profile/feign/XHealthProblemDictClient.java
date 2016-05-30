package com.yihu.ehr.profile.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.specialdict.MHealthProblemDict;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author linaz
 * @created 2016.05.28 11:40
 */
@ApiIgnore
@RequestMapping(ApiVersion.Version1_0)
@FeignClient(name = MicroServices.SpecialDict)
public interface XHealthProblemDictClient {

    @RequestMapping(value = "/problemDict/code" , method = RequestMethod.GET)
    @ApiOperation(value = "根据字典代码获取健康问题字典")
    MHealthProblemDict getHpDictByCode(
            @RequestParam(value = "code") String code);

}