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
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "icd10dict", description = "疾病字典管理接口")
public class Icd10DictController extends BaseRestController {

    @Autowired
    private Icd10DictService icd10DictService;

    @Autowired
    private HpIcd10RelationService hpIcd10RelationService;

    @Autowired
    private Icd10DrugRelationService icd10DrugRelationService;

    @Autowired
    private Icd10IndicatorRelationService icd10IndicatorRelationService;

    @RequestMapping(value = "/dict/icd10", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建新的ICD10字典" )
    public MIcd10Dict createIcd10Dict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson) throws Exception {

        Icd10Dict dict = toEntity(dictJson, Icd10Dict.class);
        dict.setCreateDate(new Date());
        String id = getObjectId(BizObject.Dict);
        dict.setId(id);
        Icd10Dict  icd10Dict= icd10DictService.createDict(dict);
        return convertToModel(icd10Dict, MIcd10Dict.class, null);
    }

    @RequestMapping(value = "dict/icd10/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除icd10疾病字典(含与药品及指标的关联关系，同时删除关联的诊断。)")
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

    @RequestMapping(value = "dict/icd10s", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据ids批量删除删除icd10疾病字典(含与药品及指标的关联关系，同时删除关联的诊断。)")
    public boolean deleteIcd10Dicts(
            @ApiParam(name = "ids", value = "icd10字典代码,多个以逗号隔开")
            @RequestParam( value = "ids") String ids) {
        String[] icd10Ids = ids.split(",");
        List<String> drugRelationIds = new ArrayList<>();
        List<String> indicationRelationIds = new ArrayList<>();
        for(String icd10Id:icd10Ids){
            List<Icd10DrugRelation> icd10DrugRelations = icd10DrugRelationService.getIcd10DrugRelationListByIcd10Id(icd10Id);
            if (icd10DrugRelations != null) {
                for(Icd10DrugRelation icd10DrugRelation : icd10DrugRelations ){
                    drugRelationIds.add(icd10DrugRelation.getId());
                }
            }
            List<Icd10IndicatorRelation> icd10IndicatorRelations = icd10IndicatorRelationService.getIcd10IndicatorRelationListByIcd10Id(icd10Id);
            if (icd10IndicatorRelations != null) {
                for(Icd10IndicatorRelation icd10IndicatorRelation : icd10IndicatorRelations ){
                    indicationRelationIds.add(icd10IndicatorRelation.getId());
                }
            }
        }
        if(drugRelationIds.size() != 0){
            icd10DrugRelationService.delete(drugRelationIds);
        }
        if(indicationRelationIds.size() != 0){
            icd10IndicatorRelationService.delete(indicationRelationIds);
        }
        icd10DictService.delete(icd10Ids);
        return true;
    }

    @RequestMapping(value = "/dict/icd10", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "更新ICD10字典" )
    public MIcd10Dict updateIcd10Dict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson)  throws Exception {

        Icd10Dict dict = toEntity(dictJson, Icd10Dict.class);
        if (null == icd10DictService.retrieve(dict.getId())) throw new ApiException(ErrorCode.GetDictFaild, "字典不存在");
        dict.setUpdateDate(new Date());
        icd10DictService.save(dict);
        return convertToModel(dict, MIcd10Dict.class);
    }

    @RequestMapping(value = "/dict/icd10/{id}", method = RequestMethod.GET)
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

    @RequestMapping(value = "/dict/icd10/existence/name" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    public boolean isNameExists(
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name){
        return icd10DictService.isNameExist(name);
    }

    @RequestMapping(value = "/dict/icd10/existence/code/{code}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典代码是否已经存在")
    public boolean isCodeExists(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code){
        return icd10DictService.isCodeExist(code);
    }

}