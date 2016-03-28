package com.yihu.ehr.ha.specialdict.controller;

import com.yihu.ehr.agModel.specialdict.IndicatorsDictModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.ha.SystemDict.service.ConventionalDictEntryClient;
import com.yihu.ehr.ha.specialdict.service.IndicatorDictClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.specialdict.MIndicatorsDict;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@Api(value = "DrugDict", description = "药品字典管理", tags = {"药品字典"})
public class IndicatorDictController extends BaseController {

    @Autowired
    private ConventionalDictEntryClient conDictEntryClient;
    @Autowired
    private IndicatorDictClient indicatorDictClient;

    @ApiOperation(value = "创建字典", produces = "application/json")
    @RequestMapping(value = "/dict/indicator", method = RequestMethod.POST)
    public Envelop createIndicatorsDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson){

        MIndicatorsDict indicatorsDict = indicatorDictClient.createIndicatorsDict(dictJson);
        IndicatorsDictModel indicatorsDictModel = changeToModel(indicatorsDict);

        Envelop envelop = new Envelop();
        if(indicatorsDictModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(indicatorsDictModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("创建字典失败");
        }
        return envelop;
    }

    @RequestMapping(value = "dict/indicator/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除指标字典")
    public Envelop deleteIndicatorsDict(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") String id) {
        Envelop envelop = new Envelop();
        Boolean bo = indicatorDictClient.deleteIndicatorsDict(id);
        envelop.setSuccessFlg(bo);
        return envelop;
    }

    @RequestMapping(value = "/dict/indicator", method = RequestMethod.PUT)
    @ApiOperation(value = "更新指标字典" )
    public Envelop updateIndicatorsDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) {

        MIndicatorsDict indicatorsDict = indicatorDictClient.updateIndicatorsDict(dictJson);
        IndicatorsDictModel indicatorsDictModel = changeToModel(indicatorsDict);

        Envelop envelop = new Envelop();
        if(indicatorsDictModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(indicatorsDictModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("修改字典失败");
        }
        return envelop;
    }

    @RequestMapping(value = "/dict/indicator/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取相应的指标字典信息。" )
    public Envelop getIndicatorsDict(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") String id){

        MIndicatorsDict indicatorsDict = indicatorDictClient.getIndicatorsDict(id);
        IndicatorsDictModel indicatorsDictModel =changeToModel(indicatorsDict);

        Envelop envelop = new Envelop();
        if(indicatorsDictModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(indicatorsDictModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取字典失败");
        }
        return envelop;
    }

    @RequestMapping(value = "/dict/indicators", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询相应的指标字典信息。" )
    public Envelop getIndicatorsDictList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序", defaultValue = "")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) Integer size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) Integer page){

        List<MIndicatorsDict> indicatorsDictList =(List<MIndicatorsDict>)indicatorDictClient.getIndicatorsDictList(fields, filters, sorts, size, page);
        List<IndicatorsDictModel> indicatorsDictModelList = new ArrayList<>();

        for (MIndicatorsDict mIndicatorsDict:indicatorsDictList){
            IndicatorsDictModel indicatorsDictModel = changeToModel(mIndicatorsDict);
            indicatorsDictModelList.add(indicatorsDictModel);
        }
        Envelop envelop = getResult(indicatorsDictModelList,0,page,size);

        return envelop;
    }

        @RequestMapping(value = "dict/indicator/icd10/{id}", method = RequestMethod.GET)
        @ApiOperation(value = "根据指标的ID判断是否与ICD10字典存在关联。")
        public Envelop indicatorIsUsage(
                @ApiParam(name = "id", value = "指标字典代码")
                @PathVariable( value = "id") String id){

            Envelop envelop = new Envelop();
            boolean result = indicatorDictClient.indicatorIsUsage(id);
            envelop.setSuccessFlg(result);

        return envelop;
    }

    @RequestMapping(value = "/dict/indicator/existence/name/{name}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    public Envelop isNameExists(
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @PathVariable(value = "name") String name){
        Envelop envelop = new Envelop();
        boolean result = indicatorDictClient.isNameExists(name);
        envelop.setSuccessFlg(result);

        return envelop;
    }

    @RequestMapping(value = "/dict/indicator/existence/code/{code}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典代码是否已经存在")
    public Envelop isCodeExists(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code){
        Envelop envelop = new Envelop();
        boolean result = indicatorDictClient.isCodeExists(code);
        envelop.setSuccessFlg(result);

        return envelop;
    }

    /**
     *  将微服务返回的MDrugDict转化为前端DrugDictModel
     * @param mIndicatorsDict
     * @return IndicatorsDictModel
     */
    private IndicatorsDictModel changeToModel(MIndicatorsDict mIndicatorsDict) {
        IndicatorsDictModel indicatorsDictModel = convertToModel(mIndicatorsDict, IndicatorsDictModel.class);
        //获取字典类别值
        MConventionalDict type = conDictEntryClient.getIndicatorType(mIndicatorsDict.getType());
        indicatorsDictModel.setTypeName(type == null ? "" : type.getValue());
        return indicatorsDictModel;
    }
}