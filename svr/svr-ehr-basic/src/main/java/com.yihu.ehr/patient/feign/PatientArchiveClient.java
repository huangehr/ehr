package com.yihu.ehr.patient.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.model.common.Result;
import com.yihu.ehr.model.geography.MGeography;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;


/**
 * Created by hzp on 2016/4/17.
 */
@FeignClient(name = MicroServices.PackageResolve)
@ApiIgnore
public interface PatientArchiveClient {


    @RequestMapping(value = ApiVersion.Version1_0+ ServiceApi.Packages.ArchiveRelation, method = RequestMethod.POST)
    Result archiveRelation(@RequestParam(value = "profileId") String profileId, @RequestParam(value = "idCardNo") String idCardNo);



}
