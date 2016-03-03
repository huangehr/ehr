package com.yihu.ehr.dict.service;

import com.yihu.ehr.query.BaseJpaService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


/**
 * 用户公私钥管理
 *
 * @author CWS
 * @version 1.0
 * @created 02-6月-2015 17:38:05
 */
@Transactional
@Service
public class HpIcd10RelationService extends BaseJpaService<HpIcd10Relation, XHpIcd10RelationRepository> {

    @PersistenceContext
    protected EntityManager entityManager;
    @Autowired
    private XHpIcd10RelationRepository hpIcd10ReRepo;

    //flag: 0 - create  1 - update
    public boolean isExist(String id, String icd10Id,String hpId, String flag){
        HpIcd10Relation hpIcd10Relation = hpIcd10ReRepo.findByIcd10IdAndHpId(icd10Id,hpId);
        if(hpIcd10Relation != null){
            if(StringUtils.equals(flag,"1")){
                return (!hpIcd10Relation.getId().equals(id));
            }
            return true;
        }else{
            return false;
        }
    }

    public List<HpIcd10Relation> getHpIcd10RelationListByHpId(String hpId){

        List<HpIcd10Relation> hpIcd10RelationList = hpIcd10ReRepo.findByHpId(hpId);
        if(hpIcd10RelationList.size() == 0){
            return null;
        }
        return hpIcd10RelationList;
    }

    public boolean isUsage(String icd10Id){
        List<HpIcd10Relation> hpIcd10RelationList = hpIcd10ReRepo.findByIcd10Id(icd10Id);
        if(hpIcd10RelationList.size() == 0){
            return false;
        }
        return true;
    }

}