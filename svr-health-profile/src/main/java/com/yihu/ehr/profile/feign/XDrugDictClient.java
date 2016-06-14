package com.yihu.ehr.profile.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.specialdict.MDrugDict;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author linaz
 * @created 2016.05.28 11:40
 */
@ApiIgnore
@RequestMapping(ApiVersion.Version1_0)
@FeignClient(name = MicroServices.SpecialDict)
public interface XDrugDictClient{

    @RequestMapping(value = "/drugs/ids", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取相应的药品字典信息。" )
    List<MDrugDict> getDrugDictByIds(
            @RequestParam(value = "ids") List<String> ids);
}