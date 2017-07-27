package com.yihu.ehr.profile.feign;

import com.yihu.ehr.constants.ApiVersion;
import com.yihu.ehr.constants.MicroServices;
import com.yihu.ehr.model.dict.MConventionalDict;
import com.yihu.ehr.model.specialdict.MDrugDict;
import com.yihu.ehr.model.specialdict.MHealthProblemDict;
import com.yihu.ehr.model.specialdict.MIcd10Dict;
import com.yihu.ehr.model.specialdict.MIndicatorsDict;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author hzp
 * @version 1.0
 * @created 2016.06.27 14:58
 * 字典服务
 */
@ApiIgnore
@RequestMapping(ApiVersion.Version1_0)
@FeignClient(value = MicroServices.Dictionary)
public interface XDictClient {

    /**************************** 系统字典 ********************************************/
    /**
     * 获取档案数据来源的字典
     * @return
     */
    @RequestMapping(value = "/dictionaries/record_data_sources", method = RequestMethod.GET)
    public List<MConventionalDict> getRecordDataSourceList();

    /**************************** 健康问题 **********************************************/
    //根据健康问题代码获取指标信息
    @RequestMapping(value = "/dict/hp/{code}/indicators" , method = RequestMethod.GET)
    List<MIndicatorsDict> getIndicatorsByHpCode(@RequestParam(value = "code") String code);

    //根据健康问题代码获取药品信息
    @RequestMapping(value = "/dict/hp/{code}/drug" , method = RequestMethod.GET)
    List<MDrugDict> getDrugDictListByHpCode(@RequestParam(value = "code") String code);

    //根据健康问题代码获取ICD10代码列表
    @RequestMapping(value = "/dict/hp/{code}/icd10" , method = RequestMethod.GET)
    List<MIcd10Dict> getIcd10ByHpCode(@RequestParam(value = "code") String code);

    /**
     * Redis通过疾病ID获取健康问题
     *//*
    @RequestMapping(value = "/hp_icd10_relation_cache/one" , method = RequestMethod.GET)
    public MHealthProblemDict getHealthProblemByIcd10(@RequestParam(value = "icd10_id") String icd10Id);

    *//**
     * Redis通过疾病ID获取健康问题
     *//*
    @RequestMapping(value = "/hp_icd10_relation_cache/list" , method = RequestMethod.POST)
    public List<MHealthProblemDict> getHealthProblemListByIcd10List(@RequestParam(value = "icd10_idList",required = true) List<String> icd10_idList);

    *//**************************** ICD10疾病 *******************************************//*
    *//*
     * 通过Code获取单个疾病字典
     *//*
    @RequestMapping(value = "/dict/icd10/code/{code}", method = RequestMethod.GET)
    public MIcd10Dict getIcd10ByCode(@RequestParam(value = "code") String code);

    *//*
    * 通过CodeList获取疾病字典
    *//*
    @RequestMapping(value = "/dict/icd10/codeList", method = RequestMethod.POST)
    public List<MIcd10Dict> getIcd10ByCodeList(@RequestParam(value = "codeList") List<String> codeList);

    *//**
     * 根据id列表获取ICD10字典信息
     *//*
    @RequestMapping(value = "/dict/icd10/ids" , method = RequestMethod.GET)
    public List<MIcd10Dict> getIcd10DictListByIds(@RequestParam(value = "ids") List<String> ids);*/

    /************************** 药品 **************************************/
    /**
     * 根据ID列表获取相应的药品字典信息
     */
    @RequestMapping(value = "/dict/drugs/ids", method = RequestMethod.GET)
    List<MDrugDict> getDrugDictByIds(@RequestParam(value = "ids") List<String> ids);



    /****************** 指标 *******************************/
    //根据ids获取相应的指标字典信息
    @RequestMapping(value = "/dict/indicators/ids", method = RequestMethod.GET)
    List<MIndicatorsDict> getIndicatorsDictByIds(@RequestParam(value = "ids") List<String> ids);

    //根据Code获取相应的指标字典信息
    @RequestMapping(value = "/dict/indicators/code", method = RequestMethod.GET)
    MIndicatorsDict getIndicatorsDictByCode(@RequestParam(value = "code") String code);

    //根据CodeList获取相应的指标字典信息
    @RequestMapping(value = "/dict/indicators/codeList", method = RequestMethod.POST)
    List<MIndicatorsDict> getIndicatorsDictByCodeList(@RequestParam(value = "codeList",required = true) List<String> codeList);




}
