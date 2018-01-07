package com.yihu.ehr.resolve.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.Result;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author hzp
 * @created 2017.04.13
 */
//@ApiIgnore
//@FeignClient(name = MicroServices.Patient)
//@RequestMapping(ApiVersion.Version1_0)
@Deprecated
public interface ArchiveClient {

    @RequestMapping(value = ServiceApi.Patients.ArchiveRelation, method = RequestMethod.POST)
    Result archiveRelation(@RequestBody String data);

}
