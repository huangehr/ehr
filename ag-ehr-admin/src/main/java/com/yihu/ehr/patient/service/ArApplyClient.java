package com.yihu.ehr.patient.service;

import com.yihu.ehr.constants.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.patient.MArApply;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
public interface ArApplyClient {

    @RequestMapping(value = ServiceApi.Patients.ArApplications, method = RequestMethod.GET)
    @ApiOperation(value = "档案关联申请列表")
    ResponseEntity<List<MArApply>> search(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);


    @RequestMapping(value = ServiceApi.Patients.ArApplications, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增档案关联申请")
    MArApply add(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody String model);

    @RequestMapping(value = ServiceApi.Patients.ArApplications, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改档案关联申请")
    MArApply update(
            @ApiParam(name = "model", value = "json数据模型", defaultValue = "")
            @RequestBody MArApply model);

    @RequestMapping(value = ServiceApi.Patients.ArApplication, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除档案关联申请")
    boolean delete(
            @ApiParam(name = "id", value = "编号", defaultValue = "")
            @PathVariable(value = "id") int id) ;

    @RequestMapping(value = ServiceApi.Patients.ArApplications, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除档案关联申请")
    boolean batchDelete(
            @ApiParam(name = "ids", value = "编号集", defaultValue = "")
            @RequestParam(value = "ids") Object[] ids);

    @RequestMapping(value = ServiceApi.Patients.ArApplication, method = RequestMethod.GET)
    @ApiOperation(value = "获取档案关联申请信息")
    MArApply getInfo(
            @ApiParam(name = "id", value = "档案关联申请编号", defaultValue = "")
            @PathVariable(value = "id") int id) ;
}
