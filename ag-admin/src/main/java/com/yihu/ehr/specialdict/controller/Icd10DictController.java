package com.yihu.ehr.specialdict.controller;

import com.yihu.ehr.SystemDict.service.ConventionalDictEntryClient;
import com.yihu.ehr.agModel.specialdict.Icd10DictModel;
import com.yihu.ehr.agModel.specialdict.Icd10DrugRelationModel;
import com.yihu.ehr.agModel.specialdict.Icd10IndicatorRelationModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.specialdict.service.Icd10DictClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.specialdict.MIcd10Dict;
import com.yihu.ehr.model.specialdict.MIcd10DrugRelation;
import com.yihu.ehr.model.specialdict.MIcd10IndicatorRelation;
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
public class Icd10DictController extends BaseController {

    @Autowired
    private Icd10DictClient icd10DictClient;
    @Autowired
    private ConventionalDictEntryClient conDictEntryClient;

    @RequestMapping(value = "/dict/icd10", method = RequestMethod.POST)
    @ApiOperation(value = "创建新的ICD10字典" )
    public Envelop createIcd10Dict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson){

        MIcd10Dict icd10Dict = icd10DictClient.createIcd10Dict(dictJson);
        Icd10DictModel icd10DictModel = changeToModel(icd10Dict);

        Envelop envelop = new Envelop();
        if(icd10DictModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(icd10DictModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("创建字典失败");
        }
        return envelop;
    }

    @RequestMapping(value = "dict/icd10/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除icd10疾病字典(含与药品及指标的关联关系。)")
    public Envelop deleteIcd10Dict(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") String id) {
        Envelop envelop = new Envelop();
        Boolean bo = icd10DictClient.deleteIcd10Dict(id);
        envelop.setSuccessFlg(bo);
        return envelop;
    }

    @RequestMapping(value = "/dict/icd10", method = RequestMethod.PUT)
    @ApiOperation(value = "更新ICD10字典" )
    public Envelop updateIcd10Dict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) {

        MIcd10Dict icd10Dict = icd10DictClient.updateIcd10Dict(dictJson);
        Icd10DictModel icd10DictModel = changeToModel(icd10Dict);

        Envelop envelop = new Envelop();
        if(icd10DictModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(icd10DictModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("修改字典失败");
        }
        return envelop;
    }

    @RequestMapping(value = "/dict/icd10/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取相应的ICD10字典信息。" )
    public Envelop getIcd10Dict(
            @ApiParam(name = "id", value = "icd10字典内码")
            @PathVariable(value = "id") String id){

        MIcd10Dict icd10Dict = icd10DictClient.getIcd10Dict(id);
        Icd10DictModel icd10DictModel = changeToModel(icd10Dict);

        Envelop envelop = new Envelop();
        if(icd10DictModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(icd10DictModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取字典失败");
        }
        return envelop;
    }

    @RequestMapping(value = "/dict/icd10s", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询相应的ICD10字典信息。" )
    public Envelop getIcd10DictList(
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

        List<MIcd10Dict> icd10DictList =(List<MIcd10Dict>)icd10DictClient.getIcd10DictList(fields, filters, sorts, size, page);
        List<Icd10DictModel> icd10DictModelList = new ArrayList<>();

        for (MIcd10Dict mIcd10Dict:icd10DictList){
            Icd10DictModel icd10DictModel= changeToModel(mIcd10Dict);
            icd10DictModelList.add(icd10DictModel);
        }
        Envelop envelop = getResult(icd10DictModelList,0,page,size);

        return envelop;
    }

    @RequestMapping(value = "dict/icd10/hp/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10的ID判断是否与健康问题存在关联。")
    public Envelop isUsage(
            @ApiParam(name = "id", value = "药品字典ID", defaultValue = "")
            @PathVariable(value = "id") String id){

        Envelop envelop = new Envelop();
        boolean result = icd10DictClient.icd10DictIsUsage(id);
        envelop.setSuccessFlg(result);

        return envelop;
    }

    @RequestMapping(value = "/dict/icd10/existence/code/{code}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典代码是否已经存在")
    public Envelop isCodeExist(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code){

        Envelop envelop = new Envelop();
        boolean result = icd10DictClient.isCodeExists(code);
        envelop.setSuccessFlg(result);

        return envelop;
    }

    @RequestMapping(value = "/dict/icd10/existence/name/{name}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    public Envelop isNameExist(
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @PathVariable(value = "name") String name){

        Envelop envelop = new Envelop();
        boolean result = icd10DictClient.isNameExists(name);
        envelop.setSuccessFlg(result);

        return envelop;
    }

    //-------------------------ICD10与药品之间关联关系管理-----------------------------------------------------------

    @RequestMapping(value = "/dict/icd10/drug", method = RequestMethod.POST)
    @ApiOperation(value = "为ICD10增加药品关联。" )
    public Envelop createIcd10DrugRelation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson){

        MIcd10DrugRelation icd10DrugRelation = icd10DictClient.createIcd10DrugRelation(dictJson);
        Icd10DrugRelationModel icd10DrugRelationModel = convertToModel(icd10DrugRelation,Icd10DrugRelationModel.class);

        Envelop envelop = new Envelop();
        if(icd10DrugRelationModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(icd10DrugRelationModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("创建字典关联失败");
        }
        return envelop;
    }

    @RequestMapping(value = "/dict/icd10/drug", method = RequestMethod.PUT)
    @ApiOperation(value = "为ICD10修改药品关联。" )
    public Envelop updateIcd10DrugRelation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson){

        MIcd10DrugRelation icd10DrugRelation = icd10DictClient.updateIcd10DrugRelation(dictJson);
        Icd10DrugRelationModel icd10DrugRelationModel = convertToModel(icd10DrugRelation,Icd10DrugRelationModel.class);

        Envelop envelop = new Envelop();
        if(icd10DrugRelationModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(icd10DrugRelationModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("创建字典关联失败");
        }
        return envelop;
    }

    @RequestMapping(value = "/dict/icd10/drug", method = RequestMethod.DELETE)
    @ApiOperation(value = "为ICD10删除药品关联。" )
    public Envelop deleteIcd10DrugRelation(
            @ApiParam(name = "id", value = "关联ID", defaultValue = "")
            @RequestParam(value = "id", required = true) String id){

        Envelop envelop = new Envelop();
        boolean bo = icd10DictClient.deleteIcd10DrugRelation(id);
        envelop.setSuccessFlg(bo);
        return envelop;
    }

    @RequestMapping(value = "/dict/icd10/drugs", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10查询相应的药品关联列表信息。" )
    public Envelop getIcd10DrugRelationList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,hpId,icd10Id")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+hpId")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page){

        List<MIcd10DrugRelation> icd10DrugRelationList =(List<MIcd10DrugRelation>)icd10DictClient.getIcd10DrugRelationList(fields, filters, sorts, size, page);
        List<Icd10DrugRelationModel> icd10DrugRelationModelList = new ArrayList<>();

        for (MIcd10DrugRelation mIcd10DrugRelation:icd10DrugRelationList){
            Icd10DrugRelationModel icd10DrugRelationModel = convertToModel(mIcd10DrugRelation,Icd10DrugRelationModel.class);
            icd10DrugRelationModelList.add(icd10DrugRelationModel);
        }
        Envelop envelop = getResult(icd10DrugRelationModelList,0,page,size);

        return envelop;
    }

    @RequestMapping(value = "/dict/icd10/drug/existence" , method = RequestMethod.GET)
    @ApiOperation(value = "判断ICD10与药品字典的关联关系在系统中是否已存在")
    public Envelop isIcd10DrugRelaExist(
            @ApiParam(name = "drugId", value = "药品字典内码")
            @RequestParam(value = "drugId", required = false) String drugId,
            @ApiParam(name = "icd10Id", value = "Icd10内码", defaultValue = "")
            @RequestParam(value = "icd10Id", required = false) String icd10Id){

        Envelop envelop = new Envelop();
        boolean result = icd10DictClient.isIcd10DrugRelaExist(drugId, icd10Id);
        envelop.setSuccessFlg(result);

        return envelop;
    }

    //-------------------------ICD10与指标之间关联关系管理-----------------------------------------------------------

    @RequestMapping(value = "/dict/icd10/indicator", method = RequestMethod.POST)
    @ApiOperation(value = "为ICD10增加指标关联。" )
    public Envelop createIcd10IndicatorRelation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson){

        MIcd10IndicatorRelation icd10IndicatorRelation = icd10DictClient.createIcd10IndicatorRelation(dictJson);
        Icd10IndicatorRelationModel icd10IndicatorRelationModel = convertToModel(icd10IndicatorRelation,Icd10IndicatorRelationModel.class);

        Envelop envelop = new Envelop();
        if(icd10IndicatorRelationModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(icd10IndicatorRelationModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("创建字典关联失败");
        }
        return envelop;
    }

    @RequestMapping(value = "/dict/icd10/indicator", method = RequestMethod.PUT)
    @ApiOperation(value = "为ICD10修改指标关联。" )
    public Envelop updateIcd10IndicatorRelation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson){

        MIcd10IndicatorRelation icd10IndicatorRelation = icd10DictClient.updateIcd10IndicatorRelation(dictJson);
        Icd10IndicatorRelationModel icd10IndicatorRelationModel = convertToModel(icd10IndicatorRelation,Icd10IndicatorRelationModel.class);

        Envelop envelop = new Envelop();
        if(icd10IndicatorRelationModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(icd10IndicatorRelationModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("创建字典关联失败");
        }
        return envelop;
    }

    @RequestMapping(value = "/dict/icd10/indicator", method = RequestMethod.DELETE)
    @ApiOperation(value = "为ICD10删除指标关联。" )
    public Envelop deleteIcd10IndicatorRelation(
            @ApiParam(name = "id", value = "关联ID", defaultValue = "")
            @RequestParam(value = "id", required = true) String id){

        Envelop envelop = new Envelop();
        boolean bo = icd10DictClient.deleteIcd10IndicatorRelation(id);
        envelop.setSuccessFlg(bo);
        return envelop;
    }

    @RequestMapping(value = "/dict/icd10/indicators", method = RequestMethod.GET)
    @ApiOperation(value = "根据ICD10查询相应的指标关联列表信息。" )
    public Envelop getIcd10IndicatorRelationList(
            @ApiParam(name = "fields", value = "返回的字段，为空返回全部字段", defaultValue = "id,hpId,icd10Id")
            @RequestParam(value = "fields", required = false) String fields,
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters,
            @ApiParam(name = "sorts", value = "排序，规则参见说明文档", defaultValue = "+hpId")
            @RequestParam(value = "sorts", required = false) String sorts,
            @ApiParam(name = "size", value = "分页大小", defaultValue = "15")
            @RequestParam(value = "size", required = false) int size,
            @ApiParam(name = "page", value = "页码", defaultValue = "1")
            @RequestParam(value = "page", required = false) int page){

        List<MIcd10IndicatorRelation> mIcd10IndicatorRelationList =(List<MIcd10IndicatorRelation>)icd10DictClient.getIcd10IndicatorRelationList(fields, filters, sorts, size, page);
        List<Icd10IndicatorRelationModel> icd10IndicatorRelationModelList = new ArrayList<>();

        for (MIcd10IndicatorRelation mIcd10IndicatorRelation:mIcd10IndicatorRelationList){
            Icd10IndicatorRelationModel icd10DrugRelationModel = convertToModel(mIcd10IndicatorRelation,Icd10IndicatorRelationModel.class);
            icd10IndicatorRelationModelList.add(icd10DrugRelationModel);
        }
        Envelop envelop = getResult(icd10IndicatorRelationModelList,0,page,size);

        return envelop;
    }

    @RequestMapping(value = "/dict/icd10/indicator/existence" , method = RequestMethod.GET)
    @ApiOperation(value = "判断ICD10与指标字典的关联关系在系统中是否已存在")
    public Envelop isIcd10IndicatorsRelaExist(
            @ApiParam(name = "indicatorsId", value = "药品字典内码")
            @RequestParam(value = "indicatorsId", required = false) String indicatorsId,
            @ApiParam(name = "icd10Id", value = "Icd10内码", defaultValue = "")
            @RequestParam(value = "icd10Id", required = false) String icd10Id){

        Envelop envelop = new Envelop();
        boolean result = icd10DictClient.isIcd10IndicatorsRelaExist(indicatorsId, icd10Id);
        envelop.setSuccessFlg(result);

        return envelop;
    }

    private Icd10DictModel changeToModel(MIcd10Dict mIcd10Dict) {
        Icd10DictModel icd10DictModel = convertToModel(mIcd10Dict, Icd10DictModel.class);
        //获取字典标识（慢病标识）值
        MConventionalDict flag = conDictEntryClient.getYesNo(mIcd10Dict.getChronicFlag()=="0"? false:true);
        icd10DictModel.setChronicFlag(flag == null ? "" : flag.getValue());
        //获取字典标识（传染病标识）值
        MConventionalDict type = conDictEntryClient.getYesNo(mIcd10Dict.getInfectiousFlag() == "0" ? false : true);
        icd10DictModel.setInfectiousFlag(type == null ? "" : type.getValue());

        return icd10DictModel;
    }
}