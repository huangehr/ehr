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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(protocols = "https", value = "healthproblemdict", description = "健康问题字典管理接口", tags = {"healthProblem"})
public class HealthProblemDictController extends BaseRestController {

    @Autowired
    private HealthProblemDictService hpDictService;
    @Autowired
    private HpIcd10RelationService hpIcd10RelationService;

    @RequestMapping(value = "/dict/hp", method = RequestMethod.POST)
    @ApiOperation(value = "创建新的健康问题字典" )
    public Object createHpDict(
            @ApiParam(name = "code", value = "字典编码")
            @RequestParam(value = "code", required = true) String code,
            @ApiParam(name = "name", value = "字典名称")
            @RequestParam(value = "name", required = true) String name,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description", required = false) String description) throws Exception {

        HealthProblemDict hpDict =  new HealthProblemDict();
        String id = getObjectId(BizObject.Dict);
        if(hpDictService.isCodeExist(id,code,"0")){
            throw new ApiException(ErrorCode.RepeatCode, "代码在系统中已存在，请确认。");
        }
        if(hpDictService.isNameExist(id,name,"0")){
            throw new ApiException(ErrorCode.RepeatName, "名称在系统中已存在，请确认。");
        }
        hpDict.setId(id);
        hpDict.setCode(code);
        hpDict.setName(name);
        hpDict.setDescription(description);

        hpDictService.save(hpDict);

        return true;
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

    @RequestMapping(value = "/dict/hp/{id}", method = RequestMethod.POST)
    @ApiOperation(value = "更新健康问题字典" )
    public Object updateHpDict(
            @ApiParam(name = "id", value = "字典内码")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "code", value = "字典编码")
            @RequestParam(value = "code", required = true) String code,
            @ApiParam(name = "name", value = "字典名称")
            @RequestParam(value = "name", required = true) String name,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description", required = false) String description) throws Exception {

        HealthProblemDict hpDict = hpDictService.retrieve(id);
        if(hpDictService.isCodeExist(id, code, "1")){
            throw new ApiException(ErrorCode.RepeatCode, "代码在系统中已存在，请确认。");
        }
        if(hpDictService.isNameExist(id, name, "1")){
            throw new ApiException(ErrorCode.RepeatName, "名称在系统中已存在，请确认。");
        }
        hpDict.setCode(code);
        hpDict.setName(name);
        hpDict.setDescription(description);

        hpDictService.save(hpDict);

        return true;
    }

    @RequestMapping(value = "/dict/hp/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取相应的健康问题字典信息。" )
    public Object getHpDict(
            @ApiParam(name = "id", value = "字典内码")
            @PathVariable(value = "id") String id) throws Exception {

        HealthProblemDict hpDict = hpDictService.retrieve(id);
        if(hpDict != null){
            return convertToModel(hpDict, MHealthProblemDict.class);
        }else{
            throw new ApiException(ErrorCode.QueryNoData," 查询无数据，请确认。");
        }
    }

    @RequestMapping(value = "/dict/hps", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询相应的ICD10字典信息。" )
    public Object getHpDictList(
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

        List<HealthProblemDict> hpDictList = hpDictService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, hpDictService.getCount(filters), page, size);

        return (List<MHealthProblemDict>)convertToModels(hpDictList,
                new ArrayList<MHealthProblemDict>(hpDictList.size()), MHealthProblemDict.class, fields);
    }

    @RequestMapping(value = "/dict/hp/icd10", method = RequestMethod.POST)
    @ApiOperation(value = "为健康问题增加ICD10疾病关联。" )
    public Object createHpIcd10Relation(
            @ApiParam(name = "hpId", value = "健康问题ID", defaultValue = "")
            @RequestParam(value = "hpId", required = true) String hpId,
            @ApiParam(name = "icd10Id", value = "ICD10字典ID", defaultValue = "")
            @RequestParam(value = "icd10Id", required = false) String icd10Id) throws Exception{

        HpIcd10Relation hpIcd10Relation =  new HpIcd10Relation();
        String id = getObjectId(BizObject.Dict);
        if(hpIcd10RelationService.isExist(id, hpId,icd10Id, "0")){
            throw new ApiException(ErrorCode.RepeatRelation, "该关联在系统中已存在，请确认。");
        }
        hpIcd10Relation.setId(id);
        hpIcd10Relation.setHpId(hpId);
        hpIcd10Relation.setIcd10Id(icd10Id);

        hpIcd10RelationService.save(hpIcd10Relation);

        return true;
    }

    @RequestMapping(value = "/dict/hp/icd10/{id}", method = RequestMethod.POST)
    @ApiOperation(value = "为健康问题修改ICD10疾病关联。" )
    public Object updateHpIcd10Relation(
            @ApiParam(name = "id", value = "关联ID", defaultValue = "")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "hpId", value = "健康问题ID", defaultValue = "")
            @RequestParam(value = "hpId", required = true) String hpId,
            @ApiParam(name = "icd10Id", value = "ICD10字典ID", defaultValue = "")
            @RequestParam(value = "icd10Id", required = false) String icd10Id) throws Exception{

        HpIcd10Relation hpIcd10Relation = hpIcd10RelationService.retrieve(id);
        if(hpIcd10Relation == null){
            throw new ApiException(ErrorCode.RepeatRelation, "在系统中找不到需要修改的关联信息，请确认。");
        }

        if(hpIcd10RelationService.isExist(id, hpId,icd10Id, "1")){
            throw new ApiException(ErrorCode.RepeatRelation, "该关联在系统中已存在，请确认。");
        }
        hpIcd10Relation.setHpId(hpId);
        hpIcd10Relation.setIcd10Id(icd10Id);
        hpIcd10RelationService.save(hpIcd10Relation);
        return true;
    }

    @RequestMapping(value = "/dict/hp/icd10", method = RequestMethod.DELETE)
    @ApiOperation(value = "为健康问题删除ICD10疾病关联。" )
    public Object deleteHpIcd10Relation(
            @ApiParam(name = "id", value = "关联ID", defaultValue = "")
            @RequestParam(value = "id", required = true) String id) throws Exception{

        hpIcd10RelationService.delete(id);

        return true;
    }

    @RequestMapping(value = "/dict/hp/icd10s", method = RequestMethod.GET)
    @ApiOperation(value = "根据健康问题查询相应的ICD10关联列表信息。" )
    public Object getHpIcd10RelationList(
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

        List<HpIcd10Relation> hpIcd10RelationList = hpIcd10RelationService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, hpIcd10RelationService.getCount(filters), page, size);

        return (List<MHpIcd10Relation>)convertToModels(hpIcd10RelationList,
                new ArrayList<MHpIcd10Relation>(hpIcd10RelationList.size()), MHpIcd10Relation.class, fields);

    }
}