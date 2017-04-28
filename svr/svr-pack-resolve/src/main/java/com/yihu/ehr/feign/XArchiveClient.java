package com.yihu.ehr.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ArchiveStatus;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.packs.MPackage;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Map;

/**
 * @author hzp
 * @created 2017.04.13
 */
@ApiIgnore
@FeignClient(name = MicroServices.Patient)
public interface XArchiveClient {

    @RequestMapping(value = ApiVersion.Version1_0 + ServiceApi.Patients.ArchiveRelation, method = RequestMethod.POST)
    Result archiveRelation(@RequestBody String data);

}
