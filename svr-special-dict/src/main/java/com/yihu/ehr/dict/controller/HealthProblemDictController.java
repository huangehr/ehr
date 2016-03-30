package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.dict.service.HealthProblemDict;
import com.yihu.ehr.dict.service.HealthProblemDictService;
import com.yihu.ehr.dict.service.HpIcd10Relation;
import com.yihu.ehr.dict.service.HpIcd10RelationService;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.specialdict.MHealthProblemDict;
import com.yihu.ehr.model.specialdict.MHpIcd10Relation;
import com.yihu.ehr.util.controller.BaseRestController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "healthproblemdict", description = "健康问题字典管理接口", tags = {"healthProblem"})
public class HealthProblemDictController extends BaseRestController {

    @Autowired
    private HealthProblemDictService hpDictService;
    @Autowired
    private HpIcd10RelationService hpIcd10RelationService;

    @RequestMapping(value = "/dict/hp", method = RequestMethod.POST)
    @ApiOperation(value = "创建新的健康问题字典" )
    public MHealthProblemDict createHpDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) throws Exception {

        HealthProblemDict dict = toEntity(dictJson, HealthProblemDict.class);
        String id = getObjectId(BizObject.Dict);
        dict.setId(id);
        HealthProblemDict healthProblemDict = hpDictService.createDict(dict);
        return convertToModel(healthProblemDict, MHealthProblemDict.class, null);
    }

     @RequestMapping(value = "dict/hp/{id}", method = RequestMethod.DELETE)
     @ApiOperation(value = "根据id删除健康问题字典（含健康问题字典及与ICD10的关联关系。）")
     public boolean deleteHpDict(
            @ApiParam(name = "id", value = "字典代码")
            @PathVariable( value = "id") String id) {

        String relationId;
        List<HpIcd10Relation> hpIcd10RelationList = hpIcd10RelationService.getHpIcd10RelationListByHpId(id);
        if (hpIcd10RelationList != null) {
            for(HpIcd10Relation hpIcd10Relation : hpIcd10RelationList ){
                relationId = hpIcd10Relation.getId();
                hpIcd10RelationService.delete(relationId);
            }
        }
        hpDictService.delete(id);

        return true;
    }

    @RequestMapping(value = "/dict/hp", method = RequestMethod.PUT)
    @ApiOperation(value = "更新健康问题字典" )
    public MHealthProblemDict updateHpDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson)  throws Exception{

        HealthProblemDict dict = toEntity(dictJson, HealthProblemDict.class);
        if (null == hpDictService.retrieve(dict.getId())) throw new ApiException(ErrorCode.GetDictFaild, "字典不存在");
        hpDictService.save(dict);
        return convertToModel(dict, MHealthProblemDict.class);
    }

    @RequestMapping(value = "/dict/hp/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取相应的健康问题字典信息。" )
    public MHealthProblemDict getHpDict(
            @ApiParam(name = "id", value = "字典内码")
            @PathVariable(value = "id") String id) throws Exception {

        HealthProblemDict dict = hpDictService.retrieve(id);
        if (dict == null) throw new ApiException(ErrorCode.GetDictFaild, "字典不存在");
        return convertToModel(dict, MHealthProblemDict.class);
    }

    @RequestMapping(value = "/dict/hps", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询相应的ICD10字典信息。" )
    public Collection<MHealthProblemDict> getHpDictList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,code,name,description")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+code,+name")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{

        page = reducePage(page);

        if (StringUtils.isEmpty(filters)) {
            Page<HealthProblemDict> hpDictPage = hpDictService.getDictList(sorts, page, size);
            pagedResponse(request, response, hpDictPage.getTotalElements(), page, size);
            return convertToModels(hpDictPage.getContent(), new ArrayList<>(hpDictPage.getNumber()), MHealthProblemDict.class, fields);
        } else {
            List<HealthProblemDict> hpDictList = hpDictService.search(fields, filters, sorts, page, size);
            pagedResponse(request, response, hpDictService.getCount(filters), page, size);
            return convertToModels(hpDictList, new ArrayList<>(hpDictList.size()), MHealthProblemDict.class, fields);
        }
    }

    @RequestMapping(value = "/dict/hp/existence/name/{name}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    public boolean isNameExists(
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @PathVariable(value = "name") String name){
        return hpDictService.isNameExist(name);
    }

    @RequestMapping(value = "/dict/hp/existence/code/{code}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典代码是否已经存在")
    public boolean isCodeExists(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code){
        return hpDictService.isCodeExist(code);
    }

    //-------------------------健康问题与ICD10之间关联关系管理-----------------------------------------------------------

    @RequestMapping(value = "/dict/hp/icd10", method = RequestMethod.POST)
    @ApiOperation(value = "为健康问题增加ICD10疾病关联。" )
    public MHpIcd10Relation createHpIcd10Relation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) throws Exception {

        HpIcd10Relation relation = toEntity(dictJson, HpIcd10Relation.class);
        String id = getObjectId(BizObject.Dict);
        relation.setId(id);
        hpIcd10RelationService.save(relation);
        return convertToModel(relation, MHpIcd10Relation.class, null);
    }

    @RequestMapping(value = "/dict/hp/icd10", method = RequestMethod.PUT)
    @ApiOperation(value = "为健康问题修改ICD10疾病关联。" )
    public MHpIcd10Relation updateHpIcd10Relation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) throws Exception {

        HpIcd10Relation relation = toEntity(dictJson, HpIcd10Relation.class);
        if (null == hpIcd10RelationService.retrieve(relation.getId())) throw new ApiException(ErrorCode.GetDictFaild, "该关联不存在");
        hpIcd10RelationService.save(relation);
        return convertToModel(relation, MHpIcd10Relation.class);
    }

    @RequestMapping(value = "/dict/hp/icd10", method = RequestMethod.DELETE)
    @ApiOperation(value = "为健康问题删除ICD10疾病关联。" )
    public boolean deleteHpIcd10Relation(
            @ApiParam(name = "id", value = "关联ID", defaultValue = "")
            @RequestParam(value = "id", required = true) String id) throws Exception{

        hpIcd10RelationService.delete(id);

        return true;
    }

    @RequestMapping(value = "/dict/hp/icd10s", method = RequestMethod.GET)
    @ApiOperation(value = "根据健康问题查询相应的ICD10关联列表信息。" )
    public Collection<MHpIcd10Relation> getHpIcd10RelationList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,hpId,icd10Id")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+hpId")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{

        page = reducePage(page);

        if (StringUtils.isEmpty(filters)) {
            Page<HpIcd10Relation> hpIcd10RelationPage = hpIcd10RelationService.getRelationList(sorts, page, size);
            pagedResponse(request, response, hpIcd10RelationPage.getTotalElements(), page, size);
            return convertToModels(hpIcd10RelationPage.getContent(), new ArrayList<>(hpIcd10RelationPage.getNumber()), MHpIcd10Relation.class, fields);
        } else {
            List<HpIcd10Relation> hpIcd10RelationList = hpIcd10RelationService.search(fields, filters, sorts, page, size);
            pagedResponse(request, response, hpIcd10RelationService.getCount(filters), page, size);
            return convertToModels(hpIcd10RelationList, new ArrayList<>(hpIcd10RelationList.size()), MHpIcd10Relation.class, fields);
        }
    }

    @RequestMapping(value = "/dict/hp/icd10/existence" , method = RequestMethod.GET)
    @ApiOperation(value = "判断健康问题与ICD10的关联关系在系统中是否已存在")
    public boolean isHpIcd10RelaExist(

            @ApiParam(name = "icd10Id", value = "Icd10内码", defaultValue = "")
            @RequestParam(value = "icd10Id", required = false) String icd10Id,
            @ApiParam(name = "hpId", value = "健康问题内码")
            @RequestParam(value = "hpId", required = false) String hpId) throws Exception {

        return hpIcd10RelationService.isExist(icd10Id,hpId);
    }

}