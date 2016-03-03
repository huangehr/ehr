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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
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
    public Object createIcd10Dict(
            @ApiParam(name = "code", value = "icd10字典编码")
            @RequestParam(value = "code", required = true) String code,
            @ApiParam(name = "name", value = "icd10字典名称")
            @RequestParam(value = "name", required = true) String name,
            @ApiParam(name = "chronicFlag", value = "慢病标识", defaultValue = "0")
            @RequestParam(value = "chronicFlag", required = false) String chronicFlag,
            @ApiParam(name = "infectiousFlag", value = "传染病标识", defaultValue = "0")
            @RequestParam(value = "infectiousFlag", required = false) String infectiousFlag,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description", required = false) String description) throws Exception {

        Icd10Dict icd10Dict =  new Icd10Dict();
        String id = getObjectId(BizObject.Dict);
        if(icd10DictService.isCodeExist(id,code,"0")){
            throw new ApiException(ErrorCode.RepeatCode, "代码在系统中已存在，请确认。");
        }
        if(icd10DictService.isNameExist(id,name,"0")){
            throw new ApiException(ErrorCode.RepeatName, "名称在系统中已存在，请确认。");
        }
        icd10Dict.setId(id);
        icd10Dict.setCode(code);
        icd10Dict.setName(name);
        icd10Dict.setChronicFlag(chronicFlag);
        icd10Dict.setInfectiousFlag(infectiousFlag);
        icd10Dict.setDescription(description);
        icd10DictService.save(icd10Dict);

        return true;
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

    @RequestMapping(value = "dict/icd10/hp/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10的ID判断是否与健康问题存在关联。")
    public boolean icd10DictIsUsage(
            @ApiParam(name = "id", value = "icd10字典代码")
            @PathVariable( value = "id") String id) {

        return hpIcd10RelationService.isUsage(id);
    }

    @RequestMapping(value = "/dict/ice10/{id}", method = RequestMethod.POST)
    @ApiOperation(value = "更新ICD10字典" )
    public Object updateIcd10Dict(
            @ApiParam(name = "id", value = "icd10字典内码")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "code", value = "icd10字典编码")
            @RequestParam(value = "code", required = true) String code,
            @ApiParam(name = "name", value = "icd10字典名称")
            @RequestParam(value = "name", required = true) String name,
            @ApiParam(name = "chronicFlag", value = "慢病标识", defaultValue = "0")
            @RequestParam(value = "chronicFlag", required = false) String chronicFlag,
            @ApiParam(name = "infectiousFlag", value = "传染病标识", defaultValue = "0")
            @RequestParam(value = "infectiousFlag", required = false) String infectiousFlag,
            @ApiParam(name = "description", value = "描述", defaultValue = "1")
            @RequestParam(value = "description", required = false) String description) throws Exception {

        Icd10Dict icd10Dict = icd10DictService.retrieve(id);
        if(icd10DictService.isCodeExist(id, code, "1")){
            throw new ApiException(ErrorCode.RepeatCode, "代码在系统中已存在，请确认。");
        }
        if(icd10DictService.isNameExist(id, name, "1")){
            throw new ApiException(ErrorCode.RepeatName, "名称在系统中已存在，请确认。");
        }
        icd10Dict.setCode(code);
        icd10Dict.setName(name);
        icd10Dict.setChronicFlag(chronicFlag);
        icd10Dict.setInfectiousFlag(infectiousFlag);
        icd10Dict.setDescription(description);
        icd10DictService.save(icd10Dict);

        return true;
    }

    @RequestMapping(value = "/dict/ice10/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取相应的ICD10字典信息。" )
    public Object getIcd10Dict(
            @ApiParam(name = "id", value = "icd10字典内码")
            @PathVariable(value = "id") String id) throws Exception {

        Icd10Dict icd10Dict = icd10DictService.retrieve(id);
        if(icd10Dict != null){
            return convertToModel(icd10Dict, MIcd10Dict.class);
        }else{
            throw new ApiException(ErrorCode.QueryNoData," 查询无数据，请确认。");
        }
    }

    @RequestMapping(value = "/dict/icd10s", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询相应的ICD10字典信息。" )
    public Object getIcd10DictList(
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

        List<Icd10Dict> icd10DictList = icd10DictService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, icd10DictService.getCount(filters), page, size);

        return (List<MIcd10Dict>)convertToModels(icd10DictList,
                new ArrayList<MIcd10Dict>(icd10DictList.size()), MIcd10Dict.class, fields);

    }

    //-------------------------ICD10与药品之间关联关系管理-----------------------------------------------------------

    @RequestMapping(value = "/dict/icd10/drug", method = RequestMethod.POST)
    @ApiOperation(value = "为ICD10增加药品关联。" )
    public Object createIcd10DrugRelation(
            @ApiParam(name = "icd10Id", value = "ICD10字典ID", defaultValue = "")
            @RequestParam(value = "icd10Id", required = true) String icd10Id,
            @ApiParam(name = "drugId", value = "药品字典ID", defaultValue = "")
            @RequestParam(value = "drugId", required = true) String drugId) throws Exception{

        Icd10DrugRelation icd10DrugRelation =  new Icd10DrugRelation();
        String id = getObjectId(BizObject.Dict);
        if(icd10DrugRelationService.isExist(id, icd10Id,drugId, "0")){
            throw new ApiException(ErrorCode.RepeatRelation, "该关联在系统中已存在，请确认。");
        }
        icd10DrugRelation.setId(id);
        icd10DrugRelation.setDrugId(drugId);
        icd10DrugRelation.setIcd10Id(icd10Id);

        icd10DrugRelationService.save(icd10DrugRelation);

        return true;
    }

    @RequestMapping(value = "/dict/icd10/drug/{id}", method = RequestMethod.POST)
    @ApiOperation(value = "为ICD10修改药品关联。" )
    public Object updateIcd10DrugRelation(
            @ApiParam(name = "id", value = "关联ID", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "icd10Id", value = "ICD10字典ID", defaultValue = "")
            @RequestParam(value = "icd10Id", required = true) String icd10Id,
            @ApiParam(name = "drugId", value = "药品字典ID", defaultValue = "")
            @RequestParam(value = "drugId", required = true) String drugId) throws Exception{

        Icd10DrugRelation icd10DrugRelation = icd10DrugRelationService.retrieve(id);
        if(icd10DrugRelation == null){
            throw new ApiException(ErrorCode.RepeatRelation, "在系统中找不到需要修改的关联信息，请确认。");
        }

        if(icd10DrugRelationService.isExist(id, icd10Id,drugId, "1")){
            throw new ApiException(ErrorCode.RepeatRelation, "该关联在系统中已存在，请确认。");
        }
        icd10DrugRelation.setDrugId(drugId);
        icd10DrugRelation.setIcd10Id(icd10Id);
        icd10DrugRelationService.save(icd10DrugRelation);

        return true;
    }

    @RequestMapping(value = "/dict/icd10/drug", method = RequestMethod.DELETE)
    @ApiOperation(value = "为ICD10删除药品关联。" )
    public Object deleteIcd10DrugRelation(
            @ApiParam(name = "id", value = "关联ID", defaultValue = "")
            @RequestParam(value = "id", required = true) String id) throws Exception{

        icd10DrugRelationService.delete(id);

        return true;
    }

    @RequestMapping(value = "/dict/icd10/drugs", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10查询相应的药品关联列表信息。" )
    public Object getIcd10DrugRelationList(
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

        List<Icd10DrugRelation> icd10DrugRelationList = icd10DrugRelationService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, icd10DrugRelationService.getCount(filters), page, size);

        return (List<MIcd10DrugRelation>)convertToModels(icd10DrugRelationList,
                new ArrayList<MIcd10DrugRelation>(icd10DrugRelationList.size()), MIcd10DrugRelation.class, fields);

    }

    //-------------------------ICD10与指标之间关联关系管理-----------------------------------------------------------

    @RequestMapping(value = "/dict/icd10/indicator", method = RequestMethod.POST)
    @ApiOperation(value = "为ICD10增加指标关联。" )
    public Object createIcd10IndicatorRelation(
            @ApiParam(name = "icd10Id", value = "ICD10字典ID", defaultValue = "")
            @RequestParam(value = "icd10Id", required = true) String icd10Id,
            @ApiParam(name = "indicatorId", value = "指标字典ID", defaultValue = "")
            @RequestParam(value = "indicatorId", required = true) String indicatorId) throws Exception{

        Icd10IndicatorRelation icd10IndicatorRelation =  new Icd10IndicatorRelation();
        String id = getObjectId(BizObject.Dict);
        if(icd10IndicatorRelationService.isExist(id, icd10Id,indicatorId, "0")){
            throw new ApiException(ErrorCode.RepeatRelation, "该关联在系统中已存在，请确认。");
        }
        icd10IndicatorRelation.setId(id);
        icd10IndicatorRelation.setIndicatorId(indicatorId);
        icd10IndicatorRelation.setIcd10Id(icd10Id);

        icd10IndicatorRelationService.save(icd10IndicatorRelation);

        return true;
    }

    @RequestMapping(value = "/dict/icd10/indicator/{id}", method = RequestMethod.POST)
    @ApiOperation(value = "为ICD10修改指标关联。" )
    public Object updateIcd10IndicatorRelation(
            @ApiParam(name = "id", value = "关联ID", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "icd10Id", value = "ICD10字典ID", defaultValue = "")
            @RequestParam(value = "icd10Id", required = true) String icd10Id,
            @ApiParam(name = "indicatorId", value = "指标字典ID", defaultValue = "")
            @RequestParam(value = "indicatorId", required = true) String indicatorId) throws Exception{

        Icd10IndicatorRelation icd10IndicatorRelation = icd10IndicatorRelationService.retrieve(id);
        if(icd10IndicatorRelation == null){
            throw new ApiException(ErrorCode.RepeatRelation, "在系统中找不到需要修改的关联信息，请确认。");
        }

        if(icd10IndicatorRelationService.isExist(id, icd10Id,indicatorId, "1")){
            throw new ApiException(ErrorCode.RepeatRelation, "该关联在系统中已存在，请确认。");
        }
        icd10IndicatorRelation.setIndicatorId(indicatorId);
        icd10IndicatorRelation.setIcd10Id(icd10Id);
        icd10IndicatorRelationService.save(icd10IndicatorRelation);

        return true;
    }

    @RequestMapping(value = "/dict/icd10/indicator", method = RequestMethod.DELETE)
    @ApiOperation(value = "为ICD10删除指标关联。" )
    public Object deleteIcd10IndicatorRelation(
            @ApiParam(name = "id", value = "关联ID", defaultValue = "")
            @RequestParam(value = "id", required = true) String id) throws Exception{

        icd10IndicatorRelationService.delete(id);

        return true;
    }

    @RequestMapping(value = "/dict/icd10/indicators", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10查询相应的指标关联列表信息。" )
    public Object getIcd10IndicatorRelationList(
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

        List<Icd10IndicatorRelation> icd10IndicatorRelations = icd10IndicatorRelationService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, icd10IndicatorRelationService.getCount(filters), page, size);

        return (List<MIcd10IndicatorRelation>)convertToModels(icd10IndicatorRelations,
                new ArrayList<MIcd10IndicatorRelation>(icd10IndicatorRelations.size()), MIcd10IndicatorRelation.class, fields);

    }
}