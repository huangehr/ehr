package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.dict.service.Icd10IndicatorRelationService;
import com.yihu.ehr.dict.service.IndicatorsDict;
import com.yihu.ehr.dict.service.IndicatorsDictService;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.specialdict.MIndicatorsDict;
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
@Api(value = "indicatorsdict", description = "指标字典管理接口", tags = {"Indicators"})
public class IndicatorsDictController extends BaseRestController {

    @Autowired
    private IndicatorsDictService indicatorsDictService;
    @Autowired
    private Icd10IndicatorRelationService icd10IndicatorRelationService;

    @RequestMapping(value = "/dict/indicator", method = RequestMethod.POST)
    @ApiOperation(value = "创建新的指标字典" )
    public MIndicatorsDict createIndicatorsDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) throws Exception {

        IndicatorsDict dict = toEntity(dictJson, IndicatorsDict.class);
        String id = getObjectId(BizObject.Dict);
        dict.setId(id);
        IndicatorsDict indicatorsDict = indicatorsDictService.createDict(dict);
        return convertToModel(indicatorsDict, MIndicatorsDict.class, null);
    }

    @RequestMapping(value = "dict/indicator/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除指标字典")
    public boolean deleteIndicatorsDict(
            @ApiParam(name = "id", value = "指标字典代码")
            @PathVariable( value = "id") String id) {

        indicatorsDictService.delete(id);
        return true;
    }

    @RequestMapping(value = "dict/indicators", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据ids批量删除指标字典")
    public boolean deleteIndicatorsDicts(
            @ApiParam(name = "ids", value = "指标字典代码,多个以逗号分隔")
            @RequestParam( value = "ids") String ids) {

        indicatorsDictService.delete(ids.split(","));
        return true;
    }

    @RequestMapping(value = "/dict/indicator", method = RequestMethod.PUT)
    @ApiOperation(value = "更新指标字典" )
    public MIndicatorsDict updateIndicatorsDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) throws Exception {

        IndicatorsDict dict = toEntity(dictJson, IndicatorsDict.class);
        if (null == indicatorsDictService.retrieve(dict.getId())) throw new ApiException(ErrorCode.GetDictFaild, "字典不存在");
        indicatorsDictService.save(dict);
        return convertToModel(dict, MIndicatorsDict.class);
    }

    @RequestMapping(value = "/dict/indicator/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取相应的指标字典信息。" )
    public MIndicatorsDict getIndicatorsDict(
            @ApiParam(name = "id", value = "字典内码")
            @PathVariable(value = "id") String id) throws Exception {

        IndicatorsDict dict = indicatorsDictService.retrieve(id);
        if (dict == null) throw new ApiException(ErrorCode.GetDictFaild, "字典不存在");
        return convertToModel(dict, MIndicatorsDict.class);
    }

    @RequestMapping(value = "/dict/indicators", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询相应的指标字典信息。" )
    public Collection<MIndicatorsDict> getIndicatorsDictList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,code,name,type,unit,upperLimit,lowerLimit,description")
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
            Page<IndicatorsDict> indicatorsDictPage = indicatorsDictService.getDictList(sorts, page, size);
            pagedResponse(request, response, indicatorsDictPage.getTotalElements(), page, size);
            return convertToModels(indicatorsDictPage.getContent(), new ArrayList<>(indicatorsDictPage.getNumber()), MIndicatorsDict.class, fields);
        } else {
            List<IndicatorsDict> indicatorsDictList = indicatorsDictService.search(fields, filters, sorts, page, size);
            pagedResponse(request, response, indicatorsDictService.getCount(filters), page, size);
            return convertToModels(indicatorsDictList, new ArrayList<>(indicatorsDictList.size()), MIndicatorsDict.class, fields);
        }
    }

    @RequestMapping(value = "dict/indicator/icd10/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据指标的ID判断是否与ICD10字典存在关联。")
    public boolean indicatorIsUsage(
            @ApiParam(name = "id", value = "指标字典代码")
            @PathVariable( value = "id") String id) {

        return icd10IndicatorRelationService.isUsage(id);
    }

    @RequestMapping(value = "/dict/indicator/existence/name" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    public boolean isNameExists(
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name){
        return indicatorsDictService.isNameExist(name);
    }

    @RequestMapping(value = "/dict/indicator/existence/code/{code}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典代码是否已经存在")
    public boolean isCodeExists(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code){
        return indicatorsDictService.isCodeExist(code);
    }
}