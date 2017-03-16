package com.yihu.ehr.specialdict.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.controller.EnvelopRestEndPoint;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.specialdict.MDrugDict;
import com.yihu.ehr.specialdict.model.DrugDict;
import com.yihu.ehr.specialdict.service.DrugDictService;
import com.yihu.ehr.specialdict.service.Icd10DrugRelationService;
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
@Api(value = "DrugDict", description = "药品字典管理接口")
public class DrugDictEndPoint extends EnvelopRestEndPoint {

    @Autowired
    private DrugDictService drugDictService;

    @Autowired
    private Icd10DrugRelationService icd10DrugRelationService;

    @RequestMapping(value = "/dict/drug", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "创建新的药品字典" )
    public MDrugDict createDrugDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson) throws Exception {

        DrugDict dict = toEntity(dictJson, DrugDict.class);
        dict.setCreateDate(new Date());
        DrugDict drugDict = drugDictService.createDict(dict);

        return convertToModel(drugDict, MDrugDict.class, null);
    }

    @RequestMapping(value = "、dict/drug/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除药品字典")
    public boolean deleteDrugDict(
            @ApiParam(name = "id", value = "药品字典代码")
            @PathVariable( value = "id") long id) throws Exception {

        drugDictService.delete(id);
        return true;
    }

    @RequestMapping(value = "dict/drugs", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据ids批量删除药品字典")
    public boolean deleteDrugDicts(
            @ApiParam(name = "ids", value = "药品字典代码")
            @RequestParam( value = "ids") String ids) throws Exception {
        String[] strIds = ids.split(",");
        Long[] longIds = new Long[strIds.length];
        for(int i=0; i<strIds.length;i++){
            longIds[i] = Long.parseLong(strIds[i]);
        }
        drugDictService.delete(longIds);
        return true;
    }

    @RequestMapping(value = "/dict/drug", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value = "更新药品字典" )
    public MDrugDict updateDrugDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestBody String dictJson)  throws Exception {

        DrugDict dict = toEntity(dictJson, DrugDict.class);
        if (null == drugDictService.retrieve(dict.getId())) throw new ApiException(ErrorCode.GetDictFaild, "字典不存在");
        dict.setUpdateDate(new Date());
        drugDictService.save(dict);
        return convertToModel(dict, MDrugDict.class);
    }

    @RequestMapping(value = "/dict/drug/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取相应的药品字典信息。" )
    public MDrugDict getDrugDict(
            @ApiParam(name = "id", value = "字典内码")
            @PathVariable(value = "id") long id) throws Exception {

        DrugDict dict = drugDictService.retrieve(id);
        if (dict == null) throw new ApiException(ErrorCode.GetDictFaild, "字典不存在");
        return convertToModel(dict, MDrugDict.class);
    }

    @RequestMapping(value = "/dict/drugs", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询相应的ICD10字典信息。" )
    public Collection<MDrugDict> getDrugDictList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception{
        List<DrugDict> drugDictList = drugDictService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, drugDictService.getCount(filters), page, size);
        return convertToModels(drugDictList, new ArrayList<>(drugDictList.size()), MDrugDict.class, fields);
    }

    @RequestMapping(value = "/dict/drug/icd10/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据drug的ID判断是否与ICD10字典存在关联。")
    public boolean isUsage(
            @ApiParam(name = "id", value = "药品字典代码")
            @PathVariable( value = "id") long id) throws Exception {

        return icd10DrugRelationService.isUsage(id);
    }

    @RequestMapping(value = "/dict/drug/existence/name" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    public boolean isNameExist(
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name){
        return drugDictService.isNameExist(name);
    }

    @RequestMapping(value = "/dict/drug/existence/code/{code}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典代码是否已经存在")
    public boolean isCodeExist(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code){
        return drugDictService.isCodeExist(code);
    }

    @RequestMapping(value = "/drugs/ids", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取相应的药品字典信息。" )
    public List<MDrugDict> getDrugDictByIds(
            @ApiParam(name = "ids", value = "字典内码")
            @RequestParam(value = "ids") String[] ids) throws Exception {
        long[] longIds = new long[ids.length];
        for(int i=0; i<ids.length;i++){
            longIds[i] = Long.parseLong(ids[i]);
        }
        List<DrugDict> drugDictList = drugDictService.getDrugDictByIds(longIds);
        return (List<MDrugDict>)convertToModels(drugDictList, new ArrayList<>(drugDictList.size()), MDrugDict.class, "");
    }
}