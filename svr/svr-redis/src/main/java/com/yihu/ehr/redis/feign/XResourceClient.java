package com.yihu.ehr.redis.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.entity.geography.GeographyDict;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author hzp
 * @created 2017.05.09
 */
@ApiIgnore
@FeignClient(name = MicroServices.Resource)
@RequestMapping(ApiVersion.Version1_0)
public interface XResourceClient {

    @RequestMapping(value= ServiceApi.Adaptions.Cache,method = RequestMethod.POST)
    boolean cacheData(@PathVariable(value = "id")String id);
}
