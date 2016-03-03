package com.yihu.ehr.dict.controller;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.BizObject;
import com.yihu.ehr.constants.ErrorCode;
import com.yihu.ehr.dict.service.Icd10IndicatorRelationService;
import com.yihu.ehr.dict.service.IndicatorsDict;
import com.yihu.ehr.dict.service.IndicatorsDictService;
import com.yihu.ehr.exception.ApiException;
import com.yihu.ehr.model.specialdict.MDrugDict;
import com.yihu.ehr.model.specialdict.MIndicatorsDict;
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
@Api(protocols = "https", value = "indicatorsdict", description = "指标字典管理接口", tags = {"Indicators"})
public class IndicatorsDictController extends BaseRestController {

    @Autowired
    private IndicatorsDictService indicatorsDictService;
    @Autowired
    private Icd10IndicatorRelationService icd10IndicatorRelationService;

    @RequestMapping(value = "/dict/indicator", method = RequestMethod.POST)
    @ApiOperation(value = "创建新的指标字典" )
    public Object createIndicatorsDict(
            @ApiParam(name = "code", value = "字典编码")
            @RequestParam(value = "code", required = true) String code,
            @ApiParam(name = "name", value = "字典名称")
            @RequestParam(value = "name", required = true) String name,
            @ApiParam(name = "type", value = "指标类别", defaultValue = "0")
            @RequestParam(value = "type", required = true) String type,
            @ApiParam(name = "unit", value = "单位")
            @RequestParam(value = "unit", required = false) String unit,
            @ApiParam(name = "upperLimit", value = "标准值上限")
            @RequestParam(value = "upperLimit", required = false) String upperLimit,
            @ApiParam(name = "lowerLimit", value = "标准值下限")
            @RequestParam(value = "lowerLimit", required = false) String lowerLimit,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description", required = false) String description) throws Exception {

        IndicatorsDict indicatorsDict =  new IndicatorsDict();
        String id = getObjectId(BizObject.Dict);
        if(indicatorsDictService.isCodeExist(id,code,"0")){
            throw new ApiException(ErrorCode.RepeatCode, "代码在系统中已存在，请确认。");
        }
        if(indicatorsDictService.isNameExist(id,name,"0")){
            throw new ApiException(ErrorCode.RepeatName, "名称在系统中已存在，请确认。");
        }
        indicatorsDict.setId(id);
        indicatorsDict.setCode(code);
        indicatorsDict.setName(name);
        indicatorsDict.setType(type);
        indicatorsDict.setUnit(unit);
        indicatorsDict.setUpperLimit(upperLimit);
        indicatorsDict.setLowerLimit(lowerLimit);
        indicatorsDict.setDescription(description);

        indicatorsDictService.save(indicatorsDict);

        return true;
    }

    @RequestMapping(value = "dict/indicator/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除指标字典")
    public boolean deleteIndicatorsDict(
            @ApiParam(name = "id", value = "指标字典代码")
            @PathVariable( value = "id") String id) {

        indicatorsDictService.delete(id);

        return true;
    }

    @RequestMapping(value = "/dict/indicator/{id}", method = RequestMethod.POST)
    @ApiOperation(value = "更新指标字典" )
    public Object updateIndicatorsDict(
            @ApiParam(name = "id", value = "字典内码")
            @PathVariable(value = "id") String id,
            @ApiParam(name = "code", value = "字典编码")
            @RequestParam(value = "code", required = true) String code,
            @ApiParam(name = "name", value = "字典名称")
            @RequestParam(value = "name", required = true) String name,
            @ApiParam(name = "type", value = "指标类别", defaultValue = "0")
            @RequestParam(value = "type", required = true) String type,
            @ApiParam(name = "unit", value = "单位")
            @RequestParam(value = "unit", required = false) String unit,
            @ApiParam(name = "upperLimit", value = "标准值上限")
            @RequestParam(value = "upperLimit", required = false) String upperLimit,
            @ApiParam(name = "lowerLimit", value = "标准值下限")
            @RequestParam(value = "lowerLimit", required = false) String lowerLimit,
            @ApiParam(name = "description", value = "描述", defaultValue = "")
            @RequestParam(value = "description", required = false) String description) throws Exception {

        IndicatorsDict indicatorsDict = indicatorsDictService.retrieve(id);
        if(indicatorsDictService.isCodeExist(id, code, "1")){
            throw new ApiException(ErrorCode.RepeatCode, "代码在系统中已存在，请确认。");
        }
        if(indicatorsDictService.isNameExist(id, name, "1")){
            throw new ApiException(ErrorCode.RepeatName, "名称在系统中已存在，请确认。");
        }
        indicatorsDict.setCode(code);
        indicatorsDict.setName(name);
        indicatorsDict.setType(type);
        indicatorsDict.setUnit(unit);
        indicatorsDict.setUpperLimit(upperLimit);
        indicatorsDict.setLowerLimit(lowerLimit);
        indicatorsDict.setDescription(description);

        indicatorsDictService.save(indicatorsDict);

        return true;
    }

    @RequestMapping(value = "/dict/indicator/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取相应的指标字典信息。" )
    public Object getIndicatorsDict(
            @ApiParam(name = "id", value = "字典内码")
            @PathVariable(value = "id") String id) throws Exception {

        IndicatorsDict indicatorsDict = indicatorsDictService.retrieve(id);
        if(indicatorsDict != null){
            return convertToModel(indicatorsDict, MIndicatorsDict.class);
        }else{
            throw new ApiException(ErrorCode.QueryNoData," 查询无数据，请确认。");
        }
    }

    @RequestMapping(value = "/dict/indicators", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询相应的指标字典信息。" )
    public Object getIndicatorsDictList(
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

        List<IndicatorsDict> indicatorsDictList= indicatorsDictService.search(fields, filters, sorts, page, size);
        pagedResponse(request, response, indicatorsDictService.getCount(filters), page, size);

        return (List<MIndicatorsDict>)convertToModels(indicatorsDictList,
                new ArrayList<MIndicatorsDict>(indicatorsDictList.size()), MIndicatorsDict.class, fields);
    }

    @RequestMapping(value = "dict/indicator/icd10/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据指标的ID判断是否与ICD10字典存在关联。")
    public boolean indicatorIsUsage(
            @ApiParam(name = "id", value = "指标字典代码")
            @PathVariable( value = "id") String id) {

        return icd10IndicatorRelationService.isUsage(id);
    }
}