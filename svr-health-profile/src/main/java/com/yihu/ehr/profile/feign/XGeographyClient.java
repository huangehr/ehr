package com.yihu.ehr.profile.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.geography.MGeographyDict;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@FeignClient(name = MicroServices.Geography)
public interface XGeographyClient {


    @RequestMapping(value = ApiVersion.Version1_0+"/geography_entries/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询行政区划地址")
    MGeographyDict getAddressDictById(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id);

}
