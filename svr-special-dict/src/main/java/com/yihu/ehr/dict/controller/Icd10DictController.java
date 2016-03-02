package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.dict.service.*;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.specialdict.MIcd10Dict;
import com.yihu.ehr.model.specialdict.MIcd10DrugRelation;
import com.yihu.ehr.model.specialdict.MIcd10IndicatorRelation;
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
@Api(protocols = "https", value = "icd10dict", description = "疾病字典管理接口", tags = {"ICD10"})
public class Icd10DictController extends BaseRestController {

    @Autowired
    private Icd10DictService icd10DictService;
    @Autowired
    private HpIcd10RelationService hpIcd10RelationService;
    @Autowired
    private Icd10DrugRelationService icd10DrugRelationService;
    @Autowired
    private Icd10IndicatorRelationService icd10IndicatorRelationService;

    @RequestMapping(value = "/dict/icd10", method = RequestMethod.POST)
    @ApiOperation(value = "创建新的ICD10字典" )
    public MIcd10Dict createIcd10Dict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) throws Exception {

        Icd10Dict dict = toEntity(dictJson, Icd10Dict.class);
        String id = getObjectId(BizObject.Dict);
        dict.setId(id);
        Icd10Dict  icd10Dict= icd10DictService.createDict(dict);
        return convertToModel(icd10Dict, MIcd10Dict.class, null);
    }

    @RequestMapping(value = "dict/icd10/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除icd10疾病字典(含与药品及指标的关联关系。)")
    public boolean deleteIcd10Dict(
            @ApiParam(name = "id", value = "icd10字典代码")
            @PathVariable( value = "id") String id) {

        String drugRelationId;
        List<Icd10DrugRelation> icd10DrugRelations = icd10DrugRelationService.getIcd10DrugRelationListByIcd10Id(id);
        if (icd10DrugRelations != null) {
            for(Icd10DrugRelation icd10DrugRelation : icd10DrugRelations ){
                drugRelationId = icd10DrugRelation.getId();
                icd10DrugRelationService.delete(drugRelationId);
            }
        }
        String indicationRelationId;
        List<Icd10IndicatorRelation> icd10IndicatorRelations = icd10IndicatorRelationService.getIcd10IndicatorRelationListByIcd10Id(id);
        if (icd10IndicatorRelations != null) {
            for(Icd10IndicatorRelation icd10IndicatorRelation : icd10IndicatorRelations ){
                indicationRelationId = icd10IndicatorRelation.getId();
                icd10IndicatorRelationService.delete(indicationRelationId);
            }
        }
        icd10DictService.delete(id);

        return true;
    }

    @RequestMapping(value = "/dict/ice10/{id}", method = RequestMethod.POST)
    @ApiOperation(value = "更新ICD10字典" )
    public MIcd10Dict updateIcd10Dict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson)  throws Exception {

        Icd10Dict dict = toEntity(dictJson, Icd10Dict.class);
        if (null == icd10DictService.retrieve(dict.getId())) throw new ApiException(ErrorCode.GetDictFaild, "字典不存在");
        icd10DictService.save(dict);
        return convertToModel(dict, MIcd10Dict.class);
    }

    @RequestMapping(value = "/dict/ice10/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取相应的ICD10字典信息。" )
    public MIcd10Dict getIcd10Dict(
            @ApiParam(name = "id", value = "icd10字典内码")
            @PathVariable(value = "id") String id) throws Exception {

        Icd10Dict dict = icd10DictService.retrieve(id);
        if (dict == null) throw new ApiException(ErrorCode.GetDictFaild, "字典不存在");
        return convertToModel(dict, MIcd10Dict.class);
    }

    @RequestMapping(value = "/dict/icd10s", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询相应的ICD10字典信息。" )
    public Collection<MIcd10Dict> getIcd10DictList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,code,name,chronicFlag,infectiousFlag,description")
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
            Page<Icd10Dict> icd10DictPage = icd10DictService.getDictList(sorts, page, size);
            pagedResponse(request, response, icd10DictPage.getTotalElements(), page, size);
            return convertToModels(icd10DictPage.getContent(), new ArrayList<>(icd10DictPage.getNumber()), MIcd10Dict.class, fields);
        } else {
            List<Icd10Dict> icd10DictList = icd10DictService.search(fields, filters, sorts, page, size);
            pagedResponse(request, response, icd10DictService.getCount(filters), page, size);
            return convertToModels(icd10DictList, new ArrayList<>(icd10DictList.size()), MIcd10Dict.class, fields);
        }
    }

    @RequestMapping(value = "dict/icd10/hp/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10的ID判断是否与健康问题存在关联。")
    public boolean icd10DictIsUsage(
            @ApiParam(name = "id", value = "icd10字典代码")
            @PathVariable( value = "id") String id) {

        return hpIcd10RelationService.isUsage(id);
    }

    @RequestMapping(value = "/dict/icd10/{name}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    public boolean isNameExists(
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @PathVariable(value = "name") String name){
        return icd10DictService.isNameExist(name);
    }

    @RequestMapping(value = "/dict/icd10/{code}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典代码是否已经存在")
    public boolean isCodeExists(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code){
        return icd10DictService.isCodeExist(code);
    }

    //-------------------------ICD10与药品之间关联关系管理-----------------------------------------------------------

    @RequestMapping(value = "/dict/icd10/drug", method = RequestMethod.POST)
    @ApiOperation(value = "为ICD10增加药品关联。" )
    public MIcd10DrugRelation createIcd10DrugRelation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) throws Exception {

        Icd10DrugRelation relation = toEntity(dictJson, Icd10DrugRelation.class);
        String id = getObjectId(BizObject.Dict);
        relation.setId(id);
        icd10DrugRelationService.save(relation);
        return convertToModel(relation, MIcd10DrugRelation.class, null);
    }

    @RequestMapping(value = "/dict/icd10/drug/{id}", method = RequestMethod.POST)
    @ApiOperation(value = "为ICD10修改药品关联。" )
    public MIcd10DrugRelation updateIcd10DrugRelation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) throws Exception {

        Icd10DrugRelation relation = toEntity(dictJson, Icd10DrugRelation.class);
        if (null == icd10DrugRelationService.retrieve(relation.getId())) throw new ApiException(ErrorCode.GetDictFaild, "该关联不存在");
        icd10DrugRelationService.save(relation);
        return convertToModel(relation, MIcd10DrugRelation.class);
    }

    @RequestMapping(value = "/dict/icd10/drug", method = RequestMethod.DELETE)
    @ApiOperation(value = "为ICD10删除药品关联。" )
    public boolean deleteIcd10DrugRelation(
            @ApiParam(name = "id", value = "关联ID", defaultValue = "")
            @RequestParam(value = "id", required = true) String id) throws Exception{

        icd10DrugRelationService.delete(id);

        return true;
    }

    @RequestMapping(value = "/dict/icd10/drugs", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10查询相应的药品关联列表信息。" )
    public  Collection<MIcd10DrugRelation> getIcd10DrugRelationList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,icd10Id,drugId")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+icd10Id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{

        page = reducePage(page);

        if (StringUtils.isEmpty(filters)) {
            Page<Icd10DrugRelation> icd10DrugRelationPage = icd10DrugRelationService.getRelationList(sorts, page, size);
            pagedResponse(request, response, icd10DrugRelationPage.getTotalElements(), page, size);
            return convertToModels(icd10DrugRelationPage.getContent(), new ArrayList<>(icd10DrugRelationPage.getNumber()), MIcd10DrugRelation.class, fields);
        } else {
            List<Icd10DrugRelation> icd10DrugRelationList = icd10DrugRelationService.search(fields, filters, sorts, page, size);
            pagedResponse(request, response, icd10DrugRelationService.getCount(filters), page, size);
            return convertToModels(icd10DrugRelationList, new ArrayList<>(icd10DrugRelationList.size()), MIcd10DrugRelation.class, fields);
        }
    }

    @RequestMapping(value = "/dict/icd10/drug/existence" , method = RequestMethod.GET)
    @ApiOperation(value = "判断ICD10与药品字典的关联关系在系统中是否已存在")
    public boolean isIcd10DrugRelaExist(
            @ApiParam(name = "drugId", value = "药品字典内码")
            @RequestParam(value = "drugId", required = false) String drugId,
            @ApiParam(name = "icd10Id", value = "Icd10内码", defaultValue = "")
            @RequestParam(value = "icd10Id", required = false) String icd10Id) throws Exception {

        return icd10DrugRelationService.isExist(icd10Id,drugId);
    }

    //-------------------------ICD10与指标之间关联关系管理-----------------------------------------------------------

    @RequestMapping(value = "/dict/icd10/indicator", method = RequestMethod.POST)
    @ApiOperation(value = "为ICD10增加指标关联。" )
    public MIcd10IndicatorRelation createIcd10IndicatorRelation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) throws Exception{

        Icd10IndicatorRelation relation = toEntity(dictJson, Icd10IndicatorRelation.class);
        String id = getObjectId(BizObject.Dict);
        relation.setId(id);
        icd10IndicatorRelationService.save(relation);
        return convertToModel(relation, MIcd10IndicatorRelation.class, null);
    }

    @RequestMapping(value = "/dict/icd10/indicator/{id}", method = RequestMethod.POST)
    @ApiOperation(value = "为ICD10修改指标关联。" )
    public MIcd10IndicatorRelation updateIcd10IndicatorRelation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) throws Exception {

        Icd10IndicatorRelation relation = toEntity(dictJson, Icd10IndicatorRelation.class);
        if (null == icd10IndicatorRelationService.retrieve(relation.getId())) throw new ApiException(ErrorCode.GetDictFaild, "该关联不存在");
        icd10IndicatorRelationService.save(relation);
        return convertToModel(relation, MIcd10IndicatorRelation.class);
    }

    @RequestMapping(value = "/dict/icd10/indicator", method = RequestMethod.DELETE)
    @ApiOperation(value = "为ICD10删除指标关联。" )
    public boolean deleteIcd10IndicatorRelation(
            @ApiParam(name = "id", value = "关联ID", defaultValue = "")
            @RequestParam(value = "id", required = true) String id) throws Exception{

        icd10IndicatorRelationService.delete(id);

        return true;
    }

    @RequestMapping(value = "/dict/icd10/indicators", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10查询相应的指标关联列表信息。" )
    public Collection<MIcd10IndicatorRelation> getIcd10IndicatorRelationList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,icd10Id,indicatorId")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+icd10Id")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{

        page = reducePage(page);

        if (StringUtils.isEmpty(filters)) {
            Page<Icd10IndicatorRelation> icd10IndicatorRelationPage = icd10IndicatorRelationService.getRelationList(sorts, page, size);
            pagedResponse(request, response, icd10IndicatorRelationPage.getTotalElements(), page, size);
            return convertToModels(icd10IndicatorRelationPage.getContent(), new ArrayList<>(icd10IndicatorRelationPage.getNumber()), MIcd10IndicatorRelation.class, fields);
        } else {
            List<Icd10IndicatorRelation> icd10IndicatorRelationList = icd10IndicatorRelationService.search(fields, filters, sorts, page, size);
            pagedResponse(request, response, icd10IndicatorRelationService.getCount(filters), page, size);
            return convertToModels(icd10IndicatorRelationList, new ArrayList<>(icd10IndicatorRelationList.size()), MIcd10IndicatorRelation.class, fields);
        }
    }

    @RequestMapping(value = "/dict/icd10/indicator/existence" , method = RequestMethod.GET)
    @ApiOperation(value = "判断ICD10与指标字典的关联关系在系统中是否已存在")
    public boolean isIcd10IndicatorsRelaExist(
            @ApiParam(name = "indicatorsId", value = "药品字典内码")
            @RequestParam(value = "indicatorsId", required = false) String indicatorsId,
            @ApiParam(name = "icd10Id", value = "Icd10内码", defaultValue = "")
            @RequestParam(value = "icd10Id", required = false) String icd10Id) throws Exception {

        return icd10IndicatorRelationService.isExist(icd10Id,indicatorsId);
    }
}