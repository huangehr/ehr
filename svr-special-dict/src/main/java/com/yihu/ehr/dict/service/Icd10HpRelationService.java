package com.yihu.ehr.dict.service;

import com.yihu.ehr.dict.model.Icd10HpRelation;
import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class Icd10HpRelationService extends BaseJpaService<Icd10HpRelation, XIcd10HpRelationRepository> {

    @Autowired
    private XIcd10HpRelationRepository Icd10hpReRepo;

    public boolean isExist(String icd10Id,String hpId){
        Icd10HpRelation icd10HpRelation = Icd10hpReRepo.findByIcd10IdAndHpId(icd10Id,hpId);
        return icd10HpRelation != null;
    }

    public List<Icd10HpRelation> getHpIcd10RelationListByHpId(String hpId){
        List<Icd10HpRelation> icd10HpRelationList = Icd10hpReRepo.findByHpId(hpId);
        if(icd10HpRelationList.size() == 0){
            return null;
        }
        return icd10HpRelationList;
    }

    public Page<Icd10HpRelation> getRelationList(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return Icd10hpReRepo.findAll(pageable);
    }

    public boolean isUsage(String icd10Id){
        List<Icd10HpRelation> icd10HpRelationList = Icd10hpReRepo.findByIcd10Id(icd10Id);
        if(icd10HpRelationList.size() == 0){
            return false;
        }
        return true;
    }

    /**
     * 根据ID删除标准来源
     *
     * @param ids
     */
    public int delete(String[] ids) {
        String hql = "DELETE FROM Icd10HpRelation WHERE id in (:ids)";
        Query query = currentSession().createQuery(hql);
        query.setParameterList("ids", ids);
        return query.executeUpdate();
    }

    public List<Icd10HpRelation> getHpIcd10RelationByHpId(String hpId) {
        return Icd10hpReRepo.findByHpId(hpId);
    }
}