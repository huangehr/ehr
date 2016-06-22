package com.yihu.ehr.patient.service;

import com.yihu.ehr.api.ServiceApi;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.patient.MArRelation;
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
public interface ArRelationClient {

    @RequestMapping(value = ServiceApi.Patients.ArRelations, method = RequestMethod.GET)
    @ApiOperation(value = "档案关联列表")
    ResponseEntity<List<MArRelation>> search(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);


    @RequestMapping(value = ServiceApi.Patients.ArRelations, method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "新增档案关联")
    MArRelation add(
            @RequestBody String model);

    @RequestMapping(value = ServiceApi.Patients.ArRelations, method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "修改档案关联")
    MArRelation update(
            @RequestBody String model);

    @RequestMapping(value = ServiceApi.Patients.ArRelation, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除档案关联")
    boolean delete(
            @PathVariable(value = "id") int id);

    @RequestMapping(value = ServiceApi.Patients.ArRelations, method = RequestMethod.DELETE)
    @ApiOperation(value = "删除档案关联")
    boolean batchDelete(
            @RequestParam(value = "ids") Object[] ids);

    @RequestMapping(value = ServiceApi.Patients.ArRelation, method = RequestMethod.GET)
    @ApiOperation(value = "获取档案关联信息")
    MArRelation getInfo(
            @PathVariable(value = "id") int id);

}
