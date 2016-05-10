package com.yihu.ehr.specialdict.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.specialdict.*;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Collection;

/**
 * Created by CWS on 2016/2/29.
 */
@FeignClient("svr-special-dict")
@RequestMapping(ApiVersion.Version1_0)
@ApiIgnore
public interface Icd10DictClient {

    @RequestMapping(value = "/dict/icd10", method = RequestMethod.POST)
    @ApiOperation(value = "创建新的ICD10字典" )
    MIcd10Dict createIcd10Dict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson);

    @RequestMapping(value = "dict/icd10/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除icd10疾病字典(含与药品及指标的关联关系，同时删除关联的诊断。)")
    boolean deleteIcd10Dict(
            @ApiParam(name = "id", value = "icd10字典代码")
            @PathVariable( value = "id") String id);

    @RequestMapping(value = "dict/icd10s", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据ids批量删除icd10疾病字典(含与药品及指标的关联关系，同时删除关联的诊断。)")
    boolean deleteIcd10Dicts(
            @ApiParam(name = "ids", value = "icd10字典代码,多个以逗号隔开")
            @RequestParam( value = "ids") String ids);

    @RequestMapping(value = "/dict/icd10", method = RequestMethod.PUT)
    @ApiOperation(value = "更新ICD10字典" )
    MIcd10Dict updateIcd10Dict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson);

    @RequestMapping(value = "/dict/icd10/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取相应的ICD10字典信息。" )
    MIcd10Dict getIcd10Dict(
            @ApiParam(name = "id", value = "icd10字典内码")
            @PathVariable(value = "id") String id);

    @RequestMapping(value = "/dict/icd10s", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询相应的ICD10字典信息。" )
    ResponseEntity<Collection<MIcd10Dict>> getIcd10DictList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,code,name,chronicFlag,infectiousFlag,description")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+code,+name")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = "dict/icd10/hp/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10的ID判断是否与健康问题存在关联。")
    boolean icd10DictIsUsage(
            @ApiParam(name = "id", value = "icd10字典代码")
            @PathVariable( value = "id") String id);

    @RequestMapping(value = "/dict/icd10/existence/name" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    boolean isNameExists(
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name);

    @RequestMapping(value = "/dict/icd10/existence/code/{code}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典代码是否已经存在")
    boolean isCodeExists(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code);

    //-------------------------ICD10与药品之间关联关系管理---开始--------------------------------------------------------

    @RequestMapping(value = "/dict/icd10/drug", method = RequestMethod.POST)
    @ApiOperation(value = "为ICD10增加药品关联。" )
    MIcd10DrugRelation createIcd10DrugRelation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson);

    @RequestMapping(value = "/dict/icd10/drugs", method = RequestMethod.POST)
    @ApiOperation(value = "为ICD10增加药品关联。--批量关联" )
    Collection<MIcd10DrugRelation> createIcd10DrugRelations(
            @ApiParam(name = "icd10_id", value = "健康问题Id")
            @RequestParam(value = "icd10_id") String icd10Id,
            @ApiParam(name = "drug_ids", value = "关联的药品字典ids,多个以逗号连接")
            @RequestParam(value = "drug_ids") String drugIds,
            @ApiParam(name = "create_user",value = "创建者")
            @RequestParam(value = "create_user") String createUser);

    @RequestMapping(value = "/dict/icd10/drug", method = RequestMethod.PUT)
    @ApiOperation(value = "为ICD10修改药品关联。" )
    MIcd10DrugRelation updateIcd10DrugRelation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson);

    @RequestMapping(value = "/dict/icd10/drug", method = RequestMethod.DELETE)
    @ApiOperation(value = "为ICD10删除药品关联。" )
    boolean deleteIcd10DrugRelation(
            @ApiParam(name = "id", value = "关联ID", defaultValue = "")
            @RequestParam(value = "id", required = true) String id);

    @RequestMapping(value = "/dict/icd10/drugs", method = RequestMethod.DELETE)
    @ApiOperation(value = "为ICD10删除药品关联。--批量删除，多个以逗号隔开" )
    boolean deleteIcd10DrugRelations(
            @ApiParam(name = "ids", value = "关联IDs", defaultValue = "")
            @RequestParam(value = "ids", required = true) String ids);

    @RequestMapping(value = "/dict/icd10/drugs", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10查询相应的药品关联列表信息。" )
    ResponseEntity<Collection<MIcd10DrugRelation>> getIcd10DrugRelationList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,icd10Id,drugId")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+icd10Id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = "/dict/icd10/drugs/no_paging", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10查询相应的药品关联列表信息。---不分页" )
    Collection<MIcd10DrugRelation> getIcd10DrugRelationListWithoutPaging(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters);

    @RequestMapping(value = "/dict/icd10/drug/existence" , method = RequestMethod.GET)
    @ApiOperation(value = "判断ICD10与药品字典的关联关系在系统中是否已存在")
    boolean isIcd10DrugRelaExist(
            @ApiParam(name = "drugId", value = "药品字典内码")
            @RequestParam(value = "drugId", required = false) String drugId,
            @ApiParam(name = "icd10Id", value = "Icd10内码", defaultValue = "")
            @RequestParam(value = "icd10Id", required = false) String icd10Id);

    //-------------------------ICD10与药品之间关联关系管理----结束-------------------------------------------------------


    //-------------------------ICD10与指标之间关联关系管理--开始---------------------------------------------------------

    @RequestMapping(value = "/dict/icd10/indicator", method = RequestMethod.POST)
    @ApiOperation(value = "为ICD10增加指标关联。" )
    MIcd10IndicatorRelation createIcd10IndicatorRelation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson);

    @RequestMapping(value = "/dict/icd10/indicators", method = RequestMethod.POST)
    @ApiOperation(value = "为ICD10增加指标关联。---批量关联，" )
    Collection<MIcd10IndicatorRelation> createIcd10IndicatorRelations(
            @ApiParam(name = "icd10_id", value = "健康问题Id")
            @RequestParam(value = "icd10_id") String icd10Id,
            @ApiParam(name = "indicator_ids", value = "关联的指标字典ids,多个以逗号连接")
            @RequestParam(value = "indicator_ids") String indicatorIds,
            @ApiParam(name = "create_user",value = "创建者")
            @RequestParam(value = "create_user") String createUser);

    @RequestMapping(value = "/dict/icd10/indicator", method = RequestMethod.PUT)
    @ApiOperation(value = "为ICD10修改指标关联。" )
    MIcd10IndicatorRelation updateIcd10IndicatorRelation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson);

    @RequestMapping(value = "/dict/icd10/indicator", method = RequestMethod.DELETE)
    @ApiOperation(value = "为ICD10删除指标关联。" )
    boolean deleteIcd10IndicatorRelation(
            @ApiParam(name = "id", value = "关联ID", defaultValue = "")
            @RequestParam(value = "id", required = true) String id);

    @RequestMapping(value = "/dict/icd10/indicators", method = RequestMethod.DELETE)
    @ApiOperation(value = "为ICD10删除指标关联。--批量删除，多个以逗号隔开" )
    boolean deleteIcd10IndicatorRelations(
            @ApiParam(name = "ids", value = "关联IDs", defaultValue = "")
            @RequestParam(value = "ids", required = true) String ids);

    @RequestMapping(value = "/dict/icd10/indicators", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10查询相应的指标关联列表信息。" )
    ResponseEntity<Collection<MIcd10IndicatorRelation>> getIcd10IndicatorRelationList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,icd10Id,indicatorId")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+icd10Id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = "/dict/icd10/indicators/no_paging", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10查询相应的指标关联列表信息。---不分页" )
    Collection<MIcd10IndicatorRelation> getIcd10IndicatorRelationListWithoutPaging(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters);

    @RequestMapping(value = "/dict/icd10/indicator/existence" , method = RequestMethod.GET)
    @ApiOperation(value = "判断ICD10与指标字典的关联关系在系统中是否已存在")
    boolean isIcd10IndicatorsRelaExist(
            @ApiParam(name = "indicatorsId", value = "药品字典内码")
            @RequestParam(value = "indicatorsId", required = false) String indicatorsId,
            @ApiParam(name = "icd10Id", value = "Icd10内码", defaultValue = "")
            @RequestParam(value = "icd10Id", required = false) String icd10Id);
    //-------------------------ICD10与指标之间关联关系管理--结束---------------------------------------------------------


    //-------------------------ICD10与诊断之间关联关系管理--开始---------------------------------------------------------
    @RequestMapping(value = "/dict/icd10/diagnose",method = RequestMethod.POST)
    @ApiOperation(value = "新增icd10关联的诊断")
    MIcd10DiagnoseRelation createIcd10DiagnoseRelation(
            @ApiParam(name = "data_json",value = "关联关系json字符串")
            @RequestParam(value = "data_json") String dataJson);

    @RequestMapping(value = "/dict/icd10/diagnose",method = RequestMethod.PUT)
    @ApiOperation(value = "修改icd10关联的诊断信息")
    MIcd10DiagnoseRelation updateIcd10DiagnoseRelation(
            @ApiParam(name = "data_json",value = "关联关系json字符串")
            @RequestParam(value = "data_json") String dataJson);

    @RequestMapping(value = "/dict/icd10/diagnoses",method = RequestMethod.DELETE)
    @ApiOperation(value = "批量、单次删除关联的诊断，多个以逗号隔开")
    boolean deleteIcd10DiagnoseRelations(
            @ApiParam(name = "ids",value = "批量/单次删除关联诊断")
            @RequestParam(value = "ids") String ids);



    @RequestMapping(value = "/dict/icd10/diagnoses", method = RequestMethod.GET)
    @ApiOperation(value = "获取icd10与诊断关联关联列表,-----分页。" )
    ResponseEntity<Collection<MIcd10DiagnoseRelation>> searchIcd10diagnoseRelations(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,icd10Id,name,description,createUser")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+icd10Id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = "/dict/icd10/diagnoses/no_paging",method = RequestMethod.GET)
    @ApiOperation(value = "查询icd10的诊断关联关系列表，---不分页")
    Collection<MIcd10DiagnoseRelation> searchIcd10DiagnoseRelationsWithoutPaging(
            @ApiParam(name = "filters",value = "过滤条件，为空默认查询全部",defaultValue = "")
            @RequestParam(value = "filters",required = false) String filters);

    @RequestMapping(value = "/dict/icd10/diagnose/{id}",method = RequestMethod.GET)
    @ApiOperation(value = "根据关联关系id，获取关联信息")
    MIcd10DiagnoseRelation getIcd10DiagnoseRelation(
            @ApiParam(name = "id",value = "关联关系id")
            @PathVariable(value = "id") String id);

    //-------------------------ICD10与诊断之间关联关系管理--结束---------------------------------------------------------

}
