package com.yihu.ehr.specialdict.controller;

import com.yihu.ehr.systemDict.service.ConventionalDictEntryClient;
import com.yihu.ehr.agModel.specialdict.DrugDictModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.specialdict.service.DrugDictClient;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.specialdict.MDrugDict;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
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
public class DrugDictController extends BaseController {

    @Autowired
    private ConventionalDictEntryClient conDictEntryClient;
    @Autowired
    private DrugDictClient drugDictClient;

    @ApiOperation(value = "创建字典", produces = "application/json")
    @RequestMapping(value = "/dict/drug", method = RequestMethod.POST)
    public Envelop createDrugDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson){

        MDrugDict drugDict = drugDictClient.createDrugDict(dictJson);
        DrugDictModel drugDictModel = changeToModel(drugDict);

        Envelop envelop = new Envelop();
        if(drugDictModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(drugDictModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("创建字典失败");
        }
        return envelop;
    }

    @ApiOperation(value = "删除字典")
    @RequestMapping(value = "/dict/drug/{id}", method = RequestMethod.DELETE)
    public Envelop deleteDrugDict(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") String id) {
        Envelop envelop = new Envelop();
        boolean flag = drugDictClient.isUsage(id);
        if(flag){
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("与疾病字典存在关联！请先解除关联。");
            return envelop;
        }
        Boolean bo = drugDictClient.deleteDrugDict(id);
        envelop.setSuccessFlg(bo);
        return envelop;
    }

    @ApiOperation(value = "批量删除字典")
    @RequestMapping(value = "/dict/drugs", method = RequestMethod.DELETE)
    public Envelop deleteDrugDicts(
            @ApiParam(name = "ids", value = "字典IDs", defaultValue = "")
            @RequestParam(value = "ids") String ids) {

        Envelop envelop = new Envelop();
        String[] drugIds = ids.split(",");
        String relaCodes = "";
        for(String drugId:drugIds){
            boolean flag = drugDictClient.isUsage(drugId);
            if(flag){
                MDrugDict drugDict = drugDictClient.getDrugDict(drugId);
                relaCodes += drugDict.getCode()+", ";
            }
        }
        if(!StringUtils.isEmpty(relaCodes)){
            relaCodes = relaCodes.substring(0,relaCodes.length()-1);
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("字典："+relaCodes+"与疾病字典存在关联！请先解除关联。");
            return envelop;
        }
        Boolean bo = drugDictClient.deleteDrugDicts(ids);
        envelop.setSuccessFlg(bo);
        return envelop;
    }

    @ApiOperation(value = "修改字典")
    @RequestMapping(value = "/dict/drug", method = RequestMethod.PUT)
    public Envelop updateDrugDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) {

        MDrugDict drugDict = drugDictClient.updateDrugDict(dictJson);
        DrugDictModel drugDictModel = changeToModel(drugDict);

        Envelop envelop = new Envelop();
        if(drugDictModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(drugDictModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("修改字典失败");
        }
        return envelop;
    }

    @ApiOperation(value = "获取字典", produces = "application/json")
    @RequestMapping(value = "/dict/drug/{id}", method = RequestMethod.GET)
    public Envelop getDrugDict(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") String id){

        MDrugDict drugDict = drugDictClient.getDrugDict(id);
        DrugDictModel drugDictModel = changeToModel(drugDict);

        Envelop envelop = new Envelop();
        if(drugDictModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(drugDictModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取字典失败");
        }
        return envelop;
    }

    @ApiOperation(value = "获取字典列表")
    @RequestMapping(value = "/dict/drugs", method = RequestMethod.GET)
    public Envelop getDrugDictList(
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

        List<DrugDictModel> drugDictModelList = new ArrayList<>();
        ResponseEntity<Collection<MDrugDict>> responseEntity = drugDictClient.getDrugDictList(fields, filters, sorts, size, page);
        Collection<MDrugDict> mDrugDicts  = responseEntity.getBody();
        for (MDrugDict mDrugDict:mDrugDicts){
            DrugDictModel drugDictModel = changeToModel(mDrugDict);
            drugDictModelList.add(drugDictModel);
        }
        Integer totalCount = getTotalCount(responseEntity);
        Envelop envelop = getResult(drugDictModelList,totalCount,page,size);

        return envelop;
    }

    @RequestMapping(value = "/dict/drug/icd10/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据drug的ID判断是否与ICD10字典存在关联。")
    public Envelop isUsage(
            @ApiParam(name = "id", value = "药品字典ID", defaultValue = "")
            @PathVariable(value = "id") String id){

        Envelop envelop = new Envelop();
        boolean result = drugDictClient.isUsage(id);
        envelop.setSuccessFlg(result);

        return envelop;
    }

    @ApiOperation(value = "判断提交的字典代码是否已经存在")
    @RequestMapping(value = "/dict/drug/existence/code/{code}" , method = RequestMethod.GET)
    public Envelop isCodeExist(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code){

        Envelop envelop = new Envelop();
        boolean result = drugDictClient.isCodeExist(code);
        envelop.setSuccessFlg(result);

        return envelop;
    }

    @RequestMapping(value = "/dict/drug/existence/name" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    public Envelop isNameExist(
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name){

        Envelop envelop = new Envelop();
        boolean result = drugDictClient.isNameExist(name);
        envelop.setSuccessFlg(result);

        return envelop;
    }

    /**
     *  将微服务返回的MDrugDict转化为前端DrugDictModel
     * @param mDrugDict
     * @return mDrugDict
     */
    private DrugDictModel changeToModel(MDrugDict mDrugDict) {
        DrugDictModel drugDictModel = convertToModel(mDrugDict, DrugDictModel.class);
        //获取字典标识（处方/非处方）值
        MConventionalDict flag = conDictEntryClient.getDrugFlag(mDrugDict.getFlag());
        drugDictModel.setFlagName(flag == null ? "" : flag.getValue());
        //获取字典类别
        MConventionalDict type = conDictEntryClient.getDrugType(mDrugDict.getType());
        drugDictModel.setTypeName(type == null ? "" : type.getValue());
        return drugDictModel;
    }
}