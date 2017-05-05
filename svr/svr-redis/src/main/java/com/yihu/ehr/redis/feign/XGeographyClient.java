package com.yihu.ehr.redis.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.geography.GeographyDict;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * @author hzp
 * @created 2017.05.04
 */
@ApiIgnore
@FeignClient(name = MicroServices.Geography)
@RequestMapping(ApiVersion.Version1_0)
public interface XGeographyClient {

    @RequestMapping(value = ServiceApi.Geography.AddressDictAll, method = RequestMethod.PUT)
    List<GeographyDict> getAllAddressDict();


}
