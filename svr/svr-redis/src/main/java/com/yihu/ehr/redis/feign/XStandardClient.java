package com.yihu.ehr.redis.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author hzp
 * @created 2017.05.09
 */
@ApiIgnore
@FeignClient(name = MicroServices.Resource)
@RequestMapping(ApiVersion.Version1_0)
public interface XStandardClient {

    @RequestMapping(value = ServiceApi.Redis.Versions, method = RequestMethod.PUT)
    public void versions(@RequestParam("versions") String versions,
                         @RequestParam("force") boolean force);

}
