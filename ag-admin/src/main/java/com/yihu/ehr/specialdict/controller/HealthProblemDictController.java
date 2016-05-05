package com.yihu.ehr.specialdict.controller;

import com.yihu.ehr.agModel.specialdict.HealthProblemDictModel;
import com.yihu.ehr.agModel.specialdict.HpIcd10RelationModel;
import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.model.specialdict.MIcd10Dict;
import com.yihu.ehr.specialdict.service.HealthProblemDictClient;
import com.yihu.ehr.model.specialdict.MHealthProblemDict;
import com.yihu.ehr.model.specialdict.MHpIcd10Relation;
import com.yihu.ehr.specialdict.service.Icd10DictClient;
import com.yihu.ehr.util.Envelop;
import com.yihu.ehr.util.controller.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping(ApiVersion.Version1_0 + "/admin")
@Api(value = "DrugDict", description = "药品字典管理", tags = {"药品字典"})
public class HealthProblemDictController extends BaseController {

    @Autowired
    private HealthProblemDictClient hpDictClient;

    @Autowired
    private Icd10DictClient icd10DictClient;

    @RequestMapping(value = "/dict/hp", method = RequestMethod.POST)
    @ApiOperation(value = "创建新的健康问题字典" )
    public Envelop createHpDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson){

        MHealthProblemDict hpDict = hpDictClient.createHpDict(dictJson);
        HealthProblemDictModel hpDictModel = convertToModel(hpDict,HealthProblemDictModel.class);

        Envelop envelop = new Envelop();
        if(hpDictModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(hpDictModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("创建字典失败");
        }
        return envelop;
    }

    @RequestMapping(value = "dict/hp/{id}", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据id删除健康问题字典（含健康问题字典及与ICD10的关联关系。）")
    public Envelop deleteHpDict(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") String id) {
        Envelop envelop = new Envelop();
        boolean bo = hpDictClient.deleteHpDict(id);
        envelop.setSuccessFlg(bo);
        return envelop;
    }

    @RequestMapping(value = "dict/hps", method = RequestMethod.DELETE)
    @ApiOperation(value = "根据ids批量删除健康问题字典,多个以逗号隔开")
    public Envelop deleteHpDicts(
            @ApiParam(name = "ids", value = "字典IDs", defaultValue = "")
            @RequestParam(value = "ids") String ids) {
        Envelop envelop = new Envelop();
        boolean bo = hpDictClient.deleteHpDicts(ids);
        envelop.setSuccessFlg(bo);
        return envelop;
    }

    @RequestMapping(value = "/dict/hp", method = RequestMethod.PUT)
    @ApiOperation(value = "更新健康问题字典" )
    public Envelop updateHpDict(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson) {

        MHealthProblemDict hpDict = hpDictClient.updateHpDict(dictJson);
        HealthProblemDictModel hpDictModel = convertToModel(hpDict,HealthProblemDictModel.class);

        Envelop envelop = new Envelop();
        if(hpDictModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(hpDictModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("修改字典失败");
        }
        return envelop;
    }

    @RequestMapping(value = "/dict/hp/{id}", method = RequestMethod.GET)
    @ApiOperation(value = "根据ID获取相应的健康问题字典信息。" )
    public Envelop getHpDict(
            @ApiParam(name = "id", value = "字典ID", defaultValue = "")
            @PathVariable(value = "id") String id){

        MHealthProblemDict hpDict = hpDictClient.getHpDict(id);
        HealthProblemDictModel hpDictModel = convertToModel(hpDict,HealthProblemDictModel.class);

        Envelop envelop = new Envelop();
        if(hpDictModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(hpDictModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("获取字典失败");
        }
        return envelop;
    }

    @RequestMapping(value = "/dict/hps", method = RequestMethod.GET)
    @ApiOperation(value = "根据查询条件查询相应的ICD10字典信息。" )
    public Envelop getHpDictList(
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

        List<HealthProblemDictModel> hpDictModelList = new ArrayList<>();
        ResponseEntity<Collection<MHealthProblemDict>> responseEntity = hpDictClient.getHpDictList(fields, filters, sorts, size, page);
        Collection<MHealthProblemDict> mHealthProblemDicts  = responseEntity.getBody();
        Integer totalCount = getTotalCount(responseEntity);
        for (MHealthProblemDict mHpDict:mHealthProblemDicts){
            HealthProblemDictModel hpDictModel = convertToModel(mHpDict,HealthProblemDictModel.class);
            //获取关联的icd10字典名称
            Collection<MHpIcd10Relation> relations = hpDictClient.getHpIcd10RelationListWithoutPaging("hpId=" + hpDictModel.getId());
            String icd10Names = "";
            if(relations.size() != 0){
                String icd110Ids = "";
                for(MHpIcd10Relation relation:relations){
                    icd110Ids += relation.getIcd10Id()+",";
                }
                icd110Ids = icd110Ids.substring(0,icd110Ids.length()-1);
                ResponseEntity<Collection<MIcd10Dict>> response = icd10DictClient.getIcd10DictList("","id="+icd110Ids,"",999,1);
                Collection<MIcd10Dict> mIcd10Dicts = response.getBody();
                if (mIcd10Dicts.size() != 0){
                    for (MIcd10Dict dict :mIcd10Dicts){
                        icd10Names += dict.getName()+",";
                    }
                    icd10Names = icd10Names.substring(0,icd10Names.length()-1);
                }
            }
            hpDictModel.setIcd10Name(icd10Names);
            hpDictModelList.add(hpDictModel);
        }
        Envelop envelop = getResult(hpDictModelList,totalCount,page,size);

        return envelop;
    }

    @RequestMapping(value = "/dict/hp/existence/code/{code}" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典代码是否已经存在")
    public Envelop isCodeExist(
            @ApiParam(name = "code", value = "code", defaultValue = "")
            @PathVariable(value = "code") String code){

        Envelop envelop = new Envelop();
        boolean result = hpDictClient.isCodeExists(code);
        envelop.setSuccessFlg(result);

        return envelop;
    }

    @RequestMapping(value = "/dict/hp/existence/name" , method = RequestMethod.GET)
    @ApiOperation(value = "判断提交的字典名称是否已经存在")
    public Envelop isNameExist(
            @ApiParam(name = "name", value = "name", defaultValue = "")
            @RequestParam(value = "name") String name){

        Envelop envelop = new Envelop();
        boolean result = hpDictClient.isNameExists(name);
        envelop.setSuccessFlg(result);

        return envelop;
    }

    //-------------------------健康问题与ICD10之间关联关系管理---------------------------------------------------------

    @RequestMapping(value = "/dict/hp/icd10", method = RequestMethod.POST)
    @ApiOperation(value = "为健康问题增加ICD10疾病关联。" )
    public Envelop createHpIcd10Relation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson){

        MHpIcd10Relation hpIcd10Relation = hpDictClient.createHpIcd10Relation(dictJson);
        HpIcd10RelationModel hpIcd10RelationModel = convertToModel(hpIcd10Relation,HpIcd10RelationModel.class);

        Envelop envelop = new Envelop();
        if(hpIcd10RelationModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(hpIcd10RelationModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("创建字典关联失败");
        }
        return envelop;
    }

    @RequestMapping(value = "/dict/hp/icd10s", method = RequestMethod.POST)
    @ApiOperation(value = "为健康问题增加ICD10疾病关联,--批量增加关联。" )
    public Envelop createHpIcd10Relations(
            @ApiParam(name = "hp_id", value = "健康问题Id")
            @RequestParam(value = "hp_id") String hpId,
            @ApiParam(name = "icd10_ids", value = "关联的icd10字典ids,多个以逗号连接")
            @RequestParam(value = "icd10_ids") String icd10Ids,
            @ApiParam(name = "create_user",value = "创建者")
            @RequestParam(value = "create_user") String createUser){
        Envelop envelop = new Envelop();
        Collection<MHpIcd10Relation> mHpIcd10Relations = hpDictClient.createHpIcd10Relations(hpId, icd10Ids,createUser);
        List<HpIcd10RelationModel> hpIcd10RelationModels = (List<HpIcd10RelationModel>)convertToModels(mHpIcd10Relations,new ArrayList<>(),HpIcd10RelationModel.class,null);
        if(hpIcd10RelationModels.size() != 0){
            envelop.setSuccessFlg(true);
            envelop.setDetailModelList(hpIcd10RelationModels);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("批量关联icd10字典失败！");
        }
        return envelop;
    }

    @RequestMapping(value = "/dict/hp/icd10", method = RequestMethod.PUT)
    @ApiOperation(value = "为健康问题修改ICD10疾病关联。" )
    public Envelop updateHpIcd10Relation(
            @ApiParam(name = "dictionary", value = "字典JSON结构")
            @RequestParam(value = "dictionary") String dictJson){

        MHpIcd10Relation hpIcd10Relation = hpDictClient.updateHpIcd10Relation(dictJson);
        HpIcd10RelationModel hpIcd10RelationModel = convertToModel(hpIcd10Relation,HpIcd10RelationModel.class);

        Envelop envelop = new Envelop();
        if(hpIcd10RelationModel != null){
            envelop.setSuccessFlg(true);
            envelop.setObj(hpIcd10RelationModel);
        }else {
            envelop.setSuccessFlg(false);
            envelop.setErrorMsg("创建字典关联失败");
        }
        return envelop;
    }

    @RequestMapping(value = "/dict/hp/icd10", method = RequestMethod.DELETE)
    @ApiOperation(value = "为健康问题删除ICD10疾病关联。" )
    public Envelop deleteHpIcd10Relation(
            @ApiParam(name = "id", value = "关联ID", defaultValue = "")
            @RequestParam(value = "id", required = true) String id){

        Envelop envelop = new Envelop();
        boolean bo = hpDictClient.deleteHpIcd10Relation(id);
        envelop.setSuccessFlg(bo);
        return envelop;
    }

    @RequestMapping(value = "/dict/hp/icd10s", method = RequestMethod.DELETE)
    @ApiOperation(value = "为健康问题删除ICD10疾病关联---批量删除。" )
    public Envelop deleteHpIcd10Relations(
            @ApiParam(name = "ids", value = "关联IDs", defaultValue = "")
            @RequestParam(value = "ids", required = true) String ids){

        Envelop envelop = new Envelop();
        boolean bo = hpDictClient.deleteHpIcd10Relations(ids);
        envelop.setSuccessFlg(bo);
        return envelop;
    }

    @RequestMapping(value = "/dict/hp/icd10s", method = RequestMethod.GET)
    @ApiOperation(value = "根据健康问题查询相应的ICD10关联列表信息。" )
    public Envelop getHpIcd10RelationList(
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

        List<HpIcd10RelationModel> hpIcd10RelationModelList = new ArrayList<>();
        ResponseEntity<Collection<MHpIcd10Relation>> responseEntity = hpDictClient.getHpIcd10RelationList(fields, filters, sorts, size, page);
        Collection<MHpIcd10Relation> mHpIcd10Relations  = responseEntity.getBody();
        Integer totalCount = getTotalCount(responseEntity);
        for (MHpIcd10Relation mHpIcd10Relation:mHpIcd10Relations){
            HpIcd10RelationModel hpIcd10RelationModel = convertToModel(mHpIcd10Relation,HpIcd10RelationModel.class);
            hpIcd10RelationModelList.add(hpIcd10RelationModel);
        }
        Envelop envelop = getResult(hpIcd10RelationModelList,totalCount,page,size);
        return envelop;
    }

    @RequestMapping(value = "/dict/hp/icd10s/no_paging", method = RequestMethod.GET)
    @ApiOperation(value = "根据健康问题查询相应的ICD10关联列表信息.--不分页。" )
    public Envelop getHpIcd10RelationListWithoutPaging(
            @ApiParam(name = "filters", value = "过滤器，为空检索所有信息", defaultValue = "")
            @RequestParam(value = "filters", required = false) String filters){
        Envelop envelop = new Envelop();
        List<HpIcd10RelationModel> hpIcd10RelationModelList = new ArrayList<>();
        Collection<MHpIcd10Relation> mHpIcd10Relations = hpDictClient.getHpIcd10RelationListWithoutPaging(filters);
        for (MHpIcd10Relation mHpIcd10Relation:mHpIcd10Relations){
            HpIcd10RelationModel hpIcd10RelationModel = convertToModel(mHpIcd10Relation,HpIcd10RelationModel.class);
            hpIcd10RelationModelList.add(hpIcd10RelationModel);
        }
        envelop.setSuccessFlg(true);
        envelop.setDetailModelList(hpIcd10RelationModelList);
        return envelop;
    }

    @RequestMapping(value = "/dict/hp/icd10/existence" , method = RequestMethod.GET)
    @ApiOperation(value = "判断健康问题与ICD10的关联关系在系统中是否已存在")
    public Envelop isHpIcd10RelaExist(
            @ApiParam(name = "hpId", value = "健康问题内码")
            @RequestParam(value = "hpId", required = false) String hpId,
            @ApiParam(name = "icd10Id", value = "Icd10内码", defaultValue = "")
            @RequestParam(value = "icd10Id", required = false) String icd10Id){

        Envelop envelop = new Envelop();
        boolean result = hpDictClient.isHpIcd10RelaExist(hpId,icd10Id);
        envelop.setSuccessFlg(result);

        return envelop;
    }
}