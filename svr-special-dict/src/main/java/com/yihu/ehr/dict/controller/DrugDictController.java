package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.dict.service.DrugDict;
import com.yihu.ehr.dict.service.DrugDictService;
import com.yihu.ehr.dict.service.Icd10DrugRelationService;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.specialdict.MDrugDict;
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
@Api(protocols = "https", value = "drugdict", description = "药品字典管理接口", tags = {"Drug"})
public class DrugDictController extends BaseRestController {

    @Autowired
    private DrugDictService drugDictService;
    @Autowired
    private Icd10DrugRelationService icd10DrugRelationService;

    @RequestMapping(value = "/dict/drug", method = RequestMethod.POST)
    @ApiOperation(value = "创建新的药品字典" )
    public MDrugDict createDrugDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) throws Exception {

        DrugDict dict = toEntity(dictJson, DrugDict.class);
        String id = getObjectId(BizObject.Dict);
        dict.setId(id);
        DrugDict drugDict = drugDictService.createDict(dict);
        return convertToModel(drugDict, MDrugDict.class, null);
    }

    @RequestMapping(value = "dict/drug/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除药品字典")
    public boolean deleteDrugDict(
            @ApiParam(name = "id", value = "药品字典代码")
            @PathVariable( value = "id") String id) throws Exception {

        drugDictService.delete(id);
        return true;
    }

    @RequestMapping(value = "/dict/drug/{id}", method = RequestMethod.POST)
    @ApiOperation(value = "更新药品字典" )
    public MDrugDict updateDrugDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson)  throws Exception {

        DrugDict dict = toEntity(dictJson, DrugDict.class);
        if (null == drugDictService.retrieve(dict.getId())) throw new ApiException(ErrorCode.GetDictFaild, "字典不存在");
        drugDictService.save(dict);
        return convertToModel(dict, MDrugDict.class);
    }

    @RequestMapping(value = "/dict/drug/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取相应的药品字典信息。" )
    public MDrugDict getDrugDict(
            @ApiParam(name = "id", value = "字典内码")
            @PathVariable(value = "id") String id) throws Exception {

        DrugDict dict = drugDictService.retrieve(id);
        if (dict == null) throw new ApiException(ErrorCode.GetDictFaild, "字典不存在");
        return convertToModel(dict, MDrugDict.class);
    }

    @RequestMapping(value = "/dict/drugs", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询相应的ICD10字典信息。" )
    public Collection<MDrugDict> getDrugDictList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,code,name,type,flag,tradeName,unit,specifications,description")
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
            Page<DrugDict> drugDictPage = drugDictService.getDictList(sorts, page, size);
            pagedResponse(request, response, drugDictPage.getTotalElements(), page, size);
            return convertToModels(drugDictPage.getContent(), new ArrayList<>(drugDictPage.getNumber()), MDrugDict.class, fields);
        } else {
            List<DrugDict> drugDictList = drugDictService.search(fields, filters, sorts, page, size);
            pagedResponse(request, response, drugDictService.getCount(filters), page, size);
            return convertToModels(drugDictList, new ArrayList<>(drugDictList.size()), MDrugDict.class, fields);
        }
    }

    @RequestMapping(value = "/dict/drug/icd10/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据drug的ID判断是否与ICD10字典存在关联。")
    public boolean isUsage(
            @ApiParam(name = "id", value = "药品字典代码")
            @PathVariable( value = "id") String id) throws Exception {

        return icd10DrugRelationService.isUsage(id);
    }

    @RequestMapping(value = "/dict/drug/{name}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    public boolean isNameExist(
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @PathVariable(value = "name") String name){
        return drugDictService.isNameExist(name);
    }

    @RequestMapping(value = "/dict/drug/{code}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典代码是否已经存在")
    public boolean isCodeExist(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code){
        return drugDictService.isCodeExist(code);
    }
}