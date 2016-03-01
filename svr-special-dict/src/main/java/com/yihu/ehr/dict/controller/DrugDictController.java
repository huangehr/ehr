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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
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
    public Object createDrugDict(
            @ApiParam(name = "code", value = "字典编码")
            @RequestParam(value = "code", required = true) String code,
            @ApiParam(name = "name", value = "字典名称")
            @RequestParam(value = "name", required = true) String name,
            @ApiParam(name = "type", value = "药品类别", defaultValue = "0")
            @RequestParam(value = "type", required = true) String type,
            @ApiParam(name = "flag", value = "处方/非处方标识", defaultValue = "0")
            @RequestParam(value = "flag", required = true) String flag,
            @ApiParam(name = "tradeName", value = "商品名")
            @RequestParam(value = "tradeName", required = false) String tradeName,
            @ApiParam(name = "unit", value = "单位")
            @RequestParam(value = "unit", required = false) String unit,
            @ApiParam(name = "specifications", value = "规格")
            @RequestParam(value = "specifications", required = false) String specifications,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description", required = false) String description) throws Exception {

        DrugDict drugDict =  new DrugDict();
        String id = getObjectId(BizObject.Dict);
        if(drugDictService.isCodeExist(id,code,"0")){
            throw new ApiException(ErrorCode.RepeatCode, "代码在系统中已存在，请确认。");
        }
        if(drugDictService.isNameExist(id,name,"0")){
            throw new ApiException(ErrorCode.RepeatName, "名称在系统中已存在，请确认。");
        }
        drugDict.setId(id);
        drugDict.setCode(code);
        drugDict.setName(name);
        drugDict.setType(type);
        drugDict.setFlag(flag);
        drugDict.setTradeName(tradeName);
        drugDict.setUnit(unit);
        drugDict.setSpecifications(specifications);
        drugDict.setDescription(description);

        drugDictService.save(drugDict);

        return true;
    }

    @RequestMapping(value = "dict/drug/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除药品字典")
    public boolean deleteDrugDict(
            @ApiParam(name = "id", value = "药品字典代码")
            @PathVariable( value = "id") String id) {

        drugDictService.delete(id);

        return true;
    }

    @RequestMapping(value = "/dict/drug/{id}", method = RequestMethod.POST)
    @ApiOperation(value = "更新药品字典" )
    public Object updateDrugDict(
            @ApiParam(name = "id", value = "字典内码")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "code", value = "字典编码")
            @RequestParam(value = "code", required = true) String code,
            @ApiParam(name = "name", value = "字典名称")
            @RequestParam(value = "name", required = true) String name,
            @ApiParam(name = "type", value = "药品类别", defaultValue = "0")
            @RequestParam(value = "type", required = true) String type,
            @ApiParam(name = "flag", value = "处方/非处方标识", defaultValue = "0")
            @RequestParam(value = "flag", required = true) String flag,
            @ApiParam(name = "tradeName", value = "商品名")
            @RequestParam(value = "tradeName", required = false) String tradeName,
            @ApiParam(name = "unit", value = "单位")
            @RequestParam(value = "unit", required = false) String unit,
            @ApiParam(name = "specifications", value = "规格")
            @RequestParam(value = "specifications", required = false) String specifications,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description", required = false) String description) throws Exception {

        DrugDict drugDict = drugDictService.retrieve(id);
        if(drugDictService.isCodeExist(id, code, "1")){
            throw new ApiException(ErrorCode.RepeatCode, "代码在系统中已存在，请确认。");
        }
        if(drugDictService.isNameExist(id, name, "1")){
            throw new ApiException(ErrorCode.RepeatName, "名称在系统中已存在，请确认。");
        }
        drugDict.setCode(code);
        drugDict.setName(name);
        drugDict.setType(type);
        drugDict.setFlag(flag);
        drugDict.setTradeName(tradeName);
        drugDict.setUnit(unit);
        drugDict.setSpecifications(specifications);
        drugDict.setDescription(description);

        drugDictService.save(drugDict);

        return true;
    }

    @RequestMapping(value = "/dict/drug/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取相应的药品字典信息。" )
    public Object getDrugDict(
            @ApiParam(name = "id", value = "字典内码")
            @PathVariable(value = "id") String id) throws Exception {

        DrugDict drugDict = drugDictService.retrieve(id);
        if(drugDict != null){
            return convertToModel(drugDict, MDrugDict.class);
        }else{
            throw new ApiException(ErrorCode.QueryNoData," 查询无数据，请确认。");
        }
    }

    @RequestMapping(value = "/dict/drugs", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询相应的ICD10字典信息。" )
    public Object getDrugDictList(
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

        List<DrugDict> drugDictList = drugDictService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, drugDictService.getCount(filters), page, size);

        return (List<MDrugDict>)convertToModels(drugDictList,
                new ArrayList<MDrugDict>(drugDictList.size()), MDrugDict.class, fields);

    }

    @RequestMapping(value = "dict/drug/icd10/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据drug的ID判断是否与ICD10字典存在关联。")
    public boolean drugIsUsage(
            @ApiParam(name = "id", value = "药品字典代码")
            @PathVariable( value = "id") String id) {

        return icd10DrugRelationService.isUsage(id);
    }
}