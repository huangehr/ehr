package com.yihu.ehr.patient.service;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.patient.MAuthentication;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author lincl
 * @version 1.0
 * @created 2016/6/22
 */
@FeignClient(name=MicroServices.Patient)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface AuthenticationClient {

    @RequestMapping(value = ServiceApi.Patients.Authentications, method = RequestMethod.GET)
    @ApiOperation(value = "人口身份认证申请列表")
    ResponseEntity<List<MAuthentication>> search(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);


    @RequestMapping(value = ServiceApi.Patients.Authentications, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增认证申请")
    MAuthentication add(
            @RequestBody String model) ;


    @RequestMapping(value = ServiceApi.Patients.Authentications, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改认证申请")
    MAuthentication update(
            @RequestBody String model);

    @RequestMapping(value = ServiceApi.Patients.Authentication, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除认证申请")
    boolean delete(
            @PathVariable(value = "id") int id);

    @RequestMapping(value = ServiceApi.Patients.Authentications, method = RequestMethod.DELETE)
    @ApiOperation(value = "批量删除认证申请")
    boolean batchDelete(
            @RequestParam(value = "ids") Object[] ids);

    @RequestMapping(value = ServiceApi.Patients.Authentication, method = RequestMethod.GET)
    @ApiOperation(value = "获取认证申请信息")
    MAuthentication getInfo(
            @PathVariable(value = "id") int id) ;

}
