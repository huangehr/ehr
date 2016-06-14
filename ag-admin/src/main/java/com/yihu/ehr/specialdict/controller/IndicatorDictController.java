package com.yihu.ehr.specialdict.controller;

import com.yihu.ehr.systemdict.service.ConventionalDictEntryClient;
import com.yihu.ehr.agModel.specialdict.IndicatorsDictModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.specialdict.service.IndicatorDictClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.specialdict.MIndicatorsDict;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
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
        boolean flag = indicatorDictClient.indicatorIsUsage(id);
        if(flag){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("与疾病字典存在关联！请先解除关联。");
            return envelop;
        }
        Boolean bo = indicatorDictClient.deleteIndicatorsDict(id);
        envelop.setSuccessFlg(bo);
        return envelop;
    }

    @RequestMapping(value = "dict/indicators", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id批量删除指标字典")
    public Envelop deleteIndicatorsDicts(
            @ApiParam(name = "ids", value = "字典ID", defaultValue = "")
            @RequestParam(value = "ids") String ids) {
        Envelop envelop = new Envelop();
        String[] indicatorIds = ids.split(",");
        String relaCodes = "";
        for (String indicatorId:indicatorIds){
            boolean flag = indicatorDictClient.indicatorIsUsage(indicatorId);
            if(flag){
                MIndicatorsDict indicatorsDict = indicatorDictClient.getIndicatorsDict(indicatorId);
                relaCodes += indicatorsDict.getCode()+", ";
            }
        }
        if(!StringUtils.isEmpty(relaCodes)){
            relaCodes = relaCodes.substring(0,relaCodes.length()-1);
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("字典："+relaCodes+"与诊断字典存在关联！请先解除关联。");
            return envelop;
        }
        Boolean bo = indicatorDictClient.deleteIndicatorsDicts(ids);
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

        List<IndicatorsDictModel> indicatorsDictModelList = new ArrayList<>();
        ResponseEntity<Collection<MIndicatorsDict>> responseEntity = indicatorDictClient.getIndicatorsDictList(fields, filters, sorts, size, page);
        Collection<MIndicatorsDict> mIndicatorsDicts = responseEntity.getBody();
        Integer totalCount = getTotalCount(responseEntity);
        for (MIndicatorsDict mIndicatorsDict:mIndicatorsDicts){
            IndicatorsDictModel indicatorsDictModel = changeToModel(mIndicatorsDict);
            indicatorsDictModelList.add(indicatorsDictModel);
        }
        Envelop envelop = getResult(indicatorsDictModelList,totalCount,page,size);

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

    @RequestMapping(value = "/dict/indicator/existence/name" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    public Envelop isNameExists(
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name){
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