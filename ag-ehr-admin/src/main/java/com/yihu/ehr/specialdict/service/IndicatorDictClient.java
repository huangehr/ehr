package com.yihu.ehr.specialdict.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.specialdict.MIndicatorsDict;
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
public interface IndicatorDictClient {

    @RequestMapping(value = "/dict/indicator", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建新的指标字典" )
    MIndicatorsDict createIndicatorsDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson);

    @RequestMapping(value = "dict/indicator/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除指标字典")
    boolean deleteIndicatorsDict(
            @ApiParam(name = "id", value = "指标字典代码")
            @PathVariable( value = "id") long id);

    @RequestMapping(value = "dict/indicators", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据ids批量删除指标字典")
    boolean deleteIndicatorsDicts(
            @ApiParam(name = "ids", value = "指标字典代码")
            @RequestParam( value = "ids") String ids);

    @RequestMapping(value = "/dict/indicator", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "更新指标字典" )
    MIndicatorsDict updateIndicatorsDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson) ;

    @RequestMapping(value = "/dict/indicator/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取相应的指标字典信息。" )
    MIndicatorsDict getIndicatorsDict(
            @ApiParam(name = "id", value = "字典内码")
            @PathVariable(value = "id") long id);

    @RequestMapping(value = "/dict/indicators", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询相应的指标字典信息。" )
    ResponseEntity<Collection<MIndicatorsDict>> getIndicatorsDictList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page) ;

    @RequestMapping(value = "dict/indicator/icd10/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据指标的ID判断是否与ICD10字典存在关联。")
    boolean indicatorIsUsage(
            @ApiParam(name = "id", value = "指标字典代码")
            @PathVariable( value = "id") long id);

    @RequestMapping(value = "/dict/indicator/existence/name" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    boolean isNameExists(
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name);

    @RequestMapping(value = "/dict/indicator/existence/code/{code}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典代码是否已经存在")
    boolean isCodeExists(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code);

}
