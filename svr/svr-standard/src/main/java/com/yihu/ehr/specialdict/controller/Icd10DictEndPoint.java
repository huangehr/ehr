package com.yihu.ehr.specialdict.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.specialdict.MIcd10Dict;
import com.yihu.ehr.specialdict.model.Icd10Dict;
import com.yihu.ehr.specialdict.model.Icd10DrugRelation;
import com.yihu.ehr.specialdict.model.Icd10IndicatorRelation;
import com.yihu.ehr.specialdict.service.Icd10DictService;
import com.yihu.ehr.specialdict.service.Icd10DrugRelationService;
import com.yihu.ehr.specialdict.service.Icd10HpRelationService;
import com.yihu.ehr.specialdict.service.Icd10IndicatorRelationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
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
@Api(value = "Icd10DictEndPoint", description = "ICD10字典", tags = {"特殊字典-ICD10字典"})
public class Icd10DictEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private Icd10DictService icd10DictService;

    @Autowired
    private Icd10HpRelationService icd10HpRelationService;

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
        Icd10Dict  icd10Dict= icd10DictService.createDict(dict);
        return convertToModel(icd10Dict, MIcd10Dict.class, null);
    }

    @RequestMapping(value = "/dict/icd10/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除icd10疾病字典(含与药品及指标的关联关系，同时删除关联的诊断。)")
    public boolean deleteIcd10Dict(
            @ApiParam(name = "id", value = "icd10字典代码")
            @PathVariable( value = "id") long id) {

        long drugRelationId;
        List<Icd10DrugRelation> icd10DrugRelations = icd10DrugRelationService.getIcd10DrugRelationListByIcd10Id(id);
        if (icd10DrugRelations != null) {
            for(Icd10DrugRelation icd10DrugRelation : icd10DrugRelations ){
                drugRelationId = icd10DrugRelation.getId();
                icd10DrugRelationService.delete(drugRelationId);
            }
        }
        long indicationRelationId;
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

    @RequestMapping(value = "/dict/icd10s", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据ids批量删除删除icd10疾病字典(含与药品及指标的关联关系，同时删除关联的诊断。)")
    public boolean deleteIcd10Dicts(
            @ApiParam(name = "ids", value = "icd10字典代码,多个以逗号隔开")
            @RequestParam( value = "ids") String ids) {
        String[] strIds = ids.split(",");
        Long[] longIds = new Long[strIds.length];
        List<Long> drugRelationIds = new ArrayList<>();
        List<Long> indicationRelationIds = new ArrayList<>();
        for(int i =0;i<strIds.length;i++){
            long icd10Id = Long.parseLong(strIds[i]);
            longIds[i] = icd10Id;
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
        icd10DictService.delete(longIds);
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
            @PathVariable(value = "id") long id) throws Exception {

        Icd10Dict dict = icd10DictService.retrieve(id);
        if (dict == null) throw new ApiException(ErrorCode.GetDictFaild, "字典不存在");
        return convertToModel(dict, MIcd10Dict.class);
    }

    @RequestMapping(value = "/dict/icd10s", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询相应的ICD10字典信息。" )
    public Collection<MIcd10Dict> getIcd10DictList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
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
        return convertToModels(icd10DictList, new ArrayList<>(icd10DictList.size()), MIcd10Dict.class, fields);
    }

    @RequestMapping(value = "/dict/icd10/hp/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10的ID判断是否与健康问题存在关联。")
    public boolean icd10DictIsUsage(
            @ApiParam(name = "id", value = "icd10字典代码")
            @PathVariable( value = "id") long id) {

        return icd10HpRelationService.isUsage(id);
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

    @RequestMapping(value = "/dict/icd10/code/{code}" , method = RequestMethod.GET)
    @ApiOperation(value = "根据code获取ICD10字典信息")
    public MIcd10Dict findByCode(@ApiParam(name = "code", value = "icd10_code")
                                     @PathVariable(value = "code") String code) throws Exception{
        Icd10Dict dict = icd10DictService.findByCode(code);
        if (dict == null) return null;
        return convertToModel(dict, MIcd10Dict.class);
    }



    @RequestMapping(value = "/dict/icd10/codeList" , method = RequestMethod.POST)
    @ApiOperation(value = "根据codeList获取ICD10字典信息")
    public List<MIcd10Dict> findByCodeList(@ApiParam(name = "codeList", value = "icd10_code_list")
                                 @RequestParam(value = "codeList") List<String> codeList) throws Exception{
        List<MIcd10Dict>list=new ArrayList<>();
        for(int i=0;i<codeList.size();i++) {
            Icd10Dict dict = icd10DictService.findByCode(codeList.get(i));
            if (dict == null)
                list.add(null);
            else
                list.add(convertToModel(dict, MIcd10Dict.class));
        }
        return list;
    }

    @RequestMapping(value = "/dict/icd10/ids" , method = RequestMethod.GET)
    @ApiOperation(value = "根据id列表获取ICD10字典信息")
    public List<MIcd10Dict> getIcd10DictListByIds(
            @ApiParam(name = "ids", value = "id列表")
            @RequestParam(value = "ids") String[] ids) throws Exception {
        long[] longIds = new long[ids.length];
        for(int i=0; i<ids.length;i++){
            longIds[i] = Long.parseLong(ids[i]);
        }
        List<Icd10Dict> icd10DictList = icd10DictService.findByIds(longIds);
        return (List<MIcd10Dict>)convertToModels(icd10DictList, new ArrayList<>(icd10DictList.size()), MIcd10Dict.class, "");
    }

}