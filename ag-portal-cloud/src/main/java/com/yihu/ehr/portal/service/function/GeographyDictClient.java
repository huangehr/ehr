package com.yihu.ehr.portal.service.function;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.geography.MGeographyDict;
import com.yihu.ehr.model.org.MOrganization;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by AndyCai on 2016/2/1.
 */
@FeignClient(name=MicroServices.Organization)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface GeographyDictClient {

    @RequestMapping(value = ServiceApi.Geography.AddressDict, method = RequestMethod.GET)
    @ApiOperation(value = "根据id查询行政区划地址")
    public MGeographyDict getAddressDictById(
            @ApiParam(name = "id", value = "id", defaultValue = "")
            @PathVariable(value = "id") String id) ;

}
