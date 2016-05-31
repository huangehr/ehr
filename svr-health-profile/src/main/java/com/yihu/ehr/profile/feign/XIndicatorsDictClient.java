package com.yihu.ehr.profile.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.specialdict.MIndicatorsDict;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
public interface XIndicatorsDictClient {

    @RequestMapping(value = "/indicators/ids", method = RequestMethod.GET)
    @ApiOperation(value = "根据ids获取相应的指标字典信息" )
    List<MIndicatorsDict> getIndicatorsDictByIds(
            @ApiParam(name = "ids", value = "指标代码")
            @RequestParam(value = "ids") List<String> ids);
}