package com.yihu.ehr.profile.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.specialdict.MIcd10Dict;
import com.yihu.ehr.model.specialdict.MIcd10DrugRelation;
import com.yihu.ehr.model.specialdict.MIcd10IndicatorRelation;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

/**
 * @author linaz
 * @created 2016.05.27 9:10
 */
@ApiIgnore
@FeignClient(MicroServices.SpecialDict)
@RequestMapping(ApiVersion.Version1_0)
public interface Icd10DictClient {

    @RequestMapping(value = "/dict/icd10/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取相应的ICD10字典信息。")
    MIcd10Dict getIcd10Dict(
            @PathVariable(value = "id") String id);

    @RequestMapping(value = "/dict/icd10s", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询相应的ICD10字典信息。")
    Collection<MIcd10Dict> getIcd10DictList(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = "dict/icd10/hp/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10的ID判断是否与健康问题存在关联。")
    boolean icd10DictIsUsage(
            @PathVariable(value = "id") String id);

    @RequestMapping(value = "/dict/icd10/existence/name", method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    boolean isNameExists(
            @RequestParam(value = "name") String name);

    @RequestMapping(value = "/dict/icd10/existence/code/{code}", method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典代码是否已经存在")
    boolean isCodeExists(
            @PathVariable(value = "code") String code);

    //-------------------------ICD10与药品之间关联关系管理-----------------------------------------------------------


    @RequestMapping(value = "/dict/icd10/drugs", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10查询相应的药品关联列表信息。")
    Collection<MIcd10DrugRelation> getIcd10DrugRelationList(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = "/dict/icd10/drugs/no_paging", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10查询相应的药品关联列表信息,------不分页。")
    Collection<MIcd10DrugRelation> getIcd10DrugRelationListWithoutPaging(
            @RequestParam(value = "filters", required = false) String filters);

    @RequestMapping(value = "/dict/icd10/drug/existence", method = RequestMethod.GET)
    @ApiOperation(value = "判断ICD10与药品字典的关联关系在系统中是否已存在")
    boolean isIcd10DrugRelaExist(
            @RequestParam(value = "drugId", required = false) String drugId,
            @RequestParam(value = "icd10Id", required = false) String icd10Id);

    //-------------------------ICD10与指标之间关联关系管理---开始--------------------------------------------------------

    @RequestMapping(value = "/dict/icd10/indicators", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10查询相应的指标关联列表信息。")
    Collection<MIcd10IndicatorRelation> getIcd10IndicatorRelationList(
            @RequestParam(value = "fields", required = false) String fields,
            @RequestParam(value = "filters", required = false) String filters,
            @RequestParam(value = "sorts", required = false) String sorts,
            @RequestParam(value = "size", required = false) int size,
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = "/dict/icd10/indicators/no_paging", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10查询相应的指标关联列表信息,---不分页。")
    Collection<MIcd10IndicatorRelation> getIcd10IndicatorRelationListWithoutPaging(
            @RequestParam(value = "filters", required = false) String filters);

    @RequestMapping(value = "/dict/icd10/indicator/existence", method = RequestMethod.GET)
    @ApiOperation(value = "判断ICD10与指标字典的关联关系在系统中是否已存在")
    boolean isIcd10IndicatorsRelaExist(
            @RequestParam(value = "indicatorsId", required = false) String indicatorsId,
            @RequestParam(value = "icd10Id", required = false) String icd10Id);
}
