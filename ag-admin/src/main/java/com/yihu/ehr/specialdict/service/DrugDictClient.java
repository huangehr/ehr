package com.yihu.ehr.specialdict.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.specialdict.MDrugDict;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

/**
 * Created by CWS on 2016/2/29.
 */
@FeignClient(MicroServices.SpecialDict)
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface DrugDictClient {

    @ApiOperation(value = "创建字典",  produces = "application/json")
    @RequestMapping(value = "/dict/drug", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MDrugDict createDrugDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson) ;

    @ApiOperation(value = "删除字典")
    @RequestMapping(value = "dict/drug/{id}", method = RequestMethod.DELETE)
    boolean deleteDrugDict(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id);

    @ApiOperation(value = "批量删除字典")
    @RequestMapping(value = "dict/drugs", method = RequestMethod.DELETE)
    boolean deleteDrugDicts(
            @ApiParam(name = "ids", value = "字典IDs", defaultValue = "")
            @RequestParam(value = "ids") String ids);

    @ApiOperation(value = "修改字典")
    @RequestMapping(value = "/dict/drug", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    MDrugDict updateDrugDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson) ;

    @ApiOperation(value = "获取字典")
    @RequestMapping(value = "/dict/drug/{id}", method = RequestMethod.GET)
    MDrugDict getDrugDict(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") long id);

    @ApiOperation(value = "获取字典列表")
    @RequestMapping(value = "/dict/drugs", method = RequestMethod.GET)
    ResponseEntity<Collection<MDrugDict>> getDrugDictList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) Integer page) ;

    @RequestMapping(value = "/dict/drug/icd10/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据drug的ID判断是否与ICD10字典存在关联。")
    boolean isUsage(
            @ApiParam(name = "id", value = "药品字典代码")
            @PathVariable( value = "id") long id);

    @ApiOperation(value = "判断提交的字典代码是否已经存在")
    @RequestMapping(value = "/dict/drug/existence/code/{code}" , method = RequestMethod.GET)
    boolean isCodeExist(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code);

    @RequestMapping(value = "/dict/drug/existence/name" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    boolean isNameExist(
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name);
}
