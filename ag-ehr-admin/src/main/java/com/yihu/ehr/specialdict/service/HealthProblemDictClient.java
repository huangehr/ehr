package com.yihu.ehr.specialdict.service;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.specialdict.MHealthProblemDict;
import com.yihu.ehr.model.specialdict.MIcd10HpRelation;
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
public interface HealthProblemDictClient {

    @RequestMapping(value = "/dict/hp", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建新的健康问题字典" )
    MHealthProblemDict createHpDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson) ;

    @RequestMapping(value = "dict/hp/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除健康问题字典（含健康问题字典及与ICD10的关联关系。）")
    boolean deleteHpDict(
            @ApiParam(name = "id", value = "字典ID")
            @PathVariable(value = "id") long id) ;

    @RequestMapping(value = "dict/hps", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据ids批量删除健康问题字典（含健康问题字典及与ICD10的关联关系。）")
    boolean deleteHpDicts(
            @ApiParam(name = "ids", value = "字典IDs")
            @RequestParam(value = "ids") String ids) ;

    @RequestMapping(value = "/dict/hp", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "更新健康问题字典" )
    MHealthProblemDict updateHpDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson) ;

    @RequestMapping(value = "/dict/hp/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取相应的健康问题字典信息。" )
    MHealthProblemDict getHpDict(
            @ApiParam(name = "id", value = "字典内码")
            @PathVariable(value = "id") long id);

    @RequestMapping(value = "/dict/hps", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询相应的ICD10字典信息。" )
    ResponseEntity<Collection<MHealthProblemDict>> getHpDictList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,code,name,description")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+code,+name")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = "/dict/hp/existence/name" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    boolean isNameExists(
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name);

    @RequestMapping(value = "/dict/hp/existence/code/{code}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典代码是否已经存在")
    boolean isCodeExists(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code);

    //-------------------------健康问题与ICD10之间关联关系管理---------------------------------------------------------

    @RequestMapping(value = "/dict/hp/icd10", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为健康问题增加ICD10疾病关联。" )
    MIcd10HpRelation createHpIcd10Relation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson);

    @RequestMapping(value = "/dict/hp/icd10s", method = RequestMethod.POST)
    @ApiOperation(value = "为健康问题增加ICD10疾病关联,--批量增加关联。" )
    Collection<MIcd10HpRelation> createHpIcd10Relations(
            @ApiParam(name = "hp_id", value = "健康问题Id")
            @RequestParam(value = "hp_id") long hpId,
            @ApiParam(name = "icd10_ids", value = "关联的icd10字典ids,多个以逗号连接")
            @RequestParam(value = "icd10_ids") String icd10Ids,
            @ApiParam(name = "create_user",value = "创建者")
            @RequestParam(value = "create_user") String createUser);

    @RequestMapping(value = "/dict/hp/icd10", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "为健康问题修改ICD10疾病关联。" )
    MIcd10HpRelation updateHpIcd10Relation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson);

    @RequestMapping(value = "/dict/hp/icd10", method = RequestMethod.DELETE)
    @ApiOperation(value = "为健康问题删除ICD10疾病关联。" )
    boolean deleteHpIcd10Relation(
            @ApiParam(name = "id", value = "关联ID", defaultValue = "")
            @RequestParam(value = "id", required = true) long id);

    @RequestMapping(value = "/dict/hp/icd10s", method = RequestMethod.DELETE)
    @ApiOperation(value = "为健康问题删除ICD10疾病关联,批量删除关联。" )
    boolean deleteHpIcd10Relations(
            @ApiParam(name = "ids", value = "关联IDs", defaultValue = "")
            @RequestParam(value = "ids", required = true) String ids);

    @RequestMapping(value = "/dict/hp/icd10s", method = RequestMethod.GET)
    @ApiOperation(value = "根据健康问题查询相应的ICD10关联列表信息。" )
    ResponseEntity<Collection<MIcd10HpRelation>> getHpIcd10RelationList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,hpId,icd10Id")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+hpId")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page);

    @RequestMapping(value = "/dict/hp/icd10s/no_paging", method = RequestMethod.GET)
    @ApiOperation(value = "根据健康问题查询相应的ICD10关联列表信息。" )
    Collection<MIcd10HpRelation> getHpIcd10RelationListWithoutPaging(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters);

    @RequestMapping(value = "/dict/hp/icd10/existence" , method = RequestMethod.GET)
    @ApiOperation(value = "判断健康问题与ICD10的关联关系在系统中是否已存在")
    boolean isHpIcd10RelaExist(
            @ApiParam(name = "hpId", value = "健康问题内码")
            @RequestParam(value = "hpId", required = false) long hpId,
            @ApiParam(name = "icd10Id", value = "Icd10内码", defaultValue = "")
            @RequestParam(value = "icd10Id", required = false) long icd10Id);
}
