package com.yihu.ehr.basic.patient.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.Result;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;


/**
 * Created by hzp on 2016/4/17.
 */
@FeignClient(name = MicroServices.PackageResolve)
@ApiIgnore
public interface PatientArchiveClient {


    @RequestMapping(value = ApiVersion.Version1_0+ ServiceApi.Packages.ArchiveRelation, method = RequestMethod.POST)
    Result archiveRelation(@RequestParam(value = "profileId") String profileId, @RequestParam(value = "idCardNo") String idCardNo);



}
