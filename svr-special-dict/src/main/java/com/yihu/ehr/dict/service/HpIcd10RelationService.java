package com.yihu.ehr.dict.service;

import com.yihu.ehr.query.BaseJpaService;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public boolean isExist(String icd10Id,String hpId){
        HpIcd10Relation hpIcd10Relation = hpIcd10ReRepo.findByIcd10IdAndHpId(icd10Id,hpId);
        return hpIcd10Relation != null;
    }

    public List<HpIcd10Relation> getHpIcd10RelationListByHpId(String hpId){
        List<HpIcd10Relation> hpIcd10RelationList = hpIcd10ReRepo.findByHpId(hpId);
        if(hpIcd10RelationList.size() == 0){
            return null;
        }
        return hpIcd10RelationList;
    }

    public Page<HpIcd10Relation> getRelationList(String sorts, int page, int size) {
        Pageable pageable = new PageRequest(page, size, parseSorts(sorts));
        return hpIcd10ReRepo.findAll(pageable);
    }

    public boolean isUsage(String icd10Id){
        List<HpIcd10Relation> hpIcd10RelationList = hpIcd10ReRepo.findByIcd10Id(icd10Id);
        if(hpIcd10RelationList.size() == 0){
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
        String hql = "DELETE FROM HpIcd10Relation WHERE id in (:ids)";
        Query query = currentSession().createQuery(hql);
        query.setParameterList("ids", ids);
        return query.executeUpdate();
    }

}