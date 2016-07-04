package com.yihu.ehr.specialdict.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.specialdict.MDrugDict;
import com.yihu.ehr.model.specialdict.MHealthProblemDict;
import com.yihu.ehr.model.specialdict.MIcd10Dict;
import com.yihu.ehr.model.specialdict.MIndicatorsDict;
import com.yihu.ehr.specialdict.model.*;
import com.yihu.ehr.specialdict.service.HealthProblemDictService;
import com.yihu.ehr.specialdict.service.Icd10HpRelationService;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
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
import java.util.*;

@RestController
@RequestMapping(ApiVersion.Version1_0)
@Api(value = "HealthProblemDict", description = "健康问题字典管理接口")
public class HealthProblemDictEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private HealthProblemDictService hpDictService;

    @Autowired
    private Icd10HpRelationService icd10HpRelationService;

    @RequestMapping(value = "/dict/hp", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建新的健康问题字典" )
    public MHealthProblemDict createHpDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson) throws Exception {

        HealthProblemDict dict = toEntity(dictJson, HealthProblemDict.class);
        dict.setCreateDate(new Date());
        HealthProblemDict healthProblemDict = hpDictService.createDict(dict);
        return convertToModel(healthProblemDict, MHealthProblemDict.class, null);
    }

     @RequestMapping(value = "dict/hp/{id}", method = RequestMethod.DELETE)
     @ApiOperation(value = "根据id删除健康问题字典（含健康问题字典及与ICD10的关联关系。）")
     public boolean deleteHpDict(
            @ApiParam(name = "id", value = "字典代码")
            @PathVariable( value = "id") long id) {

        List<Icd10HpRelation> icd10HpRelationList = icd10HpRelationService.getHpIcd10RelationListByHpId(id);
        if (icd10HpRelationList != null) {
            for(Icd10HpRelation icd10HpRelation : icd10HpRelationList){
                icd10HpRelationService.delete(icd10HpRelation.getId());
            }
        }
        hpDictService.delete(id);

        return true;
    }

    @RequestMapping(value = "dict/hps", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据ids批量删除健康问题字典（含健康问题字典及与ICD10的关联关系。）")
    public boolean deleteHpDicts(
            @ApiParam(name = "ids", value = "字典代码,多个以逗号隔开")
            @RequestParam( value = "ids") String ids) {

        String[] hpStrIds = ids.split(",");
        Long[] hpIds = new Long[hpStrIds.length];
        List<Long> relationIds = new ArrayList<>();
        for(int i=0; i<hpStrIds.length;i++){
            long hpId = Long.parseLong(hpStrIds[i]);
            hpIds[i] = hpId;
            List<Icd10HpRelation> icd10HpRelationList = icd10HpRelationService.getHpIcd10RelationListByHpId(hpId);
            if (icd10HpRelationList != null) {
                for(Icd10HpRelation icd10HpRelation : icd10HpRelationList){
                    relationIds.add(icd10HpRelation.getId());
                }
            }
        }
        if (relationIds.size() != 0){
            icd10HpRelationService.delete(relationIds);
        }
        hpDictService.delete(hpIds);
        return true;
    }

    @RequestMapping(value = "/dict/hp", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "更新健康问题字典" )
    public MHealthProblemDict updateHpDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson)  throws Exception{

        HealthProblemDict dict = toEntity(dictJson, HealthProblemDict.class);
        if (null == hpDictService.retrieve(dict.getId())) throw new ApiException(ErrorCode.GetDictFaild, "字典不存在");
        dict.setUpdateDate(new Date());
        hpDictService.save(dict);
        return convertToModel(dict, MHealthProblemDict.class);
    }

    @RequestMapping(value = "/dict/hp/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取相应的健康问题字典信息。" )
    public MHealthProblemDict getHpDict(
            @ApiParam(name = "id", value = "字典内码")
            @PathVariable(value = "id") long id) throws Exception {

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

    @RequestMapping(value = "/dict/hp/existence/name" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    public boolean isNameExists(
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name){
        return hpDictService.isNameExist(name);
    }

    @RequestMapping(value = "/dict/hp/existence/code/{code}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典代码是否已经存在")
    public boolean isCodeExists(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code){
        return hpDictService.isCodeExist(code);
    }

    @RequestMapping(value = "/dict/hp/{code}" , method = RequestMethod.GET)
    @ApiOperation(value = "根据字典代码获取健康问题字典")
    public MHealthProblemDict getHpDictByCode(
            @ApiParam(name = "code", value = "code")
            @PathVariable(value = "code") String code){
        HealthProblemDict healthProblemDict = hpDictService.findHpDictByCode(code);
        return convertToModel(healthProblemDict,MHealthProblemDict.class,null);
    }

    @RequestMapping(value = "/dict/hp/{code}/drug" , method = RequestMethod.GET)
    @ApiOperation(value = "根据健康问题代码获取药品信息")
    public Collection<MDrugDict> getDrugDictListByHpCode(
            @ApiParam(name = "code", value = "code")
            @PathVariable(value = "code") String code) throws Exception{
        List<DrugDict> list = hpDictService.getDrugDictListByHpCode(code);
        return convertToModels(list, new ArrayList<>(list.size()), MDrugDict.class,null);
    }

    @RequestMapping(value = "/dict/hp/{code}/indicators" , method = RequestMethod.GET)
    @ApiOperation(value = "根据健康问题代码获取指标信息")
    public Collection<MIndicatorsDict> getIndicatorsByHpCode(
            @ApiParam(name = "code", value = "code")
            @PathVariable(value = "code") String code) throws Exception{
        List<IndicatorsDict> list = hpDictService.getIndicatorsByHpCode(code);
        return convertToModels(list, new ArrayList<>(list.size()), MIndicatorsDict.class,null);
    }

    @RequestMapping(value = "/dict/hp/{code}/icd10" , method = RequestMethod.GET)
    @ApiOperation(value = "根据健康问题代码获取ICD10代码列表")
    public Collection<MIcd10Dict> getIcd10ByHpCode(
            @ApiParam(name = "code", value = "code")
            @PathVariable(value = "code") String code) throws Exception{
        List<Icd10Dict> list = hpDictService.getIcd10ByHpCode(code);
        return convertToModels(list, new ArrayList<>(list.size()), MIcd10Dict.class,null);
    }
}