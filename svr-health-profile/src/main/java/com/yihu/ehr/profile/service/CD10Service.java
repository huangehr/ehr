package com.yihu.ehr.profile.service;

import com.yihu.ehr.model.specialdict.*;
import com.yihu.ehr.profile.feign.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author linaz
 * @created 2016.05.28 13:49
 */
@Service
@Transactional
public class CD10Service {

    @Autowired
    private XDrugDictClient drugDictClient;

    @Autowired
    private XHealthProblemDictClient healthProblemDictClient;

    @Autowired
    private XHpIcd10RelationClient hpIcd10RelationClient;

    @Autowired
    private XIcd10DrugRelationClient icd10DrugRelationClient;

    @Autowired
    private XIcd10IndicatorRelationClient icd10IndicatorRelationClient;

    @Autowired
    private XIndicatorsDictClient indicatorsDictClient;

    @Autowired
    private XIcd10DictClient icd10DictClient;

    public List<MDrugDict> getDrugDictList(String problemCode){
        //获取健康问题id
        MHealthProblemDict healthProblemDict = healthProblemDictClient.getHpDictByCode(problemCode);
        String healthProblemDictId = healthProblemDict.getId();
        //根据健康问题id获取健康问题ICD10关系
        List<MIcd10HpRelation> hpIcd10Relations = hpIcd10RelationClient.getHpIcd10RelationByHpId(healthProblemDictId);
        //获取ICD10id列表
        List<String> icd10Ids = new ArrayList<>();
        for (MIcd10HpRelation hpIcd10Relation : hpIcd10Relations){
            icd10Ids.add(hpIcd10Relation.getIcd10Id());
        }
        //获取药品和ICD10关系
        List<MIcd10DrugRelation> icd10DrugRelations = icd10DrugRelationClient.getIcd10DrugRelationsByIcd10Ids(icd10Ids);
        //获取药品id列表
        List<String> drugIds = new ArrayList<>();
        for (MIcd10DrugRelation icd10DrugRelation : icd10DrugRelations){
            drugIds.add(icd10DrugRelation.getDrugId());
        }
        //根据药品id列表或药品列表信息
        List<MDrugDict> drugDicts = drugDictClient.getDrugDictByIds(drugIds);
        return drugDicts;
    }


    public List<MIndicatorsDict> getIndicatorsDictList(String problemCode) {
        //获取健康问题id
        MHealthProblemDict healthProblemDict = healthProblemDictClient.getHpDictByCode(problemCode);
        String healthProblemDictId = healthProblemDict.getId();
        //根据健康问题id获取健康问题ICD10关系
        List<MIcd10HpRelation> hpIcd10Relations = hpIcd10RelationClient.getHpIcd10RelationByHpId(healthProblemDictId);
        //获取ICD10id列表
        List<String> icd10Ids = new ArrayList<>();
        for (MIcd10HpRelation hpIcd10Relation : hpIcd10Relations){
            icd10Ids.add(hpIcd10Relation.getIcd10Id());
        }
        //获取指标和ICD10关系
        List<MIcd10IndicatorRelation> icd10IndicatorRelations = icd10IndicatorRelationClient.getIcd10IndicatorRelationsByIcd10Ids(icd10Ids);
        //获取指标id列表
        List<String> indicatorIds = new ArrayList<>();
        for (MIcd10IndicatorRelation icd10IndicatorRelation : icd10IndicatorRelations){
            indicatorIds.add(icd10IndicatorRelation.getIndicatorId());
        }
        //根据指标id列表或指标列表信息
        List<MIndicatorsDict> indicatorsDict = indicatorsDictClient.getIndicatorsDictByIds(indicatorIds);
        return indicatorsDict;
    }

    public List<MIcd10Dict> getIcd10DictList(String problemCode) {
        MHealthProblemDict healthProblemDict = healthProblemDictClient.getHpDictByCode(problemCode);
        String hpId = healthProblemDict.getId();
        //根据健康问题id获取健康问题ICD10关系列表
        List<MIcd10HpRelation> hpIcd10RelationList = hpIcd10RelationClient.getHpIcd10RelationByHpId(hpId);
        //获取ICD10字典id列表
        List<String> icd10Ids = new ArrayList<>();
        for (MIcd10HpRelation h: hpIcd10RelationList){
            icd10Ids.add(h.getIcd10Id());
        }
        return icd10DictClient.getIcd10DictListByIds(icd10Ids);
    }
}