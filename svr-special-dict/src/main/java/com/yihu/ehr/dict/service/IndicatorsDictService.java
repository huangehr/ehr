package com.yihu.ehr.dict.service;

import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


/**
 * 用户公私钥管理
 *
 * @author CWS
 * @version 1.0
 * @created 02-6月-2015 17:38:05
 */
@Transactional
@Service
public class IndicatorsDictService extends BaseJpaService<IndicatorsDict, XIndicatorsDictRepository> {

    @PersistenceContext
    protected EntityManager entityManager;
    @Autowired
    private XIndicatorsDictRepository indicatorsDictRepo;
    @Autowired
    private XIcd10IndicatorRelationRepository icd10IndicatorReRepo;

    //flag: 0 - create  1 - update
    public boolean isCodeExist(String id, String code, String flag){
        IndicatorsDict indicatorsDict = indicatorsDictRepo.findByCode(code);
        if(indicatorsDict != null){
            if(StringUtils.equals(flag,"1")){
                return (!indicatorsDict.getId().equals(id));
            }
            return true;
        }else{
            return false;
        }
    }
    public boolean isNameExist(String id, String name, String flag){
        IndicatorsDict indicatorsDict = indicatorsDictRepo.findByName(name);
        if(indicatorsDict != null){
            if(StringUtils.equals(flag,"1")){
                return (!indicatorsDict.getId().equals(id));
            }
            return true;
        }else{
            return false;
        }
    }

    public boolean isUsage(String id){
        boolean result = (icd10IndicatorReRepo.findByIndicatorId(id) != null);
        return result;
    }
}